package it.thesis.springboot.service.specification;

import it.thesis.springboot.dto.criteria.SearchAccountCriteria;
import it.thesis.springboot.model.Account;
import it.thesis.springboot.model.AccountRole;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;

@Component
public class AccountSpecification {
    public Specification<Account> getFilters(SearchAccountCriteria criteria) {
        return (from, query, builder) -> {
            var predicates = new ArrayList<Predicate>();

            if (!CollectionUtils.isEmpty(criteria.getRoleIds())) {
                Join<Account, AccountRole> accountRoleJoin = from.join("accountRoles");
                predicates.add(accountRoleJoin.get("role").get("id").in(criteria.getRoleIds()));
                query.distinct(true);
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
