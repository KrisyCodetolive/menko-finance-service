package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.EmployeJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface EmployeJpaRepository extends JpaRepository<EmployeJpa, UUID> {
    Page<EmployeJpa> findByFilialeIdentifiant(UUID filialeId, Pageable pageable);
}
