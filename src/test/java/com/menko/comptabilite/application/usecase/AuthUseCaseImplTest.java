package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.LoginRequest;
import com.menko.comptabilite.application.dto.request.UtilisateurRequest;
import com.menko.comptabilite.application.dto.response.TokenResponse;
import com.menko.comptabilite.application.port.out.RolePort;
import com.menko.comptabilite.application.port.out.UtilisateurPort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.Utilisateur;
import com.menko.comptabilite.infrastructure.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthUseCaseImplTest {

    @Mock private UtilisateurPort utilisateurPort;
    @Mock private RolePort rolePort;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;

    @InjectMocks
    private AuthUseCaseImpl useCase;

    private final UUID userId   = UUID.randomUUID();
    private final UUID roleId   = UUID.randomUUID();
    private final UUID filialeId = UUID.randomUUID();

    private Utilisateur utilisateurActif() {
        return Utilisateur.builder()
                .identifiant(userId)
                .email("test@menko.ci")
                .motDePasse("$2a$bcrypt")
                .nomRole("COMPTABLE")
                .identifiantFiliale(filialeId)
                .actif(true)
                .build();
    }

    @Test
    void connecter_credentialsValides_retourneToken() {
        Utilisateur u = utilisateurActif();
        when(utilisateurPort.findByEmail("test@menko.ci")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches("secret", "$2a$bcrypt")).thenReturn(true);
        when(jwtUtil.genererToken(u)).thenReturn("jwt.token.here");
        when(jwtUtil.getExpirationMs()).thenReturn(86400000L);

        TokenResponse response = useCase.connecter(new LoginRequest("test@menko.ci", "secret"));

        assertThat(response.token()).isEqualTo("jwt.token.here");
        assertThat(response.type()).isEqualTo("Bearer");
        assertThat(response.utilisateurId()).isEqualTo(userId);
        assertThat(response.role()).isEqualTo("COMPTABLE");
    }

    @Test
    void connecter_emailInexistant_leveRegleMetierException() {
        when(utilisateurPort.findByEmail(anyString())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.connecter(new LoginRequest("inconnu@menko.ci", "x")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("incorrect");
    }

    @Test
    void connecter_compteDesactive_leveRegleMetierException() {
        Utilisateur inactif = utilisateurActif();
        inactif.setActif(false);
        when(utilisateurPort.findByEmail(anyString())).thenReturn(Optional.of(inactif));

        assertThatThrownBy(() -> useCase.connecter(new LoginRequest("test@menko.ci", "secret")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("désactivé");
    }

    @Test
    void connecter_mauvaisMotDePasse_leveRegleMetierException() {
        Utilisateur u = utilisateurActif();
        when(utilisateurPort.findByEmail("test@menko.ci")).thenReturn(Optional.of(u));
        when(passwordEncoder.matches(anyString(), anyString())).thenReturn(false);

        assertThatThrownBy(() -> useCase.connecter(new LoginRequest("test@menko.ci", "wrong")))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("incorrect");
    }

    @Test
    void creerUtilisateur_emailDejaExistant_leveRegleMetierException() {
        when(utilisateurPort.existsByEmail("dup@menko.ci")).thenReturn(true);

        UtilisateurRequest request = new UtilisateurRequest(
                "Doe", "John", "dup@menko.ci", "pass", roleId, filialeId);

        assertThatThrownBy(() -> useCase.creerUtilisateur(request))
                .isInstanceOf(RegleMetierException.class)
                .hasMessageContaining("email");
    }

    @Test
    void creerUtilisateur_roleInexistant_leveEntiteIntrouvableException() {
        when(utilisateurPort.existsByEmail(anyString())).thenReturn(false);
        when(rolePort.findById(roleId)).thenReturn(Optional.empty());

        UtilisateurRequest request = new UtilisateurRequest(
                "Doe", "John", "new@menko.ci", "pass", roleId, null);

        assertThatThrownBy(() -> useCase.creerUtilisateur(request))
                .isInstanceOf(EntiteIntrouvableException.class);
    }

    @Test
    void desactiverUtilisateur_existant_setActifFalse() {
        Utilisateur u = utilisateurActif();
        when(utilisateurPort.findById(userId)).thenReturn(Optional.of(u));
        when(utilisateurPort.save(any())).thenAnswer(inv -> inv.getArgument(0));

        useCase.desactiverUtilisateur(userId);

        verify(utilisateurPort).save(argThat(saved -> !saved.isActif()));
    }
}
