package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.LigneRapprochementJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface LigneRapprochementJpaRepository extends JpaRepository<LigneRapprochementJpa, UUID> {
    List<LigneRapprochementJpa> findByRapprochementIdentifiant(UUID rapprochementId);
}
