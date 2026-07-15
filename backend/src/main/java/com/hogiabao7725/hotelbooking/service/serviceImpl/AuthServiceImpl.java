package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.config.properties.VerifyEmailProperties;
import com.hogiabao7725.hotelbooking.dto.request.auth.LoginRequest;
import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.LoginResponse;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;
import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.entity.Profile;
import com.hogiabao7725.hotelbooking.entity.Role;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import com.hogiabao7725.hotelbooking.enums.UserRole;
import com.hogiabao7725.hotelbooking.exception.BusinessException;
import com.hogiabao7725.hotelbooking.exception.ConflictException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.exception.ResourceNotFoundException;
import com.hogiabao7725.hotelbooking.mapper.AccountMapper;
import com.hogiabao7725.hotelbooking.mapper.ProfileMapper;
import com.hogiabao7725.hotelbooking.repository.AccountRepository;
import com.hogiabao7725.hotelbooking.repository.RoleRepository;
import com.hogiabao7725.hotelbooking.security.jwt.JwtTokenProvider;
import com.hogiabao7725.hotelbooking.service.AuthService;
import com.hogiabao7725.hotelbooking.service.EmailService;
import com.hogiabao7725.hotelbooking.service.OneTimeTokenService;
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
    private final VerifyEmailProperties verifyEmailProperties;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional
    public RegisterResponse register(RegisterRequest request) {
        Optional<Account> existingAccountOpt = accountRepository.findByEmail(request.email());
        Account account;

        if (existingAccountOpt.isPresent()) {
            account = existingAccountOpt.get();

            // Reject registration if email is already verified
            if (account.getStatus() == AccountStatus.ACTIVE) {
                throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS);
            }

            // Update existing unverified account
            account.setPasswordHash(passwordEncoder.encode(request.password()));
            Profile profile = account.getProfile();
            profile.setFullName(request.fullName());
        } else {
            // Create a new account
            Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                    .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

            account = accountMapper.toEntity(request);
            account.setPasswordHash(passwordEncoder.encode(request.password()));
            account.setRole(customerRole);
            account.setStatus(AccountStatus.INACTIVE);

            Profile profile = profileMapper.toEntity(request);
            account.setProfile(profile);
        }

        account = accountRepository.save(account);

        // Generate verification token and send email
        String token = oneTimeTokenService.createToken(
                account.getEmail(),
                verifyEmailProperties.prefix(),
                verifyEmailProperties.expiration());

        emailService.sendVerification(account.getEmail(), account.getProfile().getFullName(), token);

        return accountMapper.toResponse(account);
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String accessToken = jwtTokenProvider.generateToken(authentication);

        return new LoginResponse(accessToken, "Bear");
    }

    @Override
    @Transactional
    public void verifyEmail(String token) {
        String email = oneTimeTokenService.consumeToken(token, verifyEmailProperties.prefix());
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "email", email));
        account.setStatus(AccountStatus.ACTIVE);
        accountRepository.save(account);
    }

    @Override
    @Transactional(readOnly = true)
    public void resendVerification(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Account", "email", email));
        if (account.getStatus() == AccountStatus.ACTIVE) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        String token = oneTimeTokenService.createToken(
                account.getEmail(),
                verifyEmailProperties.prefix(),
                verifyEmailProperties.expiration()
        );
        emailService.sendVerification(account.getEmail(), account.getProfile().getFullName(), token);
    }
}
