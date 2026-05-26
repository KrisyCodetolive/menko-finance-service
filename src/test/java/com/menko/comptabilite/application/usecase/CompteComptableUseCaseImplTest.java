package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.CompteComptableRequest;
import com.menko.comptabilite.application.port.out.CompteComptablePort;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.CompteComptable;
import com.menko.comptabilite.domain.model.Filiale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompteComptableUseCaseImplTest {

    @Mock
    private CompteComptablePort comptePort;

    @Mock
    private FilialePort filialePort;

    @InjectMocks
    private CompteComptableUseCaseImpl useCase;

    private UUID filialeId;
    private Filiale filiale;

    @BeforeEach
    void setUp() {
        filialeId = UUID.randomUUID();
        filiale = Filiale.builder().identifiant(filialeId).nom("Test Filiale").build();
    }

    @Test
    void creer_premierCompteDeClasse_genereNumeroPrefixePlusUn() {
        CompteComptableRequest request = new CompteComptableRequest(
                "Capitaux propres", 1, "Credit", filialeId);

        when(filialePort.findById(filialeId)).thenReturn(Optional.of(filiale));
        when(comptePort.findMaxNumeroByClasseAndFilialeId(1, filialeId)).thenReturn(Optional.empty());
        when(comptePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CompteComptable result = useCase.creer(request);

        assertThat(result.getNumero()).isEqualTo("100001");
        assertThat(result.getClasse()).isEqualTo(1);
        assertThat(result.isActif()).isTrue();
    }

    @Test
    void creer_compteExistant_incremente() {
        CompteComptableRequest request = new CompteComptableRequest(
                "Compte courant", 5, "Debit", filialeId);

        when(filialePort.findById(filialeId)).thenReturn(Optional.of(filiale));
        when(comptePort.findMaxNumeroByClasseAndFilialeId(5, filialeId)).thenReturn(Optional.of("512100"));
        when(comptePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        CompteComptable result = useCase.creer(request);

        assertThat(result.getNumero()).isEqualTo("512101");
    }

    @Test
    void creer_filialInexistante_leveException() {
        CompteComptableRequest request = new CompteComptableRequest(
                "Libelle", 1, "Debit", filialeId);
        when(filialePort.findById(filialeId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.creer(request))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void archiver_compteExistant_metActifAFalse() {
        UUID compteId = UUID.randomUUID();
        CompteComptable compte = CompteComptable.builder()
                .identifiant(compteId).actif(true).build();
        when(comptePort.findById(compteId)).thenReturn(Optional.of(compte));
        when(comptePort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.archiver(compteId);

        verify(comptePort).save(argThat(c -> !c.isActif()));
    }

    @Test
    void archiver_compteInexistant_leveException() {
        UUID compteId = UUID.randomUUID();
        when(comptePort.findById(compteId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.archiver(compteId))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void creer_numeroSature_leveRegleMetierException() {
        // Classe 1, dernier numéro est 1 suivi de 5 nines → incrémente → dépasse préfixe
        CompteComptableRequest request = new CompteComptableRequest(
                "Compte test", 1, "Credit", filialeId);
        when(filialePort.findById(filialeId)).thenReturn(Optional.of(filiale));
        when(comptePort.findMaxNumeroByClasseAndFilialeId(1, filialeId))
                .thenReturn(Optional.of("199999"));

        assertThatThrownBy(() -> useCase.creer(request))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("saturée");
    }
}
