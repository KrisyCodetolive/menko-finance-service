package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.EcritureComptableJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface EcritureComptableJpaRepository extends JpaRepository<EcritureComptableJpa, UUID> {

    @Query("SELECT e FROM EcritureComptableJpa e WHERE e.filiale.identifiant = :filialeId " +
           "AND (:debut IS NULL OR e.dateEcriture >= :debut) " +
           "AND (:fin IS NULL OR e.dateEcriture <= :fin) " +
           "ORDER BY e.dateEcriture DESC")
    Page<EcritureComptableJpa> findByFilialeAndPeriode(
            @Param("filialeId") UUID filialeId,
            @Param("debut") LocalDate debut,
            @Param("fin") LocalDate fin,
            Pageable pageable);

    @Query("SELECT MAX(e.numeroPiece) FROM EcritureComptableJpa e " +
           "WHERE e.numeroPiece LIKE CONCAT(:prefixe, '-', :annee, '-%') " +
           "AND e.filiale.identifiant = :filialeId")
    Optional<String> findMaxNumeroPiece(@Param("prefixe") String prefixe,
                                         @Param("annee") int annee,
                                         @Param("filialeId") UUID filialeId);

    long countByFilialeIdentifiant(UUID filialeId);
}
