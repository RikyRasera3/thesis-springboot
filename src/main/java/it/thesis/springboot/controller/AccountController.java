package it.thesis.springboot.controller;

import it.thesis.springboot.dto.CreateAccountDto;
import it.thesis.springboot.dto.UpdateAccountDto;
import it.thesis.springboot.dto.criteria.SearchAccountCriteria;
import it.thesis.springboot.model.Account;
import it.thesis.springboot.service.AccountService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static it.thesis.springboot.controller.Path.ACCOUNT_PATH;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(value = ACCOUNT_PATH, produces = MediaType.APPLICATION_JSON_VALUE)
public class AccountController {
    private final AccountService accountService;

    @GetMapping("/search")
    public ResponseEntity<PagedModel<Account>> searchAccounts(@PageableDefault Pageable pageable, @Validated SearchAccountCriteria criteria) {
        log.info("Received request to search accounts with pagination");
        Page<Account> accounts = accountService.findAll(pageable, criteria);
        return ResponseEntity.ok(new PagedModel<>(accounts));
    }

    @GetMapping
    public ResponseEntity<List<Account>> getAccounts(@Validated SearchAccountCriteria criteria) {
        log.info("Received request to search accounts");
        List<Account> accounts = accountService.findAll(criteria);
        return ResponseEntity.ok(accounts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Account> getAccountById(@PathVariable Long id) {
        log.info("Received request to search account with id {}", id);
        Optional<Account> account = accountService.findById(id);
        return ResponseEntity.ok(account.orElse(null));
    }

    @PostMapping
    public ResponseEntity<Account> createAccount(@RequestBody @Validated CreateAccountDto dto) {
        log.info("Received request to create account");
        Account account = accountService.createAccount(dto);

        URI location = ServletUriComponentsBuilder.fromCurrentServletMapping()
                .path(ACCOUNT_PATH + "/{id}")
                .buildAndExpand(account.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(account);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Void> updateAccount(@PathVariable Long id, @RequestBody @Validated UpdateAccountDto dto) {
        log.info("Received request to update account with id {}", id);
        accountService.updateAccount(id, dto);
        return ResponseEntity.ok(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAccount(@PathVariable Long id) {
        log.info("Received request to delete account with id {}", id);
        accountService.deleteAccount(id);

        return ResponseEntity.noContent()
                .build();
    }
}