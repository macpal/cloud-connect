package com.cloudconnect.auth.repository;

import com.cloudconnect.auth.entity.Role;
import com.cloudconnect.auth.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByRoleName(String roleName);
}
