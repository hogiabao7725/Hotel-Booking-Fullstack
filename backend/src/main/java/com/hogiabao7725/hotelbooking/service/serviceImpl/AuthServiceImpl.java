package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.dto.request.auth.LoginRequest;
import com.hogiabao7725.hotelbooking.dto.request.auth.RefreshRequest;
import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.AuthResponse;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;
import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.entity.Profile;
import com.hogiabao7725.hotelbooking.entity.Role;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import com.hogiabao7725.hotelbooking.enums.UserRole;
import com.hogiabao7725.hotelbooking.exception.*;
import com.hogiabao7725.hotelbooking.mapper.AccountMapper;
import com.hogiabao7725.hotelbooking.mapper.ProfileMapper;
import com.hogiabao7725.hotelbooking.repository.AccountRepository;
import com.hogiabao7725.hotelbooking.repository.RoleRepository;
import com.hogiabao7725.hotelbooking.security.jwt.JwtTokenProvider;
import com.hogiabao7725.hotelbooking.service.AuthService;
import com.hogiabao7725.hotelbooking.service.EmailService;
import com.hogiabao7725.hotelbooking.service.OneTimeTokenService;
import com.hogiabao7725.hotelbooking.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final RefreshTokenService refreshTokenService;
    private final OneTimeTokenService emailVerificationTokenService;
    private final EmailService emailService;

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;

    private final AccountMapper accountMapper;
    private final ProfileMapper profileMapper;

    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        Optional<Account> existingAccountOpt = accountRepository.findByEmail(request.email());
        Account account;

        if (existingAccountOpt.isPresent()) {
            account = existingAccountOpt.get();

            // Reject registration if email is already active or banned
            // Only for inactive account if account present
            if (account.getStatus() != AccountStatus.INACTIVE) {
                throw new AppException(ErrorCode.ACCOUNT_EMAIL_ALREADY_EXISTS);
            }

            // Update existing unverified account
            account.setPasswordHash(passwordEncoder.encode(request.password()));
            Profile profile = account.getProfile();
            profile.setFullName(request.fullName());
        } else {
            // Create a new account
            Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_ROLE_NOT_FOUND));

            account = accountMapper.toEntity(request);
            account.setPasswordHash(passwordEncoder.encode(request.password()));
            account.setRole(customerRole);
            account.setStatus(AccountStatus.INACTIVE);

            Profile profile = profileMapper.toEntity(request);
            account.setProfile(profile);
        }
        account = accountRepository.save(account);

        // Generate verification token and send email
        String token = emailVerificationTokenService.createToken(account.getEmail());
        emailService.sendVerification(account.getEmail(), account.getProfile().getFullName(), token);

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // access token
        String accessToken = jwtTokenProvider.generateToken(authentication);
        // refresh token
        String refreshToken = refreshTokenService.create(authentication.getName());
        return new AuthResponse(accessToken,"Bearer", refreshToken);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        String email = emailVerificationTokenService.consumeToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_INVALID_ONE_TIME_TOKEN));
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_INVALID_ONE_TIME_TOKEN));

        switch (account.getStatus()) {
            // White list
            case INACTIVE -> {
                account.setStatus(AccountStatus.ACTIVE);
                accountRepository.save(account);
            }
            // Black list
            case ACTIVE -> throw new AppException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
            case BANNED -> throw new AppException(ErrorCode.ACCOUNT_BANNED);
            default -> throw new AppException(ErrorCode.AUTH_INVALID_ONE_TIME_TOKEN);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void resendVerification(String email) { // Only for register feature
        accountRepository.findByEmailAndStatus(email, AccountStatus.INACTIVE)
                .ifPresent(account -> {
                    String token = emailVerificationTokenService.createToken(account.getEmail());

                    emailService.sendVerification(
                            account.getEmail(),
                            account.getProfile().getFullName(),
                            token
                    );
                });
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponse refreshToken(RefreshRequest request) {
        String email = refreshTokenService.getPayload(request.refreshToken())
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_REFRESH_TOKEN_INVALID));

        refreshTokenService.revoke(request.refreshToken());

        UserDetails userDetails = userDetailsService.loadUserByUsername(email);
        if (!userDetails.isEnabled()) {
            throw new AppException(ErrorCode.ACCOUNT_INACTIVE);
        }
        if (!userDetails.isAccountNonLocked()) {
            throw new AppException(ErrorCode.ACCOUNT_BANNED);
        }

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities()
        );
        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = refreshTokenService.create(email);
        return new  AuthResponse(newAccessToken, "Bearer", newRefreshToken);
    }
}
