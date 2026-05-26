package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.RapprochementBancaireJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RapprochementBancaireJpaRepository extends JpaRepository<RapprochementBancaireJpa, UUID> {

    List<RapprochementBancaireJpa> findByCompteBancaireIdentifiant(UUID compteBancaireId);

    boolean existsByCompteBancaireIdentifiantAndPeriode(UUID compteBancaireId, String periode);

    long countByCompteBancaireIdentifiantAndStatut(UUID compteBancaireId, String statut);
}
