package it.thesis.springboot.service;

import it.thesis.springboot.model.Role;
import it.thesis.springboot.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collector;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository repository;

    public List<Role> findAllByRoleIds(List<Long> roleIds) {
        List<Role> roles = repository.findAllById(roleIds);

        if (roles.size() != roleIds.size()) {
            String ids = roles.stream()
                    .map(Role::getId)
                    .collect(Collector.of(StringBuilder::new, (sb, id) -> sb.append(id).append(", "), StringBuilder::append))
                    .toString();

            throw new IllegalArgumentException("One or more role IDs not found [" + ids + "]");
        }

        return roles;
    }
}