package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.EcritureComptableRequest;
import com.menko.comptabilite.domain.model.EcritureComptable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.UUID;

/** Saisie et consultation des écritures comptables. */
public interface EcritureComptableUseCase {
    Page<EcritureComptable> listerParFiliale(UUID filialeId, LocalDate debut, LocalDate fin, Pageable pageable);
    EcritureComptable trouverParId(UUID id);
    EcritureComptable creer(EcritureComptableRequest request, UUID utilisateurId);
    EcritureComptable modifier(UUID id, EcritureComptableRequest request);
    void supprimer(UUID id);
    /** Transition BROUILLON → VALIDEE. */
    EcritureComptable valider(UUID id);
    /** Transition VALIDEE → CLOTUREE. */
    EcritureComptable cloturer(UUID id);
}
