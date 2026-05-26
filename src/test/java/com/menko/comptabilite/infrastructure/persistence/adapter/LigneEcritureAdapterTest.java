package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.infrastructure.persistence.repository.LigneEcritureJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LigneEcritureAdapterTest {

    @Mock
    private LigneEcritureJpaRepository repository;

    @InjectMocks
    private LigneEcritureAdapter adapter;

    private final UUID compteId = UUID.randomUUID();

    @Test
    void calculerGrandLivre_calculeLesSoldesProgressifsCumulatifs() {
        // 3 mouvements : débit 1000, crédit 300, débit 500
        // Soldes attendus : 1000, 700, 1200
        List<GrandLivreLigneResponse> raw = List.of(
                raw(LocalDate.of(2026, 1, 1), "ACH-2026-0001", "Achat A",
                        new BigDecimal("1000"), BigDecimal.ZERO),
                raw(LocalDate.of(2026, 1, 5), "OD-2026-0001", "Remise",
                        BigDecimal.ZERO, new BigDecimal("300")),
                raw(LocalDate.of(2026, 1, 10), "ACH-2026-0002", "Achat B",
                        new BigDecimal("500"), BigDecimal.ZERO)
        );
        when(repository.calculerGrandLivreRaw(compteId, null, null)).thenReturn(raw);

        List<GrandLivreLigneResponse> result = adapter.calculerGrandLivre(compteId, null, null);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).soldeProgressif()).isEqualByComparingTo("1000");
        assertThat(result.get(1).soldeProgressif()).isEqualByComparingTo("700");
        assertThat(result.get(2).soldeProgressif()).isEqualByComparingTo("1200");
    }

    @Test
    void calculerGrandLivre_listeVide_retourneListeVide() {
        when(repository.calculerGrandLivreRaw(compteId, null, null)).thenReturn(List.of());

        List<GrandLivreLigneResponse> result = adapter.calculerGrandLivre(compteId, null, null);

        assertThat(result).isEmpty();
    }

    @Test
    void calculerGrandLivre_conserveLesMetadonneesDeLaLigne() {
        LocalDate date = LocalDate.of(2026, 3, 15);
        List<GrandLivreLigneResponse> raw = List.of(
                raw(date, "VTE-2026-0010", "Vente client", new BigDecimal("250"), BigDecimal.ZERO)
        );
        when(repository.calculerGrandLivreRaw(compteId, null, null)).thenReturn(raw);

        GrandLivreLigneResponse ligne = adapter.calculerGrandLivre(compteId, null, null).get(0);

        assertThat(ligne.dateEcriture()).isEqualTo(date);
        assertThat(ligne.numeroPiece()).isEqualTo("VTE-2026-0010");
        assertThat(ligne.libelle()).isEqualTo("Vente client");
        assertThat(ligne.debit()).isEqualByComparingTo("250");
        assertThat(ligne.credit()).isEqualByComparingTo("0");
    }

    private GrandLivreLigneResponse raw(LocalDate date, String piece, String libelle,
                                         BigDecimal debit, BigDecimal credit) {
        return new GrandLivreLigneResponse(date, piece, libelle, "JNL", debit, credit, BigDecimal.ZERO);
    }
}
