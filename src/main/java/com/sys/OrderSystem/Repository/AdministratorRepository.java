package com.sys.OrderSystem.Repository;

import com.sys.OrderSystem.Entity.Administrator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Integer> {
    Administrator findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}