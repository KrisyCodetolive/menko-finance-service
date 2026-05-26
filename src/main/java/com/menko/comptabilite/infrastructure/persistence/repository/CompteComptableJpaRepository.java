package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.CompteComptableJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteComptableJpaRepository extends JpaRepository<CompteComptableJpa, UUID> {

    Page<CompteComptableJpa> findByFilialeIdentifiant(UUID filialeId, Pageable pageable);

    List<CompteComptableJpa> findByFilialeIdentifiantAndClasse(UUID filialeId, Integer classe);

    Optional<CompteComptableJpa> findByNumeroAndFilialeIdentifiant(String numero, UUID filialeId);

    @Query("SELECT MAX(c.numero) FROM CompteComptableJpa c " +
           "WHERE c.classe = :classe AND c.filiale.identifiant = :filialeId")
    Optional<String> findMaxNumeroByClasseAndFilialeId(@Param("classe") Integer classe,
                                                        @Param("filialeId") UUID filialeId);
}
