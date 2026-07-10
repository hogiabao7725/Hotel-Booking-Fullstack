package com.hogiabao7725.hotelbooking.converter;

import com.hogiabao7725.hotelbooking.enums.AccountStatus;
import jakarta.persistence.Converter;

@Converter
public class AccountStatusConverter extends BaseEnumConverter<AccountStatus> {

    public AccountStatusConverter() {
        super(AccountStatus.class);
    }

}
