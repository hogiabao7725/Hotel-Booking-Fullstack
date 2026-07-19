package com.hogiabao7725.hotelbooking.service.serviceImpl;

import com.hogiabao7725.hotelbooking.entity.Account;
import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import com.hogiabao7725.hotelbooking.repository.AccountRepository;
import com.hogiabao7725.hotelbooking.service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Override
    public Optional<Account> findByEmail(String email) {
        return accountRepository.findByEmail(email);
    }

    @Override
    public Optional<Account> findByEmailAndStatus(String email, AccountStatus status) {
        return accountRepository.findByEmailAndStatus(email, status);
    }

    @Override
    public Account save(Account account) {
        return accountRepository.save(account);
    }
}
