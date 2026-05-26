package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.LigneRapprochementRequest;
import com.menko.comptabilite.application.dto.request.RapprochementBancaireRequest;
import com.menko.comptabilite.application.dto.request.StatutLigneRapprochementRequest;
import com.menko.comptabilite.domain.model.LigneRapprochement;
import com.menko.comptabilite.domain.model.RapprochementBancaire;

import java.util.List;
import java.util.UUID;

/** Sessions de rapprochement bancaire ligne par ligne. */
public interface RapprochementBancaireUseCase {
    List<RapprochementBancaire> listerParCompteBancaire(UUID compteBancaireId);
    RapprochementBancaire trouverParId(UUID id);
    RapprochementBancaire ouvrirSession(RapprochementBancaireRequest request, UUID utilisateurId);
    void terminerSession(UUID id);
    LigneRapprochement ajouterLigne(UUID rapprochementId, LigneRapprochementRequest request);
    LigneRapprochement validerLigne(UUID ligneId, StatutLigneRapprochementRequest request);
    List<LigneRapprochement> listerLignes(UUID rapprochementId);
}
