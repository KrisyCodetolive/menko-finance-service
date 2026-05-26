package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.ChequeRequest;
import com.menko.comptabilite.application.dto.request.StatutChequeRequest;
import com.menko.comptabilite.application.port.in.ChequeUseCase;
import com.menko.comptabilite.application.port.out.ChequePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.Cheque;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ChequeUseCaseImpl implements ChequeUseCase {

    private final ChequePort chequePort;

    @Override
    public Page<Cheque> listerParCompteBancaire(UUID compteBancaireId, String statut, Pageable pageable) {
        if (statut != null && !statut.isBlank()) {
            return chequePort.findByCompteBancaireIdAndStatut(compteBancaireId, statut, pageable);
        }
        return chequePort.findByCompteBancaireId(compteBancaireId, pageable);
    }

    @Override
    public List<Cheque> alertesChequesNonEncaisses(UUID filialeId, int joursDepuis) {
        LocalDate limite = LocalDate.now().minusDays(joursDepuis);
        return chequePort.findChequesNonEncaissesBefore(filialeId, limite);
    }

    @Override
    public Cheque trouverParId(UUID id) {
        return chequePort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Chèque", id));
    }

    @Override
    public Cheque emettre(ChequeRequest request) {
        return chequePort.save(Cheque.builder()
                .type(request.type())
                .numeroCheque(request.numeroCheque())
                .tiersNom(request.tiersNom())
                .montant(request.montant())
                .motif(request.motif())
                .dateCheque(request.dateCheque())
                .statut("En cours")
                .identifiantCompteBancaire(request.identifiantCompteBancaire())
                .identifiantEcriture(request.identifiantEcriture())
                .build());
    }

    @Override
    public Cheque mettreAJourStatut(UUID id, StatutChequeRequest request) {
        Cheque cheque = trouverParId(id);
        validerTransitionStatut(cheque.getStatut(), request.statut());
        cheque.setStatut(request.statut());
        if (request.dateEncaissement() != null) {
            cheque.setDateEncaissement(request.dateEncaissement());
        }
        return chequePort.save(cheque);
    }

    private static final Map<String, Set<String>> TRANSITIONS_AUTORISEES = Map.of(
            "En cours",        Set.of("Remis en banque", "Encaissé", "Rejeté"),
            "Remis en banque", Set.of("Encaissé", "Rejeté")
    );

    private void validerTransitionStatut(String actuel, String nouveau) {
        Set<String> autorisees = TRANSITIONS_AUTORISEES.getOrDefault(actuel, Set.of());
        if (!autorisees.contains(nouveau)) {
            throw new RegleMetierException(
                    "Transition de statut invalide pour un chèque : " + actuel + " → " + nouveau);
        }
    }
}
