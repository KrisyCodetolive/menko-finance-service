package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Rôle utilisateur : Super Administrateur, Directeur Général, DFC, Trésorier. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {
    private UUID identifiant;
    private String nom;
}
