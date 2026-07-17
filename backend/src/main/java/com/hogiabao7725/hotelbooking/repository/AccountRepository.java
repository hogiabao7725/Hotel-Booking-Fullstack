package com.hogiabao7725.hotelbooking.repository;

import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByEmail(String email);

    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailAndStatus(String email, AccountStatus status);
}
