package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.LigneRapprochementRequest;
import com.menko.comptabilite.application.dto.request.RapprochementBancaireRequest;
import com.menko.comptabilite.application.dto.request.StatutLigneRapprochementRequest;
import com.menko.comptabilite.application.port.in.RapprochementBancaireUseCase;
import com.menko.comptabilite.application.port.out.LigneRapprochementPort;
import com.menko.comptabilite.application.port.out.RapprochementBancairePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.LigneRapprochement;
import com.menko.comptabilite.domain.model.RapprochementBancaire;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RapprochementBancaireUseCaseImpl implements RapprochementBancaireUseCase {

    private final RapprochementBancairePort rapprochementPort;
    private final LigneRapprochementPort lignePort;

    @Override
    public List<RapprochementBancaire> listerParCompteBancaire(UUID compteBancaireId) {
        return rapprochementPort.findByCompteBancaireId(compteBancaireId);
    }

    @Override
    public RapprochementBancaire trouverParId(UUID id) {
        return rapprochementPort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Rapprochement bancaire", id));
    }

    @Override
    public RapprochementBancaire ouvrirSession(RapprochementBancaireRequest request, UUID utilisateurId) {
        if (rapprochementPort.existsByCompteBancaireIdAndPeriode(
                request.identifiantCompteBancaire(), request.periode())) {
            throw new RegleMetierException("Un rapprochement existe déjà pour la période '" +
                    request.periode() + "' sur ce compte");
        }
        return rapprochementPort.save(RapprochementBancaire.builder()
                .periode(request.periode())
                .statut("En cours")
                .soldeReleve(request.soldeReleve())
                .dateImport(request.dateImport())
                .identifiantCompteBancaire(request.identifiantCompteBancaire())
                .identifiantUtilisateur(utilisateurId)
                .build());
    }

    @Override
    public void terminerSession(UUID id) {
        RapprochementBancaire r = trouverParId(id);
        if ("Terminé".equals(r.getStatut())) {
            throw new RegleMetierException("Ce rapprochement est déjà terminé");
        }
        r.setStatut("Terminé");
        rapprochementPort.save(r);
    }

    @Override
    public LigneRapprochement ajouterLigne(UUID rapprochementId, LigneRapprochementRequest request) {
        trouverParId(rapprochementId);
        return lignePort.save(LigneRapprochement.builder()
                .dateReleve(request.dateReleve())
                .libelleReleve(request.libelleReleve())
                .montantReleve(request.montantReleve())
                .statut("En attente")
                .identifiantRapprochement(rapprochementId)
                .identifiantEcriture(request.identifiantEcriture())
                .build());
    }

    @Override
    public LigneRapprochement validerLigne(UUID ligneId, StatutLigneRapprochementRequest request) {
        LigneRapprochement ligne = lignePort.findById(ligneId)
                .orElseThrow(() -> new EntiteIntrouvableException("Ligne de rapprochement", ligneId));
        ligne.setStatut(request.statut());
        if (request.identifiantEcriture() != null) {
            ligne.setIdentifiantEcriture(request.identifiantEcriture());
        }
        return lignePort.save(ligne);
    }

    @Override
    public List<LigneRapprochement> listerLignes(UUID rapprochementId) {
        return lignePort.findByRapprochementId(rapprochementId);
    }
}
