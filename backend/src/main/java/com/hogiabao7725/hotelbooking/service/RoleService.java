package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.entity.Role;
import com.hogiabao7725.hotelbooking.enums.UserRole;
import com.hogiabao7725.hotelbooking.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface RoleService {

    Role getByName(UserRole name);
}
