package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.dto.request.auth.LoginRequest;
import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.LoginResult;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final ProfileMapper profileMapper;

    private final OneTimeTokenService oneTimeTokenService;
    private final EmailService emailService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenService refreshTokenService;

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
        String token = oneTimeTokenService.createToken(account.getEmail());
        emailService.sendVerification(account.getEmail(), account.getProfile().getFullName(), token);

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResult login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );
        SecurityContextHolder.getContext().setAuthentication(authentication);
        // access token
        String accessToken = jwtTokenProvider.generateToken(authentication);
        // refresh token
        String refreshToken = refreshTokenService.create(authentication.getName());
        return new LoginResult(accessToken, refreshToken);
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        String email = oneTimeTokenService.consumeToken(token)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_INVALID_ONE_TIME_TOKEN));
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new AppException(ErrorCode.AUTH_INVALID_ONE_TIME_TOKEN));

        // White list
        if (account.getStatus() == AccountStatus.INACTIVE) {
            account.setStatus(AccountStatus.ACTIVE);
            accountRepository.save(account);
            return;
        }

        // Black list
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new AppException(ErrorCode.ACCOUNT_ALREADY_ACTIVE);
        }
        if (account.getStatus() == AccountStatus.BANNED) {
            throw new AppException(ErrorCode.ACCOUNT_BANNED);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public void resendVerification(String email) { // Only for register feature
        accountRepository.findByEmailAndStatus(email, AccountStatus.INACTIVE)
                .ifPresent(account -> {
                    String token = oneTimeTokenService.createToken(account.getEmail());

                    emailService.sendVerification(
                            account.getEmail(),
                            account.getProfile().getFullName(),
                            token
                    );
                });
    }
}
