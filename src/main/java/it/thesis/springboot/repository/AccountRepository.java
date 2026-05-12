package it.thesis.springboot.repository;

import it.thesis.springboot.model.Account;
import org.jspecify.annotations.NonNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, JpaSpecificationExecutor<Account> {
    @NonNull
    @Override
    @EntityGraph(attributePaths = {"accountRoles", "accountRoles.role"})
    List<Account> findAll(@NonNull Specification<Account> spec);

    @NonNull
    @Override
    Page<Account> findAll(@NonNull Specification<Account> spec, @NonNull Pageable pageable);

    @Query("SELECT a FROM Account a WHERE a.id IN :ids")
    @EntityGraph(attributePaths = {"accountRoles", "accountRoles.role"})
    List<Account> findAllByIdWithRoles(@Param("ids") List<Long> ids);

    @NonNull
    @Override
    Optional<Account> findById(@NonNull Long id);

    @Query("SELECT a FROM Account a WHERE a.id = :id")
    Optional<Account> findByIdWithoutRoles(@NonNull @Param("id") Long id);

    @Query("SELECT a FROM Account a WHERE a.id = :id")
    @EntityGraph(attributePaths = {"accountRoles", "accountRoles.role"})
    Optional<Account> findByIdWithRoles(@NonNull @Param("id") Long id);
}