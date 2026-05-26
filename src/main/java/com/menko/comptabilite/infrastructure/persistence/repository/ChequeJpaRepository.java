package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.ChequeJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ChequeJpaRepository extends JpaRepository<ChequeJpa, UUID> {

    Page<ChequeJpa> findByCompteBancaireIdentifiant(UUID compteBancaireId, Pageable pageable);

    Page<ChequeJpa> findByCompteBancaireIdentifiantAndStatut(UUID compteBancaireId,
                                                              String statut, Pageable pageable);

    @Query("SELECT c FROM ChequeJpa c WHERE c.compteBancaire.filiale.identifiant = :filialeId " +
           "AND c.statut = 'En cours' AND c.dateCheque <= :limite " +
           "ORDER BY c.dateCheque ASC")
    List<ChequeJpa> findChequesNonEncaissesBefore(@Param("filialeId") UUID filialeId,
                                                    @Param("limite") LocalDate limite);

    long countByCompteBancaireIdentifiantAndStatut(UUID compteBancaireId, String statut);
}
