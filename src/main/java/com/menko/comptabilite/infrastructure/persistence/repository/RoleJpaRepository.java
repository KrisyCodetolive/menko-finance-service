package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.RoleJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface RoleJpaRepository extends JpaRepository<RoleJpa, UUID> {
    Optional<RoleJpa> findByNom(String nom);
}
