package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.ChequeRequest;
import com.menko.comptabilite.application.dto.request.StatutChequeRequest;
import com.menko.comptabilite.application.port.out.ChequePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.Cheque;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ChequeUseCaseImplTest {

    @Mock
    private ChequePort chequePort;

    @InjectMocks
    private ChequeUseCaseImpl useCase;

    private final UUID compteId  = UUID.randomUUID();
    private final UUID filialeId = UUID.randomUUID();
    private final UUID chequeId  = UUID.randomUUID();

    private ChequeRequest requeteValide() {
        return new ChequeRequest("EMIS", "CHQ-001", "Fournisseur SA",
                new BigDecimal("150000"), "Achat fournitures",
                LocalDate.of(2026, 5, 10), compteId, null);
    }

    @Test
    void emettre_creerChequeAvecStatutEnCours() {
        when(chequePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cheque result = useCase.emettre(requeteValide());

        assertThat(result.getStatut()).isEqualTo("En cours");
        assertThat(result.getType()).isEqualTo("EMIS");
        assertThat(result.getMontant()).isEqualByComparingTo("150000");
        verify(chequePort).save(any());
    }

    @Test
    void trouverParId_inexistant_leveEntiteIntrouvableException() {
        when(chequePort.findById(chequeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.trouverParId(chequeId))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void listerParCompteBancaire_sansStatut_appeleFindAll() {
        Page<Cheque> page = new PageImpl<>(new ArrayList<>());
        when(chequePort.findByCompteBancaireId(eq(compteId), any())).thenReturn(page);

        useCase.listerParCompteBancaire(compteId, null, Pageable.unpaged());

        verify(chequePort).findByCompteBancaireId(eq(compteId), any());
        verify(chequePort, never()).findByCompteBancaireIdAndStatut(any(), any(), any());
    }

    @Test
    void listerParCompteBancaire_avecStatutVide_appeleFindAll() {
        Page<Cheque> page = new PageImpl<>(new ArrayList<>());
        when(chequePort.findByCompteBancaireId(eq(compteId), any())).thenReturn(page);

        useCase.listerParCompteBancaire(compteId, "  ", Pageable.unpaged());

        verify(chequePort).findByCompteBancaireId(eq(compteId), any());
    }

    @Test
    void listerParCompteBancaire_avecStatut_appeleFindByStatut() {
        Page<Cheque> page = new PageImpl<>(new ArrayList<>());
        when(chequePort.findByCompteBancaireIdAndStatut(eq(compteId), eq("Encaissé"), any()))
                .thenReturn(page);

        useCase.listerParCompteBancaire(compteId, "Encaissé", Pageable.unpaged());

        verify(chequePort).findByCompteBancaireIdAndStatut(eq(compteId), eq("Encaissé"), any());
        verify(chequePort, never()).findByCompteBancaireId(any(), any());
    }

    @Test
    void mettreAJourStatut_metsAJourStatutEtDateEncaissement() {
        Cheque cheque = Cheque.builder().identifiant(chequeId).statut("En cours").build();
        when(chequePort.findById(chequeId)).thenReturn(Optional.of(cheque));
        when(chequePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LocalDate dateEnc = LocalDate.of(2026, 5, 20);
        Cheque result = useCase.mettreAJourStatut(chequeId,
                new StatutChequeRequest("Encaissé", dateEnc));

        assertThat(result.getStatut()).isEqualTo("Encaissé");
        assertThat(result.getDateEncaissement()).isEqualTo(dateEnc);
    }

    @Test
    void mettreAJourStatut_sansDateEncaissement_conserveStatutSeulement() {
        Cheque cheque = Cheque.builder().identifiant(chequeId).statut("En cours").build();
        when(chequePort.findById(chequeId)).thenReturn(Optional.of(cheque));
        when(chequePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        Cheque result = useCase.mettreAJourStatut(chequeId,
                new StatutChequeRequest("Remis en banque", null));

        assertThat(result.getStatut()).isEqualTo("Remis en banque");
        assertThat(result.getDateEncaissement()).isNull();
    }

    @Test
    void mettreAJourStatut_statutTerminal_leveRegleMetierException() {
        Cheque cheque = Cheque.builder().identifiant(chequeId).statut("Encaissé").build();
        when(chequePort.findById(chequeId)).thenReturn(Optional.of(cheque));

        assertThatThrownBy(() -> useCase.mettreAJourStatut(chequeId,
                new StatutChequeRequest("En cours", null)))
                .isInstanceOf(com.menko.comptabilite.domain.exception.RegleMetierException.class)
                .hasMessageContaining("Encaissé");
    }

    @Test
    void mettreAJourStatut_retourArriere_leveRegleMetierException() {
        Cheque cheque = Cheque.builder().identifiant(chequeId).statut("Remis en banque").build();
        when(chequePort.findById(chequeId)).thenReturn(Optional.of(cheque));

        assertThatThrownBy(() -> useCase.mettreAJourStatut(chequeId,
                new StatutChequeRequest("En cours", null)))
                .isInstanceOf(com.menko.comptabilite.domain.exception.RegleMetierException.class)
                .hasMessageContaining("Remis en banque");
    }

    @Test
    void alertesChequesNonEncaisses_delegueAuPortAvecDateLimite() {
        when(chequePort.findChequesNonEncaissesBefore(eq(filialeId), any())).thenReturn(List.of());

        useCase.alertesChequesNonEncaisses(filialeId, 30);

        verify(chequePort).findChequesNonEncaissesBefore(eq(filialeId),
                argThat(d -> d.isBefore(LocalDate.now())));
    }
}
