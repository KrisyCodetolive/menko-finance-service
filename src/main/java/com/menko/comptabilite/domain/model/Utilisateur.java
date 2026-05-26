package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** Compte utilisateur de l'application. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {
    private UUID identifiant;
    private String nom;
    private String prenom;
    private String email;
    private String motDePasse;
    private UUID identifiantRole;
    private String nomRole;
    /** Filiale rattachée (null pour Super Admin et DG). */
    private UUID identifiantFiliale;
    private String nomFiliale;
    private boolean actif;
    private LocalDateTime dateCreation;
}
