package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;
import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.entity.Profile;
import com.hogiabao7725.hotelbooking.entity.Role;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import com.hogiabao7725.hotelbooking.enums.UserRole;
import com.hogiabao7725.hotelbooking.exception.BusinessException;
import com.hogiabao7725.hotelbooking.exception.ConflictException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.mapper.AccountMapper;
import com.hogiabao7725.hotelbooking.mapper.ProfileMapper;
import com.hogiabao7725.hotelbooking.repository.AccountRepository;
import com.hogiabao7725.hotelbooking.repository.RoleRepository;
import com.hogiabao7725.hotelbooking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final AccountRepository accountRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccountMapper accountMapper;
    private final ProfileMapper profileMapper;

    @Override
    public RegisterResponse register(RegisterRequest request) {
        if (accountRepository.existsByEmail(request.email())) {
            throw new ConflictException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }
        Role customerRole = roleRepository.findByName(UserRole.ROLE_CUSTOMER)
                .orElseThrow(() -> new BusinessException(ErrorCode.ROLE_NOT_FOUND));

        Account account = accountMapper.toEntity(request);
        account.setPasswordHash(passwordEncoder.encode(request.password()));
        account.setRole(customerRole);
        account.setStatus(AccountStatus.INACTIVE);

        Profile profile = profileMapper.toEntity(request);
        account.setProfile(profile);

        account = accountRepository.save(account);
        return accountMapper.toResponse(account);
    }
}
