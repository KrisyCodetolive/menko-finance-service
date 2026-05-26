package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.BulletinPaieRequest;
import com.menko.comptabilite.application.port.in.BulletinPaieUseCase;
import com.menko.comptabilite.application.port.out.BulletinPaiePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.BulletinPaie;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BulletinPaieUseCaseImpl implements BulletinPaieUseCase {

    private final BulletinPaiePort bulletinPort;

    @Override
    public List<BulletinPaie> listerParEmploye(UUID employeId) {
        return bulletinPort.findByEmployeId(employeId);
    }

    @Override
    public List<BulletinPaie> listerParFilialeEtPeriode(UUID filialeId, int mois, int annee) {
        return bulletinPort.findByFilialeIdAndPeriode(filialeId, mois, annee);
    }

    @Override
    public BulletinPaie trouverParId(UUID id) {
        return bulletinPort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Bulletin de paie", id));
    }

    @Override
    public BulletinPaie creer(BulletinPaieRequest request) {
        if (bulletinPort.existsByEmployeIdAndMoisAndAnnee(
                request.identifiantEmploye(), request.mois(), request.annee())) {
            throw new RegleMetierException("Un bulletin existe déjà pour cet employé sur " +
                    request.mois() + "/" + request.annee());
        }
        return bulletinPort.save(BulletinPaie.builder()
                .mois(request.mois())
                .annee(request.annee())
                .salaireBrut(request.salaireBrut())
                .chargesSalariales(request.chargesSalariales())
                .impot(request.impot())
                .salaireNet(request.salaireNet())
                .chargesPatronales(request.chargesPatronales())
                .identifiantEmploye(request.identifiantEmploye())
                .identifiantEcriture(request.identifiantEcriture())
                .build());
    }
}
