package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.BulletinPaieRequest;
import com.menko.comptabilite.application.port.out.BulletinPaiePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.BulletinPaie;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BulletinPaieUseCaseImplTest {

    @Mock
    private BulletinPaiePort bulletinPort;

    @InjectMocks
    private BulletinPaieUseCaseImpl useCase;

    private final UUID employeId   = UUID.randomUUID();
    private final UUID filialeId   = UUID.randomUUID();
    private final UUID ecritureId  = UUID.randomUUID();

    private BulletinPaieRequest requeteValide() {
        return new BulletinPaieRequest(
                5, 2026,
                new BigDecimal("500000"),
                new BigDecimal("50000"),
                new BigDecimal("20000"),
                new BigDecimal("430000"),
                new BigDecimal("75000"),
                employeId, ecritureId);
    }

    @Test
    void creer_bulletinNouveau_enregistreAvecSucces() {
        when(bulletinPort.existsByEmployeIdAndMoisAndAnnee(employeId, 5, 2026)).thenReturn(false);
        when(bulletinPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        BulletinPaie result = useCase.creer(requeteValide());

        assertThat(result.getMois()).isEqualTo(5);
        assertThat(result.getAnnee()).isEqualTo(2026);
        assertThat(result.getSalaireNet()).isEqualByComparingTo("430000");
        verify(bulletinPort).save(any());
    }

    @Test
    void creer_bulletinDoublon_leveRegleMetierException() {
        when(bulletinPort.existsByEmployeIdAndMoisAndAnnee(employeId, 5, 2026)).thenReturn(true);

        assertThatThrownBy(() -> useCase.creer(requeteValide()))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("existe déjà");

        verify(bulletinPort, never()).save(any());
    }

    @Test
    void trouverParId_inexistant_leveEntiteIntrouvableException() {
        UUID id = UUID.randomUUID();
        when(bulletinPort.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.trouverParId(id))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void trouverParId_existant_retourneBulletin() {
        UUID id = UUID.randomUUID();
        BulletinPaie bulletin = BulletinPaie.builder().identifiant(id).mois(3).annee(2026).build();
        when(bulletinPort.findById(id)).thenReturn(Optional.of(bulletin));

        BulletinPaie result = useCase.trouverParId(id);

        assertThat(result.getIdentifiant()).isEqualTo(id);
    }

    @Test
    void listerParEmploye_delegueAuPort() {
        when(bulletinPort.findByEmployeId(employeId)).thenReturn(List.of());

        useCase.listerParEmploye(employeId);

        verify(bulletinPort).findByEmployeId(employeId);
    }

    @Test
    void listerParFilialeEtPeriode_delegueAuPort() {
        when(bulletinPort.findByFilialeIdAndPeriode(filialeId, 5, 2026)).thenReturn(List.of());

        useCase.listerParFilialeEtPeriode(filialeId, 5, 2026);

        verify(bulletinPort).findByFilialeIdAndPeriode(filialeId, 5, 2026);
    }
}
