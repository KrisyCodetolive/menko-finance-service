package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.EmployeRequest;
import com.menko.comptabilite.domain.model.Employe;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/** Gestion des employés par filiale. */
public interface EmployeUseCase {
    Page<Employe> listerParFiliale(UUID filialeId, Pageable pageable);
    Employe trouverParId(UUID id);
    Employe creer(EmployeRequest request);
    Employe modifier(UUID id, EmployeRequest request);
    void archiver(UUID id);
}
