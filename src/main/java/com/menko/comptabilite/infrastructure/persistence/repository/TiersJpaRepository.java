package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.TiersJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TiersJpaRepository extends JpaRepository<TiersJpa, UUID> {
    Page<TiersJpa> findByFilialeIdentifiant(UUID filialeId, Pageable pageable);
    Page<TiersJpa> findByFilialeIdentifiantAndType(UUID filialeId, String type, Pageable pageable);
}
