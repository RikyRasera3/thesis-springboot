package it.thesis.springboot.service;

import it.thesis.springboot.dto.CreateAccountDto;
import it.thesis.springboot.dto.UpdateAccountDto;
import it.thesis.springboot.dto.criteria.SearchAccountCriteria;
import it.thesis.springboot.factory.AccountFactory;
import it.thesis.springboot.model.Account;
import it.thesis.springboot.model.Role;
import it.thesis.springboot.repository.AccountRepository;
import it.thesis.springboot.service.specification.AccountSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AccountService {
    private final AccountRepository repository;

    private final AccountSpecification specification;

    private final AccountRoleService accountRoleService;
    private final RoleService roleService;

    @Transactional
    public Account createAccount(CreateAccountDto dto) {
        List<Role> roles = roleService.findAllByRoleIds(dto.getRoleIds());

        Account account = AccountFactory.updateAccount(dto);
        repository.save(account);

        accountRoleService.insert(account, roles);

        return account;
    }

    @Transactional
    public void updateAccount(Long id, UpdateAccountDto dto) {
        Account account = findByIdOrError(id);

        if(dto.getRoleIds().isPresent()) {
            List<Role> roles = roleService.findAllByRoleIds(dto.getRoleIds().get());
            accountRoleService.deleteByAccountId(id);
            accountRoleService.insert(account, roles);
        }

        AccountFactory.updateAccount(account, dto);
        repository.save(account);
    }

    @Transactional
    public void deleteAccount(Long id) {
        findByIdOrError(id);
        accountRoleService.deleteByAccountId(id);
        repository.deleteById(id);
    }

    public Page<Account> findAll(Pageable pageable, SearchAccountCriteria criteria) {
        return repository.findAll(specification.getFilters(criteria), pageable);
    }

    public List<Account> findAll(SearchAccountCriteria criteria) {
        return repository.findAll(specification.getFilters(criteria));
    }

    public Optional<Account> findById(Long id) {
        return repository.findByIdWithRoles(id);
    }

    public Account findByIdOrError(Long id) {
        return findById(id).orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " not found"));
    }
}