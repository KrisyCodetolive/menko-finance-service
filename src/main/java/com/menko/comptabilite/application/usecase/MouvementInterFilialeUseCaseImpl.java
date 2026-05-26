package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.MouvementInterFilialeRequest;
import com.menko.comptabilite.application.port.in.EcritureComptableUseCase;
import com.menko.comptabilite.application.port.in.MouvementInterFilialeUseCase;
import com.menko.comptabilite.application.port.out.JournalComptablePort;
import com.menko.comptabilite.application.port.out.MouvementInterFilialePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.EcritureComptable;
import com.menko.comptabilite.domain.model.MouvementInterFiliale;
import com.menko.comptabilite.application.dto.request.EcritureComptableRequest;
import com.menko.comptabilite.application.dto.request.LigneEcritureRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class MouvementInterFilialeUseCaseImpl implements MouvementInterFilialeUseCase {

    private final MouvementInterFilialePort mouvementPort;
    private final EcritureComptableUseCase ecritureUseCase;
    private final JournalComptablePort journalPort;

    @Override
    public Page<MouvementInterFiliale> listerParFiliale(UUID filialeId, Pageable pageable) {
        return mouvementPort.findByFilialeId(filialeId, pageable);
    }

    @Override
    public MouvementInterFiliale trouverParId(UUID id) {
        return mouvementPort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Mouvement inter-filiale", id));
    }

    @Override
    @Transactional
    public MouvementInterFiliale enregistrer(MouvementInterFilialeRequest request, UUID utilisateurId) {
        if (request.identifiantFilialeSource().equals(request.identifiantFilialeDestination())) {
            throw new RegleMetierException("La filiale source et destination doivent être différentes");
        }

        UUID journalSourceId = resoudreJournalOI(request.identifiantJournalSource(),
                request.identifiantFilialeSource());
        UUID journalDestId = resoudreJournalOI(request.identifiantJournalDestination(),
                request.identifiantFilialeDestination());

        // Écriture côté source : débit compte source, crédit compte interco
        EcritureComptableRequest reqSource = new EcritureComptableRequest(
                request.dateMouvement(),
                "Virement inter-filiale : " + request.libelle(),
                journalSourceId,
                request.identifiantFilialeSource(),
                null,
                List.of(
                        new LigneEcritureRequest(request.identifiantCompteDebitSource(),
                                request.montant(), java.math.BigDecimal.ZERO, null),
                        new LigneEcritureRequest(request.identifiantCompteCreditSource(),
                                java.math.BigDecimal.ZERO, request.montant(), null)
                )
        );
        EcritureComptable ecritureSource = ecritureUseCase.creer(reqSource, utilisateurId);

        // Écriture côté destination : débit compte interco, crédit compte destination
        EcritureComptableRequest reqDest = new EcritureComptableRequest(
                request.dateMouvement(),
                "Virement inter-filiale reçu : " + request.libelle(),
                journalDestId,
                request.identifiantFilialeDestination(),
                null,
                List.of(
                        new LigneEcritureRequest(request.identifiantCompteDebitDestination(),
                                request.montant(), java.math.BigDecimal.ZERO, null),
                        new LigneEcritureRequest(request.identifiantCompteCreditDestination(),
                                java.math.BigDecimal.ZERO, request.montant(), null)
                )
        );
        EcritureComptable ecritureDest = ecritureUseCase.creer(reqDest, utilisateurId);

        return mouvementPort.save(MouvementInterFiliale.builder()
                .libelle(request.libelle())
                .montant(request.montant())
                .dateMouvement(request.dateMouvement())
                .identifiantFilialeSource(request.identifiantFilialeSource())
                .identifiantFilialeDestination(request.identifiantFilialeDestination())
                .identifiantEcritureSource(ecritureSource.getIdentifiant())
                .identifiantEcritureDestination(ecritureDest.getIdentifiant())
                .build());
    }

    private UUID resoudreJournalOI(UUID journalId, UUID filialeId) {
        if (journalId != null) return journalId;
        return journalPort.findByCodeAndFilialeId("OI", filialeId)
                .map(j -> j.getIdentifiant())
                .orElseThrow(() -> new RegleMetierException(
                        "Journal OI introuvable pour la filiale " + filialeId +
                        " — créez-le ou fournissez identifiantJournalSource/Destination"));
    }
}
