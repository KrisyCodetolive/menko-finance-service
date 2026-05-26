package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.response.DashboardFilialeResponse;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.Filiale;
import com.menko.comptabilite.infrastructure.persistence.repository.ChequeJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteComptableJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteBancaireJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.EcritureComptableJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.LigneEcritureJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.RapprochementBancaireJpaRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardUseCaseImplTest {

    @Mock private FilialePort filialePort;
    @Mock private EcritureComptableJpaRepository ecritureRepo;
    @Mock private CompteComptableJpaRepository compteRepo;
    @Mock private LigneEcritureJpaRepository ligneRepo;
    @Mock private ChequeJpaRepository chequeRepo;
    @Mock private RapprochementBancaireJpaRepository rapprochementRepo;
    @Mock private CompteBancaireJpaRepository compteBancaireRepo;

    @InjectMocks
    private DashboardUseCaseImpl useCase;

    private final UUID filialeId = UUID.randomUUID();

    private void stubDashboardNominal() {
        Filiale filiale = Filiale.builder().identifiant(filialeId).nom("Menko CI").build();
        when(filialePort.findById(filialeId)).thenReturn(Optional.of(filiale));
        when(ecritureRepo.countByFilialeIdentifiant(filialeId)).thenReturn(42L);
        when(compteRepo.countByFilialeIdentifiant(filialeId)).thenReturn(15L);
        when(ligneRepo.sumTotalDebit(filialeId)).thenReturn(new BigDecimal("5000000"));
        when(ligneRepo.sumTotalCredit(filialeId)).thenReturn(new BigDecimal("4800000"));
        when(ligneRepo.sumSoldeTresorerie(filialeId)).thenReturn(new BigDecimal("200000"));
        // Pas de comptes bancaires → chequesEnCours = 0, rapprochementsEnCours = 0
        when(compteBancaireRepo.findByFilialeIdentifiant(filialeId)).thenReturn(new ArrayList<>());
        when(ecritureRepo.findByFilialeAndPeriode(eq(filialeId), isNull(), isNull(), any()))
                .thenReturn(new PageImpl<>(new ArrayList<>()));
    }

    @Test
    void dashboardFiliale_filialeExistante_retourneAgregatsCorrects() {
        stubDashboardNominal();

        DashboardFilialeResponse result = useCase.dashboardFiliale(filialeId);

        assertThat(result.nomFiliale()).isEqualTo("Menko CI");
        assertThat(result.nombreEcritures()).isEqualTo(42L);
        assertThat(result.nombreComptes()).isEqualTo(15L);
        assertThat(result.totalDebit()).isEqualByComparingTo("5000000");
        assertThat(result.totalCredit()).isEqualByComparingTo("4800000");
        assertThat(result.soldeTresorerie()).isEqualByComparingTo("200000");
        assertThat(result.chequesEnCours()).isEqualTo(0L);
        assertThat(result.rapprochementsEnCours()).isEqualTo(0L);
        assertThat(result.dernieresEcritures()).isEmpty();
    }

    @Test
    void dashboardFiliale_filialeInexistante_leveEntiteIntrouvableException() {
        when(filialePort.findById(filialeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.dashboardFiliale(filialeId))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void dashboardGroupe_invoqueDashboardPourChaqueFiliale() {
        Filiale f1 = Filiale.builder().identifiant(filialeId).nom("Menko CI").build();
        UUID filialeId2 = UUID.randomUUID();
        Filiale f2 = Filiale.builder().identifiant(filialeId2).nom("Menko SN").build();
        when(filialePort.findAll()).thenReturn(List.of(f1, f2));

        // Stub complet pour les deux filiales
        for (UUID id : List.of(filialeId, filialeId2)) {
            Filiale f = id.equals(filialeId) ? f1 : f2;
            when(filialePort.findById(id)).thenReturn(Optional.of(f));
            when(ecritureRepo.countByFilialeIdentifiant(id)).thenReturn(0L);
            when(compteRepo.countByFilialeIdentifiant(id)).thenReturn(0L);
            when(ligneRepo.sumTotalDebit(id)).thenReturn(BigDecimal.ZERO);
            when(ligneRepo.sumTotalCredit(id)).thenReturn(BigDecimal.ZERO);
            when(ligneRepo.sumSoldeTresorerie(id)).thenReturn(BigDecimal.ZERO);
            when(compteBancaireRepo.findByFilialeIdentifiant(id)).thenReturn(new ArrayList<>());
            when(ecritureRepo.findByFilialeAndPeriode(eq(id), isNull(), isNull(), any()))
                    .thenReturn(new PageImpl<>(new ArrayList<>()));
        }

        List<DashboardFilialeResponse> result = useCase.dashboardGroupe();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(DashboardFilialeResponse::nomFiliale)
                .containsExactly("Menko CI", "Menko SN");
    }
}
