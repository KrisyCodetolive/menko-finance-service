package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.UtilisateurJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UtilisateurJpaRepository extends JpaRepository<UtilisateurJpa, UUID> {
    Optional<UtilisateurJpa> findByEmail(String email);
    boolean existsByEmail(String email);
}
