package com.hogiabao7725.hotelbooking.mapper;

import com.hogiabao7725.hotelbooking.dto.request.auth.RegisterRequest;
import com.hogiabao7725.hotelbooking.dto.response.auth.RegisterResponse;
import com.hogiabao7725.hotelbooking.entity.Account;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AccountMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "profile", ignore = true)
    Account toEntity(RegisterRequest request);

    @Mapping(source = "id", target = "accountId")
    @Mapping(source = "profile.fullName", target = "fullName")
    @Mapping(source = "role.name", target = "role")
    RegisterResponse toResponse(Account account);

}
