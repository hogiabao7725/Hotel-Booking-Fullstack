package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.entity.Role;
import com.hogiabao7725.hotelbooking.enums.UserRole;
import org.springframework.stereotype.Service;

@Service
public interface RoleService {

    Role getByName(UserRole name);
}
