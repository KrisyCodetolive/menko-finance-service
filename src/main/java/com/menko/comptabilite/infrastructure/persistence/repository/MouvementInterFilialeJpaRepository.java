package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.MouvementInterFilialeJpa;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface MouvementInterFilialeJpaRepository extends JpaRepository<MouvementInterFilialeJpa, UUID> {

    @Query("SELECT m FROM MouvementInterFilialeJpa m " +
           "WHERE m.filialeSource.identifiant = :filialeId " +
           "   OR m.filialeDestination.identifiant = :filialeId " +
           "ORDER BY m.dateMouvement DESC")
    Page<MouvementInterFilialeJpa> findByFilialeId(@Param("filialeId") UUID filialeId, Pageable pageable);
}
