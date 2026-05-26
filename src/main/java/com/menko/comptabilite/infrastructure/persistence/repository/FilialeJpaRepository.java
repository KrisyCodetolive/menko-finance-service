package com.menko.comptabilite.infrastructure.persistence.repository;

import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilialeJpaRepository extends JpaRepository<FilialeJpa, UUID> {}
