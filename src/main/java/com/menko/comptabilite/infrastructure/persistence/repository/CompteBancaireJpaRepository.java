package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.CompteBancaireJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface CompteBancaireJpaRepository extends JpaRepository<CompteBancaireJpa, UUID> {

    List<CompteBancaireJpa> findByFilialeIdentifiant(UUID filialeId);

    /** Solde = soldeInitial + encaissements reçus - encaissements émis. */
    @Query("""
           SELECT cb.soldeInitial
                + COALESCE((SELECT SUM(c.montant) FROM ChequeJpa c
                             WHERE c.compteBancaire.identifiant = :compteId
                               AND c.type = 'RECU' AND c.statut = 'Encaissé'), 0)
                - COALESCE((SELECT SUM(c.montant) FROM ChequeJpa c
                             WHERE c.compteBancaire.identifiant = :compteId
                               AND c.type = 'EMIS' AND c.statut = 'Encaissé'), 0)
           FROM CompteBancaireJpa cb WHERE cb.identifiant = :compteId
           """)
    BigDecimal calculerSoldeActuel(@Param("compteId") UUID compteId);

    @Query("SELECT COALESCE(SUM(cb.soldeInitial), 0) FROM CompteBancaireJpa cb " +
           "WHERE cb.filiale.identifiant = :filialeId AND cb.statut = 'ACTIF'")
    BigDecimal sumSoldeInitialByFiliale(@Param("filialeId") UUID filialeId);
}
