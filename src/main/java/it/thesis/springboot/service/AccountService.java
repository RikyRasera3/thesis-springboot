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
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

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
        account = repository.save(account);

        accountRoleService.insert(account, roles);

        return account;
    }

    @Transactional
    public void updateAccount(Long id, UpdateAccountDto dto) {
        Account account = findByIdWithoutRolesOrError(id);

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
        findByIdWithoutRolesOrError(id);
        accountRoleService.deleteByAccountId(id);
        repository.deleteById(id);
    }

    public Page<Account> findAll(Pageable pageable, SearchAccountCriteria criteria) {
        Page<Account> page = repository.findAll(specification.getFilters(criteria), pageable);

        if (page.isEmpty()) {
            return page;
        }

        List<Long> ids = page.getContent().stream().map(Account::getId).toList();

        Map<Long, Account> accountsWithRoles = repository.findAllByIdWithRoles(ids)
                .stream()
                .collect(Collectors.toMap(Account::getId, a -> a));

        List<Account> enrichedContent = page.getContent()
                .stream()
                .map(a -> accountsWithRoles.getOrDefault(a.getId(), a))
                .toList();

        return new PageImpl<>(enrichedContent, pageable, page.getTotalElements());
    }

    public List<Account> findAll(SearchAccountCriteria criteria) {
        return repository.findAll(specification.getFilters(criteria));
    }

    public Optional<Account> findById(Long id) {
        return repository.findByIdWithRoles(id);
    }

    public Optional<Account> findByIdWithoutRoles(Long id) {
        return repository.findByIdWithoutRoles(id);
    }

    public Account findByIdWithoutRolesOrError(Long id) {
        return findByIdWithoutRoles(id).orElseThrow(() -> new IllegalArgumentException("Account with id " + id + " not found"));
    }
}