package com.menko.comptabilite.repository;

import com.menko.comptabilite.entity.CompteComptable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteComptableRepository extends JpaRepository<CompteComptable, UUID> {

    List<CompteComptable> findByFilialeIdentifiant(UUID identifiantFiliale);

    Optional<CompteComptable> findByNumeroAndFilialeIdentifiant(String numero, UUID identifiantFiliale);

    @Query("SELECT MAX(c.numero) FROM CompteComptable c WHERE c.classe = :classe AND c.filiale.identifiant = :identifiantFiliale")
    Optional<String> findMaxNumeroByClasseAndFiliale(@Param("classe") Integer classe, @Param("identifiantFiliale") UUID identifiantFiliale);
}
