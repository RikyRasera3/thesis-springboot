package it.thesis.springboot.repository;

import it.thesis.springboot.model.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    void deleteByAccountId(Long accountId);
}