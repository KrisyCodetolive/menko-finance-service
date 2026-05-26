package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.ChequeRequest;
import com.menko.comptabilite.application.dto.request.StatutChequeRequest;
import com.menko.comptabilite.domain.model.Cheque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/** Gestion des chèques émis et reçus. */
public interface ChequeUseCase {
    Page<Cheque> listerParCompteBancaire(UUID compteBancaireId, String statut, Pageable pageable);
    List<Cheque> alertesChequesNonEncaisses(UUID filialeId, int joursDepuis);
    Cheque trouverParId(UUID id);
    Cheque emettre(ChequeRequest request);
    Cheque mettreAJourStatut(UUID id, StatutChequeRequest request);
}
