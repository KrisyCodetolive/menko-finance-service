package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.BulletinPaieJpa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BulletinPaieJpaRepository extends JpaRepository<BulletinPaieJpa, UUID> {

    List<BulletinPaieJpa> findByEmployeIdentifiant(UUID employeId);

    @Query("SELECT b FROM BulletinPaieJpa b WHERE b.employe.filiale.identifiant = :filialeId " +
           "AND b.mois = :mois AND b.annee = :annee")
    List<BulletinPaieJpa> findByFilialeAndPeriode(@Param("filialeId") UUID filialeId,
                                                    @Param("mois") int mois,
                                                    @Param("annee") int annee);

    boolean existsByEmployeIdentifiantAndMoisAndAnnee(UUID employeId, int mois, int annee);
}
