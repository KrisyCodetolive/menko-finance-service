package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.LoginRequest;
import com.menko.comptabilite.application.dto.request.UtilisateurRequest;
import com.menko.comptabilite.application.dto.response.TokenResponse;
import com.menko.comptabilite.application.port.in.AuthUseCase;
import com.menko.comptabilite.application.port.out.RolePort;
import com.menko.comptabilite.application.port.out.UtilisateurPort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.Role;
import com.menko.comptabilite.domain.model.Utilisateur;
import com.menko.comptabilite.infrastructure.security.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUseCaseImpl implements AuthUseCase {

    private final UtilisateurPort utilisateurPort;
    private final RolePort rolePort;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    @Override
    public TokenResponse connecter(LoginRequest request) {
        Utilisateur utilisateur = utilisateurPort.findByEmail(request.email())
                .orElseThrow(() -> new RegleMetierException("Email ou mot de passe incorrect"));

        if (!utilisateur.isActif()) {
            throw new RegleMetierException("Compte désactivé");
        }

        if (!passwordEncoder.matches(request.motDePasse(), utilisateur.getMotDePasse())) {
            throw new RegleMetierException("Email ou mot de passe incorrect");
        }

        String token = jwtUtil.genererToken(utilisateur);
        return TokenResponse.bearer(token, utilisateur.getIdentifiant(), utilisateur.getEmail(),
                utilisateur.getNomRole(), utilisateur.getIdentifiantFiliale(), jwtUtil.getExpirationMs());
    }

    @Override
    public Utilisateur creerUtilisateur(UtilisateurRequest request) {
        if (utilisateurPort.existsByEmail(request.email())) {
            throw new RegleMetierException("Un utilisateur avec cet email existe déjà");
        }

        Role role = rolePort.findById(request.identifiantRole())
                .orElseThrow(() -> new EntiteIntrouvableException("Rôle", request.identifiantRole()));

        Utilisateur utilisateur = Utilisateur.builder()
                .nom(request.nom())
                .prenom(request.prenom())
                .email(request.email())
                .motDePasse(passwordEncoder.encode(request.motDePasse()))
                .identifiantRole(role.getIdentifiant())
                .nomRole(role.getNom())
                .identifiantFiliale(request.identifiantFiliale())
                .actif(true)
                .build();

        return utilisateurPort.save(utilisateur);
    }

    @Override
    public Utilisateur modifierUtilisateur(UUID id, UtilisateurRequest request) {
        Utilisateur utilisateur = utilisateurPort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Utilisateur", id));

        Role role = rolePort.findById(request.identifiantRole())
                .orElseThrow(() -> new EntiteIntrouvableException("Rôle", request.identifiantRole()));

        utilisateur.setNom(request.nom());
        utilisateur.setPrenom(request.prenom());
        utilisateur.setEmail(request.email());
        if (request.motDePasse() != null && !request.motDePasse().isBlank()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(request.motDePasse()));
        }
        utilisateur.setIdentifiantRole(role.getIdentifiant());
        utilisateur.setNomRole(role.getNom());
        utilisateur.setIdentifiantFiliale(request.identifiantFiliale());

        return utilisateurPort.save(utilisateur);
    }

    @Override
    public void desactiverUtilisateur(UUID id) {
        Utilisateur utilisateur = utilisateurPort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Utilisateur", id));
        utilisateur.setActif(false);
        utilisateurPort.save(utilisateur);
    }

    @Override
    public List<Utilisateur> listerUtilisateurs() {
        return utilisateurPort.findAll();
    }
}
