package com.menko.comptabilite.repository;

import com.menko.comptabilite.entity.Filiale;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FilialeRepository extends JpaRepository<Filiale, UUID> {
}
