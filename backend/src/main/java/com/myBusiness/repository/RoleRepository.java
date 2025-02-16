package com.myBusiness.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.myBusiness.model.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(String name);
}

