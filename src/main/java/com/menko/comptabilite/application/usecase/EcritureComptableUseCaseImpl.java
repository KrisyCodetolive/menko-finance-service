package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.EcritureComptableRequest;
import com.menko.comptabilite.application.dto.request.LigneEcritureRequest;
import com.menko.comptabilite.application.port.in.EcritureComptableUseCase;
import com.menko.comptabilite.application.port.out.EcritureComptablePort;
import com.menko.comptabilite.application.port.out.JournalComptablePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.EcritureComptable;
import com.menko.comptabilite.domain.model.LigneEcriture;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EcritureComptableUseCaseImpl implements EcritureComptableUseCase {

    private final EcritureComptablePort ecriturePort;
    private final JournalComptablePort journalPort;

    @Override
    public Page<EcritureComptable> listerParFiliale(UUID filialeId, LocalDate debut, LocalDate fin, Pageable pageable) {
        return ecriturePort.findByFilialeId(filialeId, debut, fin, pageable);
    }

    @Override
    public EcritureComptable trouverParId(UUID id) {
        return ecriturePort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Écriture comptable", id));
    }

    @Override
    @Transactional
    public EcritureComptable creer(EcritureComptableRequest request, UUID utilisateurId) {
        validerEquilibreEcriture(request.lignes());

        var journal = journalPort.findById(request.identifiantJournal())
                .orElseThrow(() -> new EntiteIntrouvableException("Journal", request.identifiantJournal()));

        String numeroPiece = genererNumeroPiece(journal.getCode(), request.identifiantFiliale());

        List<LigneEcriture> lignes = request.lignes().stream()
                .map(l -> LigneEcriture.builder()
                        .identifiantCompte(l.identifiantCompte())
                        .debit(l.debit())
                        .credit(l.credit())
                        .build())
                .toList();

        EcritureComptable ecriture = EcritureComptable.builder()
                .numeroPiece(numeroPiece)
                .dateEcriture(request.dateEcriture())
                .libelle(request.libelle())
                .identifiantJournal(request.identifiantJournal())
                .identifiantFiliale(request.identifiantFiliale())
                .identifiantUtilisateur(utilisateurId)
                .identifiantTiers(request.identifiantTiers())
                .lignes(lignes)
                .build();

        return ecriturePort.save(ecriture);
    }

    @Override
    @Transactional
    public EcritureComptable modifier(UUID id, EcritureComptableRequest request) {
        EcritureComptable ecriture = trouverParId(id);
        validerEquilibreEcriture(request.lignes());

        List<LigneEcriture> lignes = request.lignes().stream()
                .map(l -> LigneEcriture.builder()
                        .identifiantCompte(l.identifiantCompte())
                        .debit(l.debit())
                        .credit(l.credit())
                        .build())
                .toList();

        ecriture.setDateEcriture(request.dateEcriture());
        ecriture.setLibelle(request.libelle());
        ecriture.setIdentifiantTiers(request.identifiantTiers());
        ecriture.setLignes(lignes);

        return ecriturePort.save(ecriture);
    }

    @Override
    public void supprimer(UUID id) {
        trouverParId(id);
        ecriturePort.deleteById(id);
    }

    private void validerEquilibreEcriture(List<LigneEcritureRequest> lignes) {
        BigDecimal totalDebit = lignes.stream().map(LigneEcritureRequest::debit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalCredit = lignes.stream().map(LigneEcritureRequest::credit)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        if (totalDebit.compareTo(totalCredit) != 0) {
            throw new RegleMetierException(
                    "L'écriture n'est pas équilibrée : débit=" + totalDebit + " ≠ crédit=" + totalCredit);
        }
    }

    private String genererNumeroPiece(String codeJournal, UUID filialeId) {
        int annee = Year.now().getValue();
        String maxPiece = ecriturePort.findMaxNumeroPiece(codeJournal, annee, filialeId).orElse(null);
        int sequence = 1;
        if (maxPiece != null) {
            try {
                sequence = Integer.parseInt(maxPiece.substring(maxPiece.lastIndexOf('-') + 1)) + 1;
            } catch (NumberFormatException ignored) {}
        }
        return String.format("%s-%d-%04d", codeJournal, annee, sequence);
    }
}
