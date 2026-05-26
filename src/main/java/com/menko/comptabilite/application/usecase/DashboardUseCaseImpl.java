package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.response.DashboardFilialeResponse;
import com.menko.comptabilite.application.port.in.DashboardUseCase;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.Filiale;
import com.menko.comptabilite.infrastructure.persistence.repository.ChequeJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteComptableJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.EcritureComptableJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.LigneEcritureJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.RapprochementBancaireJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteBancaireJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class DashboardUseCaseImpl implements DashboardUseCase {

    private final FilialePort filialePort;
    private final EcritureComptableJpaRepository ecritureRepo;
    private final CompteComptableJpaRepository compteRepo;
    private final LigneEcritureJpaRepository ligneRepo;
    private final ChequeJpaRepository chequeRepo;
    private final RapprochementBancaireJpaRepository rapprochementRepo;
    private final CompteBancaireJpaRepository compteBancaireRepo;

    @Override
    public DashboardFilialeResponse dashboardFiliale(UUID filialeId) {
        Filiale filiale = filialePort.findById(filialeId)
                .orElseThrow(() -> new EntiteIntrouvableException("Filiale", filialeId));

        long nombreEcritures = ecritureRepo.countByFilialeIdentifiant(filialeId);
        long nombreComptes = compteRepo.findByFilialeIdentifiant(filialeId,
                PageRequest.of(0, Integer.MAX_VALUE)).getTotalElements();

        BigDecimal totalDebit = ligneRepo.sumTotalDebit(filialeId);
        BigDecimal totalCredit = ligneRepo.sumTotalCredit(filialeId);
        BigDecimal soldeTresorerie = ligneRepo.sumSoldeTresorerie(filialeId);

        long chequesEnCours = compteRepo.findByFilialeIdentifiant(filialeId,
                PageRequest.of(0, Integer.MAX_VALUE))
                .stream()
                .mapToLong(c -> chequeRepo.countByCompteBancaireIdentifiantAndStatut(
                        c.getIdentifiant(), "En cours"))
                .sum();

        long rapprochementsEnCours = compteRepo.findByFilialeIdentifiant(filialeId,
                PageRequest.of(0, Integer.MAX_VALUE))
                .stream()
                .mapToLong(c -> rapprochementRepo.countByCompteBancaireIdentifiantAndStatut(
                        c.getIdentifiant(), "En cours"))
                .sum();

        var dernieresEcritures = ecritureRepo.findByFilialeAndPeriode(
                        filialeId, null, null, PageRequest.of(0, 5))
                .stream()
                .map(e -> {
                    BigDecimal d = e.getLignes().stream().map(l -> l.getDebit())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    BigDecimal cr = e.getLignes().stream().map(l -> l.getCredit())
                            .reduce(BigDecimal.ZERO, BigDecimal::add);
                    return new DashboardFilialeResponse.EcritureRecenteResponse(
                            e.getNumeroPiece(), e.getLibelle(),
                            e.getJournal().getCode(), e.getDateEcriture().toString(), d, cr);
                })
                .toList();

        return new DashboardFilialeResponse(
                filialeId, filiale.getNom(), nombreEcritures, nombreComptes,
                totalDebit, totalCredit, soldeTresorerie,
                chequesEnCours, rapprochementsEnCours, dernieresEcritures);
    }

    @Override
    public List<DashboardFilialeResponse> dashboardGroupe() {
        return filialePort.findAll().stream()
                .map(f -> dashboardFiliale(f.getIdentifiant()))
                .toList();
    }
}
