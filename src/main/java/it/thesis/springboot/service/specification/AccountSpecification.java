package it.thesis.springboot.service.specification;

import it.thesis.springboot.dto.criteria.SearchAccountCriteria;
import it.thesis.springboot.model.Account;
import it.thesis.springboot.model.AccountRole;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
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
                Subquery<Long> subquery = query.subquery(Long.class);
                Root<AccountRole> accountRoleRoot = subquery.from(AccountRole.class);

                subquery.select(accountRoleRoot.get("account").get("id"))
                        .where(accountRoleRoot.get("role").get("id").in(criteria.getRoleIds()));

                predicates.add(from.get("id").in(subquery));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }
}
