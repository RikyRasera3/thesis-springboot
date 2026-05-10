package it.thesis.springboot.factory;

import it.thesis.springboot.model.Account;
import it.thesis.springboot.model.AccountRole;
import it.thesis.springboot.model.Role;

public class AccountRoleFactory {
    public static AccountRole createAccountRole(Account account, Role role) {
        var accountRole = new AccountRole();
        accountRole.setAccount(account);
        accountRole.setRole(role);
        return accountRole;
    }
}