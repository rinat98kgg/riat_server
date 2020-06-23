package com.riatServer.repo;

import com.riatServer.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RolesRepo extends JpaRepository<Role, Long> {
    Role findByName(String name);
}