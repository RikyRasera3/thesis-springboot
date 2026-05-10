package it.thesis.springboot.service;

import it.thesis.springboot.model.Account;
import it.thesis.springboot.model.AccountRole;
import it.thesis.springboot.model.Role;
import it.thesis.springboot.repository.AccountRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static it.thesis.springboot.factory.AccountRoleFactory.createAccountRole;

@Service
@RequiredArgsConstructor
public class AccountRoleService {
    private final AccountRoleRepository repository;

    public List<AccountRole> insert(Account account, List<Role> roles) {
        List<AccountRole> accountRoles = roles.stream()
                .map(role -> createAccountRole(account, role))
                .toList();

        return repository.saveAll(accountRoles);
    }

    public void deleteByAccountId(Long accountId) {
        repository.deleteByAccountId(accountId);
    }
}