package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.entity.Role;
import com.hogiabao7725.hotelbooking.enums.UserRole;
import com.hogiabao7725.hotelbooking.exception.AppException;
import com.hogiabao7725.hotelbooking.exception.ErrorCode;
import com.hogiabao7725.hotelbooking.repository.RoleRepository;
import com.hogiabao7725.hotelbooking.service.RoleService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    public Role getByName(UserRole name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new AppException(ErrorCode.ACCOUNT_ROLE_NOT_FOUND));
    }
}
