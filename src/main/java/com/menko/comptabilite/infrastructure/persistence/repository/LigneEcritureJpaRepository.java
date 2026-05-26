package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.infrastructure.persistence.entity.LigneEcritureJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LigneEcritureJpaRepository extends JpaRepository<LigneEcritureJpa, UUID> {

    List<LigneEcritureJpa> findByEcritureIdentifiant(UUID ecritureId);

    void deleteByEcritureIdentifiant(UUID ecritureId);

    @Query("""
           SELECT new com.menko.comptabilite.application.dto.response.BalanceLigneResponse(
               c.numero, c.libelle, c.classe,
               COALESCE(SUM(l.debit), 0),
               COALESCE(SUM(l.credit), 0),
               CASE WHEN COALESCE(SUM(l.debit),0) > COALESCE(SUM(l.credit),0)
                    THEN COALESCE(SUM(l.debit),0) - COALESCE(SUM(l.credit),0) ELSE 0 END,
               CASE WHEN COALESCE(SUM(l.credit),0) > COALESCE(SUM(l.debit),0)
                    THEN COALESCE(SUM(l.credit),0) - COALESCE(SUM(l.debit),0) ELSE 0 END
           )
           FROM LigneEcritureJpa l
           JOIN l.compte c
           JOIN l.ecriture e
           WHERE c.filiale.identifiant = :filialeId
             AND e.statut IN ('VALIDEE', 'CLOTUREE')
             AND (:debut IS NULL OR e.dateEcriture >= :debut)
             AND (:fin   IS NULL OR e.dateEcriture <= :fin)
           GROUP BY c.identifiant, c.numero, c.libelle, c.classe
           ORDER BY c.numero
           """)
    List<BalanceLigneResponse> calculerBalance(@Param("filialeId") UUID filialeId,
                                                @Param("debut") LocalDate debut,
                                                @Param("fin") LocalDate fin);

    @Query("""
           SELECT new com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse(
               e.dateEcriture, e.numeroPiece, l.libelle, e.journal.code,
               l.debit, l.credit, 0.0BD
           )
           FROM LigneEcritureJpa l
           JOIN l.ecriture e
           WHERE l.compte.identifiant = :compteId
             AND e.statut IN ('VALIDEE', 'CLOTUREE')
             AND (:debut IS NULL OR e.dateEcriture >= :debut)
             AND (:fin   IS NULL OR e.dateEcriture <= :fin)
           ORDER BY e.dateEcriture, e.dateCreation
           """)
    List<GrandLivreLigneResponse> calculerGrandLivreRaw(@Param("compteId") UUID compteId,
                                                          @Param("debut") LocalDate debut,
                                                          @Param("fin") LocalDate fin);

    @Query("SELECT COALESCE(SUM(l.debit) - SUM(l.credit), 0) FROM LigneEcritureJpa l " +
           "JOIN l.compte c WHERE c.filiale.identifiant = :filialeId AND c.classe = 5")
    java.math.BigDecimal sumSoldeTresorerie(@Param("filialeId") UUID filialeId);

    @Query("SELECT COALESCE(SUM(l.debit), 0) FROM LigneEcritureJpa l " +
           "JOIN l.ecriture e WHERE e.filiale.identifiant = :filialeId")
    java.math.BigDecimal sumTotalDebit(@Param("filialeId") UUID filialeId);

    @Query("SELECT COALESCE(SUM(l.credit), 0) FROM LigneEcritureJpa l " +
           "JOIN l.ecriture e WHERE e.filiale.identifiant = :filialeId")
    java.math.BigDecimal sumTotalCredit(@Param("filialeId") UUID filialeId);
}
