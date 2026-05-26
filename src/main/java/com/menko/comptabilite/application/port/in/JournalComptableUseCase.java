package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.JournalComptableRequest;
import com.menko.comptabilite.domain.model.JournalComptable;

import java.util.List;
import java.util.UUID;

/** Journaux comptables (VTE, ACH, SAL, OI) par filiale. */
public interface JournalComptableUseCase {
    List<JournalComptable> listerParFiliale(UUID filialeId);
    JournalComptable trouverParId(UUID id);
    JournalComptable creer(JournalComptableRequest request);
    JournalComptable modifier(UUID id, JournalComptableRequest request);
    void supprimer(UUID id);
}
