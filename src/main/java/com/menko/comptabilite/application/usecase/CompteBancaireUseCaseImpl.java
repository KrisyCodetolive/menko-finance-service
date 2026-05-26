package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.CompteBancaireRequest;
import com.menko.comptabilite.application.port.in.CompteBancaireUseCase;
import com.menko.comptabilite.application.port.out.CompteBancairePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.CompteBancaire;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompteBancaireUseCaseImpl implements CompteBancaireUseCase {

    private final CompteBancairePort compteBancairePort;

    @Override
    public List<CompteBancaire> listerParFiliale(UUID filialeId) {
        return compteBancairePort.findByFilialeId(filialeId);
    }

    @Override
    public CompteBancaire trouverParId(UUID id) {
        return compteBancairePort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Compte bancaire", id));
    }

    @Override
    public CompteBancaire creer(CompteBancaireRequest request) {
        return compteBancairePort.save(CompteBancaire.builder()
                .nomBanque(request.nomBanque())
                .numeroCompte(request.numeroCompte())
                .soldeInitial(request.soldeInitial())
                .actif(true)
                .identifiantFiliale(request.identifiantFiliale())
                .build());
    }

    @Override
    public CompteBancaire modifier(UUID id, CompteBancaireRequest request) {
        CompteBancaire compte = trouverParId(id);
        compte.setNomBanque(request.nomBanque());
        compte.setNumeroCompte(request.numeroCompte());
        return compteBancairePort.save(compte);
    }

    @Override
    public void cloturerCompte(UUID id) {
        CompteBancaire compte = trouverParId(id);
        compte.setActif(false);
        compteBancairePort.save(compte);
    }
}
