package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.CompteBancaireRequest;
import com.menko.comptabilite.domain.model.CompteBancaire;

import java.util.List;
import java.util.UUID;

/** Comptes bancaires par filiale avec solde en temps réel. */
public interface CompteBancaireUseCase {
    List<CompteBancaire> listerParFiliale(UUID filialeId);
    CompteBancaire trouverParId(UUID id);
    CompteBancaire creer(CompteBancaireRequest request);
    CompteBancaire modifier(UUID id, CompteBancaireRequest request);
    void cloturerCompte(UUID id);
}
