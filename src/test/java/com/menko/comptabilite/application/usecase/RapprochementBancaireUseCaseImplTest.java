package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.LigneRapprochementRequest;
import com.menko.comptabilite.application.dto.request.RapprochementBancaireRequest;
import com.menko.comptabilite.application.dto.request.StatutLigneRapprochementRequest;
import com.menko.comptabilite.application.port.out.LigneRapprochementPort;
import com.menko.comptabilite.application.port.out.RapprochementBancairePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.LigneRapprochement;
import com.menko.comptabilite.domain.model.RapprochementBancaire;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RapprochementBancaireUseCaseImplTest {

    @Mock
    private RapprochementBancairePort rapprochementPort;

    @Mock
    private LigneRapprochementPort lignePort;

    @InjectMocks
    private RapprochementBancaireUseCaseImpl useCase;

    private final UUID compteId    = UUID.randomUUID();
    private final UUID userId      = UUID.randomUUID();
    private final UUID rapproId    = UUID.randomUUID();
    private final UUID ligneId     = UUID.randomUUID();

    private RapprochementBancaireRequest requeteValide() {
        return new RapprochementBancaireRequest(
                "Mai 2026", new BigDecimal("1250000"),
                LocalDate.of(2026, 5, 31), compteId);
    }

    @Test
    void ouvrirSession_nouvellePeriode_enregistreAvecStatutEnCours() {
        when(rapprochementPort.existsByCompteBancaireIdAndPeriode(compteId, "Mai 2026"))
                .thenReturn(false);
        when(rapprochementPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        RapprochementBancaire result = useCase.ouvrirSession(requeteValide(), userId);

        assertThat(result.getStatut()).isEqualTo("En cours");
        assertThat(result.getPeriode()).isEqualTo("Mai 2026");
        assertThat(result.getSoldeReleve()).isEqualByComparingTo("1250000");
        verify(rapprochementPort).save(any());
    }

    @Test
    void ouvrirSession_periodeDoublonnee_leveRegleMetierException() {
        when(rapprochementPort.existsByCompteBancaireIdAndPeriode(compteId, "Mai 2026"))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.ouvrirSession(requeteValide(), userId))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("existe déjà");

        verify(rapprochementPort, never()).save(any());
    }

    @Test
    void terminerSession_sessionEnCours_passaTermine() {
        RapprochementBancaire r = RapprochementBancaire.builder()
                .identifiant(rapproId).statut("En cours").build();
        when(rapprochementPort.findById(rapproId)).thenReturn(Optional.of(r));
        when(rapprochementPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.terminerSession(rapproId);

        verify(rapprochementPort).save(argThat(s -> "Terminé".equals(s.getStatut())));
    }

    @Test
    void terminerSession_sessionDejaTerminee_leveRegleMetierException() {
        RapprochementBancaire r = RapprochementBancaire.builder()
                .identifiant(rapproId).statut("Terminé").build();
        when(rapprochementPort.findById(rapproId)).thenReturn(Optional.of(r));

        assertThatThrownBy(() -> useCase.terminerSession(rapproId))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("déjà terminé");
    }

    @Test
    void trouverParId_inexistant_leveEntiteIntrouvableException() {
        when(rapprochementPort.findById(rapproId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.trouverParId(rapproId))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void ajouterLigne_rapprochementExistant_creeLigneEnAttente() {
        RapprochementBancaire r = RapprochementBancaire.builder()
                .identifiant(rapproId).statut("En cours").build();
        when(rapprochementPort.findById(rapproId)).thenReturn(Optional.of(r));

        LigneRapprochementRequest req = new LigneRapprochementRequest(
                LocalDate.of(2026, 5, 15), "Virement client", new BigDecimal("75000"), null);
        when(lignePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LigneRapprochement result = useCase.ajouterLigne(rapproId, req);

        assertThat(result.getStatut()).isEqualTo("En attente");
        assertThat(result.getMontantReleve()).isEqualByComparingTo("75000");
        assertThat(result.getIdentifiantRapprochement()).isEqualTo(rapproId);
    }

    @Test
    void ajouterLigne_rapprochementInexistant_leveException() {
        when(rapprochementPort.findById(rapproId)).thenReturn(Optional.empty());

        LigneRapprochementRequest req = new LigneRapprochementRequest(
                LocalDate.now(), "Test", BigDecimal.ONE, null);

        assertThatThrownBy(() -> useCase.ajouterLigne(rapproId, req))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void validerLigne_ligneExistante_metAJourStatutEtEcriture() {
        UUID ecritureId = UUID.randomUUID();
        LigneRapprochement ligne = LigneRapprochement.builder()
                .identifiant(ligneId).statut("En attente").build();
        when(lignePort.findById(ligneId)).thenReturn(Optional.of(ligne));
        when(lignePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        LigneRapprochement result = useCase.validerLigne(ligneId,
                new StatutLigneRapprochementRequest("Réconciliée", ecritureId));

        assertThat(result.getStatut()).isEqualTo("Réconciliée");
        assertThat(result.getIdentifiantEcriture()).isEqualTo(ecritureId);
    }

    @Test
    void validerLigne_ligneInexistante_leveException() {
        when(lignePort.findById(ligneId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.validerLigne(ligneId,
                new StatutLigneRapprochementRequest("Réconciliée", null)))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void validerLigne_derniereLigneReconciliee_autoClotureLaSession() {
        LigneRapprochement ligne = LigneRapprochement.builder()
                .identifiant(ligneId).statut("En attente")
                .identifiantRapprochement(rapproId).build();
        RapprochementBancaire rapprochement = RapprochementBancaire.builder()
                .identifiant(rapproId).statut("En cours").build();

        when(lignePort.findById(ligneId)).thenReturn(Optional.of(ligne));
        when(lignePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(lignePort.findByRapprochementId(rapproId)).thenReturn(List.of(
                LigneRapprochement.builder().identifiant(ligneId)
                        .statut("Réconciliée").identifiantRapprochement(rapproId).build()));
        when(rapprochementPort.findById(rapproId)).thenReturn(Optional.of(rapprochement));
        when(rapprochementPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.validerLigne(ligneId, new StatutLigneRapprochementRequest("Réconciliée", null));

        verify(rapprochementPort).save(argThat(r -> "Terminé".equals(r.getStatut())));
    }

    @Test
    void validerLigne_lignesRestantesEnAttente_neCloturePasSession() {
        UUID autreId = UUID.randomUUID();
        LigneRapprochement ligne = LigneRapprochement.builder()
                .identifiant(ligneId).statut("En attente")
                .identifiantRapprochement(rapproId).build();

        when(lignePort.findById(ligneId)).thenReturn(Optional.of(ligne));
        when(lignePort.save(any())).thenAnswer(inv -> inv.getArgument(0));
        when(lignePort.findByRapprochementId(rapproId)).thenReturn(List.of(
                LigneRapprochement.builder().identifiant(ligneId)
                        .statut("Réconciliée").identifiantRapprochement(rapproId).build(),
                LigneRapprochement.builder().identifiant(autreId)
                        .statut("En attente").identifiantRapprochement(rapproId).build()));

        useCase.validerLigne(ligneId, new StatutLigneRapprochementRequest("Réconciliée", null));

        verify(rapprochementPort, never()).save(any());
    }
}
