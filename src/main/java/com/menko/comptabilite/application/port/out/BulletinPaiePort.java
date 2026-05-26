package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.BulletinPaie;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BulletinPaiePort {
    List<BulletinPaie> findByEmployeId(UUID employeId);
    List<BulletinPaie> findByFilialeIdAndPeriode(UUID filialeId, int mois, int annee);
    Optional<BulletinPaie> findById(UUID id);
    boolean existsByEmployeIdAndMoisAndAnnee(UUID employeId, int mois, int annee);
    BulletinPaie save(BulletinPaie bulletin);
}
