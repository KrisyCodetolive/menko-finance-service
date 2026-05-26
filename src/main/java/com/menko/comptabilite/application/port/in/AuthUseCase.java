package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.LoginRequest;
import com.menko.comptabilite.application.dto.request.UtilisateurRequest;
import com.menko.comptabilite.application.dto.response.TokenResponse;
import com.menko.comptabilite.domain.model.Utilisateur;

/** Authentification et gestion des comptes utilisateurs. */
public interface AuthUseCase {
    TokenResponse connecter(LoginRequest request);
    Utilisateur creerUtilisateur(UtilisateurRequest request);
    Utilisateur modifierUtilisateur(java.util.UUID id, UtilisateurRequest request);
    void desactiverUtilisateur(java.util.UUID id);
    java.util.List<Utilisateur> listerUtilisateurs();
}
