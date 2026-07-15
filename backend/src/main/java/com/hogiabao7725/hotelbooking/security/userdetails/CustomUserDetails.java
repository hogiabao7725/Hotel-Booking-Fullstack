package com.hogiabao7725.hotelbooking.security.userdetails;

import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@RequiredArgsConstructor
public class CustomUserDetails implements UserDetails {

    private final Account account;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = account.getRole().getName().toString();
        return List.of(new SimpleGrantedAuthority(roleName));
    }

    @Override
    public @Nullable String getPassword() {
        return account.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return account.getEmail();
    }

    @Override
    public boolean isAccountNonLocked() {
        return account.getStatus() != AccountStatus.BANNED;
    }

    @Override
    public boolean isEnabled() {
        return account.getStatus() == AccountStatus.ACTIVE;
    }
}
