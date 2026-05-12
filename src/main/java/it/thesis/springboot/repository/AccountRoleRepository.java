package it.thesis.springboot.repository;

import it.thesis.springboot.model.AccountRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRoleRepository extends JpaRepository<AccountRole, Long> {
    @Modifying
    @Query("DELETE FROM AccountRole a WHERE a.account.id = :accountId")
    void deleteByAccountId(@Param("accountId") Long accountId);
}