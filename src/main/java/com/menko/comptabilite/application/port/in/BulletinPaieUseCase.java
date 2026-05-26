package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.BulletinPaieRequest;
import com.menko.comptabilite.domain.model.BulletinPaie;

import java.util.List;
import java.util.UUID;

/** Bulletins de paie mensuels. */
public interface BulletinPaieUseCase {
    List<BulletinPaie> listerParEmploye(UUID employeId);
    List<BulletinPaie> listerParFilialeEtPeriode(UUID filialeId, int mois, int annee);
    BulletinPaie trouverParId(UUID id);
    BulletinPaie creer(BulletinPaieRequest request);
}
