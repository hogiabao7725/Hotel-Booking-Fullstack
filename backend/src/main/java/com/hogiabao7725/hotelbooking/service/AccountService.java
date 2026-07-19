package com.hogiabao7725.hotelbooking.service;

import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;

import java.util.Optional;

public interface AccountService {

    Optional<Account> findByEmail(String email);

    Optional<Account> findByEmailAndStatus(String email, AccountStatus status);

    Account save(Account account);
}
