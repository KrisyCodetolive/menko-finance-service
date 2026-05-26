package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.BilanResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.application.port.in.BalanceUseCase;
import com.menko.comptabilite.application.port.out.CompteComptablePort;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.application.port.out.LigneEcriturePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceUseCaseImpl implements BalanceUseCase {

    private final LigneEcriturePort ligneEcriturePort;
    private final CompteComptablePort comptePort;
    private final FilialePort filialePort;

    @Override
    public List<BalanceLigneResponse> balanceGenerale(UUID filialeId, LocalDate debut, LocalDate fin) {
        return ligneEcriturePort.calculerBalance(filialeId, debut, fin);
    }

    @Override
    public List<GrandLivreLigneResponse> grandLivre(UUID compteId, LocalDate debut, LocalDate fin) {
        comptePort.findById(compteId)
                .orElseThrow(() -> new EntiteIntrouvableException("Compte comptable", compteId));
        return ligneEcriturePort.calculerGrandLivre(compteId, debut, fin);
    }

    @Override
    public BilanResponse bilan(UUID filialeId, LocalDate fin) {
        var filiale = filialePort.findById(filialeId)
                .orElseThrow(() -> new EntiteIntrouvableException("Filiale", filialeId));

        List<BalanceLigneResponse> balance = ligneEcriturePort.calculerBalance(filialeId, null, fin);

        // Actif : classes 1-2 (soldes débiteurs) + 3 + 4 débiteurs + 5
        List<BalanceLigneResponse> actif = balance.stream()
                .filter(l -> List.of(2, 3, 5).contains(l.classe())
                        || (l.classe() == 1 && l.soldeDebiteur().compareTo(BigDecimal.ZERO) > 0)
                        || (l.classe() == 4 && l.soldeDebiteur().compareTo(BigDecimal.ZERO) > 0))
                .toList();

        // Passif : classe 1 (capitaux) + 4 créditeurs
        List<BalanceLigneResponse> passif = balance.stream()
                .filter(l -> (l.classe() == 1 && l.soldeCrediteur().compareTo(BigDecimal.ZERO) > 0)
                        || (l.classe() == 4 && l.soldeCrediteur().compareTo(BigDecimal.ZERO) > 0))
                .toList();

        BigDecimal totalActif = actif.stream().map(BalanceLigneResponse::soldeDebiteur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalPassif = passif.stream().map(BalanceLigneResponse::soldeCrediteur)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        String periode = fin != null ? "Au " + fin : "À ce jour";
        return new BilanResponse(filiale.getNom(), periode, actif, passif, totalActif, totalPassif);
    }

    @Override
    public List<BalanceLigneResponse> compteDeResultat(UUID filialeId, LocalDate debut, LocalDate fin) {
        return ligneEcriturePort.calculerBalance(filialeId, debut, fin).stream()
                .filter(l -> l.classe() == 6 || l.classe() == 7)
                .toList();
    }
}
