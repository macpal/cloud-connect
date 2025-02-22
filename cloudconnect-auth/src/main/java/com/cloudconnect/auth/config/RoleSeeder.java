package com.cloudconnect.auth.config;

import com.cloudconnect.auth.entity.Role;
import com.cloudconnect.auth.entity.RoleName;
import com.cloudconnect.auth.repository.RoleRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Slf4j  // Add this for logging
public class RoleSeeder {

    private final RoleRepository roleRepository;

    @PostConstruct
    public void seedRoles() {
        Set<RoleName> roleNames = new HashSet<>(Arrays.asList(RoleName.USER, RoleName.ADMIN));

        for (RoleName roleName : roleNames) {
            roleRepository.findByRoleName(roleName)
                    .ifPresentOrElse(
                            role -> log.info("Role '{}' already exists", roleName),
                            () -> {
                                Role newRole = new Role();
                                newRole.setRoleName(roleName);
                                roleRepository.save(newRole);
                                log.info("Created new role: '{}'", roleName);
                            }
                    );
        }
    }
}
