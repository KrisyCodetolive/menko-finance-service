package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.Utilisateur;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UtilisateurPort {
    List<Utilisateur> findAll();
    Optional<Utilisateur> findById(UUID id);
    Optional<Utilisateur> findByEmail(String email);
    Utilisateur save(Utilisateur utilisateur);
    boolean existsByEmail(String email);
}
