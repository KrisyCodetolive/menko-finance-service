package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.JournalComptableJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JournalComptableJpaRepository extends JpaRepository<JournalComptableJpa, UUID> {
    List<JournalComptableJpa> findByFilialeIdentifiant(UUID filialeId);
    Optional<JournalComptableJpa> findByCodeAndFilialeIdentifiant(String code, UUID filialeId);
}
