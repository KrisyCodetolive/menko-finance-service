package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.EcritureComptableRequest;
import com.menko.comptabilite.application.dto.request.LigneEcritureRequest;
import com.menko.comptabilite.application.port.out.EcritureComptablePort;
import com.menko.comptabilite.application.port.out.JournalComptablePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.EcritureComptable;
import com.menko.comptabilite.domain.model.JournalComptable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Year;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EcritureComptableUseCaseImplTest {

    @Mock
    private EcritureComptablePort ecriturePort;

    @Mock
    private JournalComptablePort journalPort;

    @InjectMocks
    private EcritureComptableUseCaseImpl useCase;

    private final UUID journalId  = UUID.randomUUID();
    private final UUID filialeId  = UUID.randomUUID();
    private final UUID userId     = UUID.randomUUID();
    private final UUID compteId   = UUID.randomUUID();

    @Test
    void creer_ecritureEquilibree_succes() {
        List<LigneEcritureRequest> lignes = List.of(
                new LigneEcritureRequest(compteId, new BigDecimal("1000"), BigDecimal.ZERO),
                new LigneEcritureRequest(compteId, BigDecimal.ZERO, new BigDecimal("1000"))
        );
        EcritureComptableRequest request = new EcritureComptableRequest(
                LocalDate.now(), "Achat marchandises", journalId, filialeId, null, lignes);

        JournalComptable journal = JournalComptable.builder()
                .identifiant(journalId).code("ACH").build();
        when(journalPort.findById(journalId)).thenReturn(Optional.of(journal));
        when(ecriturePort.findMaxNumeroPiece(eq("ACH"), anyInt(), eq(filialeId)))
                .thenReturn(Optional.empty());
        when(ecriturePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EcritureComptable result = useCase.creer(request, userId);

        assertThat(result.getNumeroPiece())
                .matches("ACH-" + Year.now().getValue() + "-0001");
        assertThat(result.getLignes()).hasSize(2);
    }

    @Test
    void creer_ecritureDesequilibree_leveException() {
        List<LigneEcritureRequest> lignes = List.of(
                new LigneEcritureRequest(compteId, new BigDecimal("1000"), BigDecimal.ZERO),
                new LigneEcritureRequest(compteId, BigDecimal.ZERO, new BigDecimal("900"))
        );
        EcritureComptableRequest request = new EcritureComptableRequest(
                LocalDate.now(), "Écriture déséquilibrée", journalId, filialeId, null, lignes);

        assertThatThrownBy(() -> useCase.creer(request, userId))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("équilibrée");
    }

    @Test
    void creer_journalInexistant_leveException() {
        List<LigneEcritureRequest> lignes = List.of(
                new LigneEcritureRequest(compteId, new BigDecimal("500"), BigDecimal.ZERO),
                new LigneEcritureRequest(compteId, BigDecimal.ZERO, new BigDecimal("500"))
        );
        EcritureComptableRequest request = new EcritureComptableRequest(
                LocalDate.now(), "Test", journalId, filialeId, null, lignes);
        when(journalPort.findById(journalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.creer(request, userId))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void creer_numeroPieceSequentiel_incrementeCorrectement() {
        List<LigneEcritureRequest> lignes = List.of(
                new LigneEcritureRequest(compteId, new BigDecimal("200"), BigDecimal.ZERO),
                new LigneEcritureRequest(compteId, BigDecimal.ZERO, new BigDecimal("200"))
        );
        EcritureComptableRequest request = new EcritureComptableRequest(
                LocalDate.now(), "Vente", journalId, filialeId, null, lignes);

        int annee = Year.now().getValue();
        JournalComptable journal = JournalComptable.builder()
                .identifiant(journalId).code("VTE").build();
        when(journalPort.findById(journalId)).thenReturn(Optional.of(journal));
        when(ecriturePort.findMaxNumeroPiece("VTE", annee, filialeId))
                .thenReturn(Optional.of("VTE-" + annee + "-0042"));
        when(ecriturePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        EcritureComptable result = useCase.creer(request, userId);

        assertThat(result.getNumeroPiece()).isEqualTo("VTE-" + annee + "-0043");
    }

    @Test
    void supprimer_ecritureInexistante_leveException() {
        UUID id = UUID.randomUUID();
        when(ecriturePort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.supprimer(id))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void modifier_ecritureDesequilibree_leveException() {
        UUID id = UUID.randomUUID();
        EcritureComptable existante = EcritureComptable.builder().identifiant(id).build();
        when(ecriturePort.findById(id)).thenReturn(Optional.of(existante));

        List<LigneEcritureRequest> lignesDesequilibrees = List.of(
                new LigneEcritureRequest(compteId, new BigDecimal("300"), BigDecimal.ZERO),
                new LigneEcritureRequest(compteId, BigDecimal.ZERO, new BigDecimal("100"))
        );
        EcritureComptableRequest request = new EcritureComptableRequest(
                LocalDate.now(), "Modif", journalId, filialeId, null, lignesDesequilibrees);

        assertThatThrownBy(() -> useCase.modifier(id, request))
                .isInstanceOf(RegleMetierException.class);
    }
}
