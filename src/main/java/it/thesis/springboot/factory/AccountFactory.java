package it.thesis.springboot.factory;

import it.thesis.springboot.dto.CreateAccountDto;
import it.thesis.springboot.dto.UpdateAccountDto;
import it.thesis.springboot.model.Account;
import it.thesis.springboot.util.OptionalUtils;

public class AccountFactory {
    public static Account updateAccount(CreateAccountDto dto) {
        var account = new Account();
        account.setName(dto.getName());
        account.setSurname(dto.getSurname());
        account.setEmail(dto.getEmail());
        account.setPhone(dto.getPhone());
        account.setDateOfBirth(dto.getDateOfBirth());
        return account;
    }

    public static void updateAccount(Account account, UpdateAccountDto dto) {
        OptionalUtils.getOptionalValue(dto.getName(), account::setName);
        OptionalUtils.getOptionalValue(dto.getSurname(), account::setSurname);
        OptionalUtils.getOptionalValue(dto.getEmail(), account::setEmail);
        OptionalUtils.getOptionalValue(dto.getPhone(), account::setPhone);
        OptionalUtils.getOptionalValue(dto.getDateOfBirth(), account::setDateOfBirth);
    }
}