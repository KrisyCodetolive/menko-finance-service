package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.BilanResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.application.port.out.CompteComptablePort;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.application.port.out.LigneEcriturePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.CompteComptable;
import com.menko.comptabilite.domain.model.Filiale;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BalanceUseCaseImplTest {

    @Mock
    private LigneEcriturePort ligneEcriturePort;

    @Mock
    private CompteComptablePort comptePort;

    @Mock
    private FilialePort filialePort;

    @InjectMocks
    private BalanceUseCaseImpl useCase;

    private final UUID filialeId = UUID.randomUUID();
    private final UUID compteId  = UUID.randomUUID();

    // --- Balance générale ---

    @Test
    void balanceGenerale_delegueAuPortEtRetourneResultat() {
        LocalDate debut = LocalDate.of(2026, 1, 1);
        LocalDate fin   = LocalDate.of(2026, 12, 31);
        List<BalanceLigneResponse> attendu = List.of(
                new BalanceLigneResponse("411000", "Clients", 4,
                        new BigDecimal("1000"), BigDecimal.ZERO,
                        new BigDecimal("1000"), BigDecimal.ZERO));
        when(ligneEcriturePort.calculerBalance(filialeId, debut, fin)).thenReturn(attendu);

        List<BalanceLigneResponse> result = useCase.balanceGenerale(filialeId, debut, fin);

        assertThat(result).isEqualTo(attendu);
        verify(ligneEcriturePort).calculerBalance(filialeId, debut, fin);
    }

    // --- Grand livre ---

    @Test
    void grandLivre_compteInexistant_leveEntiteIntrouvableException() {
        when(comptePort.findById(compteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.grandLivre(compteId, null, null))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void grandLivre_compteExistant_delegueAuPortEtRetourneResultat() {
        CompteComptable compte = CompteComptable.builder().identifiant(compteId).build();
        when(comptePort.findById(compteId)).thenReturn(Optional.of(compte));

        List<GrandLivreLigneResponse> lignes = List.of(
                new GrandLivreLigneResponse(LocalDate.of(2026, 1, 5), "ACH-2026-0001",
                        "Achat marchandises", "ACH",
                        new BigDecimal("500"), BigDecimal.ZERO, new BigDecimal("500")),
                new GrandLivreLigneResponse(LocalDate.of(2026, 1, 10), "ACH-2026-0002",
                        "Retour marchandises", "ACH",
                        BigDecimal.ZERO, new BigDecimal("200"), new BigDecimal("300"))
        );
        when(ligneEcriturePort.calculerGrandLivre(compteId, null, null)).thenReturn(lignes);

        List<GrandLivreLigneResponse> result = useCase.grandLivre(compteId, null, null);

        assertThat(result).hasSize(2);
        assertThat(result.get(1).soldeProgressif()).isEqualByComparingTo("300");
    }

    // --- Bilan ---

    @Test
    void bilan_repartitCorrectementActifEtPassif() {
        Filiale filiale = Filiale.builder().identifiant(filialeId).nom("Menko CI").build();
        when(filialePort.findById(filialeId)).thenReturn(Optional.of(filiale));

        LocalDate fin = LocalDate.of(2026, 12, 31);
        List<BalanceLigneResponse> balance = List.of(
                // Classe 1 créditeur → PASSIF
                new BalanceLigneResponse("101000", "Capital social", 1,
                        BigDecimal.ZERO, new BigDecimal("5000000"),
                        BigDecimal.ZERO, new BigDecimal("5000000")),
                // Classe 2 débiteur → ACTIF
                new BalanceLigneResponse("241000", "Matériel", 2,
                        new BigDecimal("200000"), BigDecimal.ZERO,
                        new BigDecimal("200000"), BigDecimal.ZERO),
                // Classe 5 débiteur → ACTIF
                new BalanceLigneResponse("521000", "Banque", 5,
                        new BigDecimal("150000"), BigDecimal.ZERO,
                        new BigDecimal("150000"), BigDecimal.ZERO)
        );
        when(ligneEcriturePort.calculerBalance(filialeId, null, fin)).thenReturn(balance);

        BilanResponse bilan = useCase.bilan(filialeId, fin);

        assertThat(bilan.filiale()).isEqualTo("Menko CI");
        // Classes 2 et 5 → actif (2 lignes)
        assertThat(bilan.actif()).hasSize(2);
        // Classe 1 créditeur → passif (1 ligne)
        assertThat(bilan.passif()).hasSize(1);
        assertThat(bilan.totalActif()).isEqualByComparingTo("350000");
        assertThat(bilan.totalPassif()).isEqualByComparingTo("5000000");
    }

    @Test
    void bilan_filialeInexistante_leveException() {
        when(filialePort.findById(filialeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.bilan(filialeId, LocalDate.now()))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    // --- Compte de résultat ---

    @Test
    void compteDeResultat_filtreUniquementClasses6et7() {
        List<BalanceLigneResponse> balance = List.of(
                ligne("411000", 4), // client → exclu
                ligne("601000", 6), // charge → inclus
                ligne("601500", 6), // charge → inclus
                ligne("701000", 7), // produit → inclus
                ligne("521000", 5)  // tréso → exclu
        );
        when(ligneEcriturePort.calculerBalance(filialeId, null, null)).thenReturn(balance);

        List<BalanceLigneResponse> result = useCase.compteDeResultat(filialeId, null, null);

        assertThat(result).hasSize(3);
        assertThat(result).allMatch(l -> l.classe() == 6 || l.classe() == 7);
    }

    private BalanceLigneResponse ligne(String numero, int classe) {
        return new BalanceLigneResponse(numero, "Libelle " + numero, classe,
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
