package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.CompteComptableRequest;
import com.menko.comptabilite.application.port.in.CompteComptableUseCase;
import com.menko.comptabilite.application.port.out.CompteComptablePort;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.CompteComptable;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompteComptableUseCaseImpl implements CompteComptableUseCase {

    private final CompteComptablePort comptePort;
    private final FilialePort filialePort;

    @Override
    public Page<CompteComptable> listerParFiliale(UUID filialeId, Pageable pageable) {
        verifierFiliale(filialeId);
        return comptePort.findByFilialeId(filialeId, pageable);
    }

    @Override
    public List<CompteComptable> listerParFilialeEtClasse(UUID filialeId, Integer classe) {
        return comptePort.findByFilialeIdAndClasse(filialeId, classe);
    }

    @Override
    public CompteComptable trouverParId(UUID id) {
        return comptePort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Compte comptable", id));
    }

    @Override
    public CompteComptable creer(CompteComptableRequest request) {
        verifierFiliale(request.identifiantFiliale());
        String numero = genererNumero(request.classe(), request.identifiantFiliale());
        return comptePort.save(CompteComptable.builder()
                .numero(numero)
                .libelle(request.libelle())
                .classe(request.classe())
                .type(request.type())
                .actif(true)
                .identifiantFiliale(request.identifiantFiliale())
                .build());
    }

    @Override
    public CompteComptable modifier(UUID id, CompteComptableRequest request) {
        CompteComptable compte = trouverParId(id);
        compte.setLibelle(request.libelle());
        compte.setType(request.type());
        return comptePort.save(compte);
    }

    @Override
    public void archiver(UUID id) {
        CompteComptable compte = trouverParId(id);
        compte.setActif(false);
        comptePort.save(compte);
    }

    private String genererNumero(Integer classe, UUID filialeId) {
        String prefixe = String.valueOf(classe);
        String dernier = comptePort.findMaxNumeroByClasseAndFilialeId(classe, filialeId)
                .orElse(prefixe + "00000");
        int suivant = Integer.parseInt(dernier) + 1;
        if (!String.valueOf(suivant).startsWith(prefixe)) {
            throw new RegleMetierException("Numérotation saturée pour la classe " + classe);
        }
        return String.valueOf(suivant);
    }

    private void verifierFiliale(UUID filialeId) {
        filialePort.findById(filialeId)
                .orElseThrow(() -> new EntiteIntrouvableException("Filiale", filialeId));
    }
}
