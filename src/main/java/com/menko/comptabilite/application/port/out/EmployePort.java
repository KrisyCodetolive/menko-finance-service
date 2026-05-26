package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.Employe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EmployePort {
    Page<Employe> findByFilialeId(UUID filialeId, Pageable pageable);
    Optional<Employe> findById(UUID id);
    Employe save(Employe employe);
}
