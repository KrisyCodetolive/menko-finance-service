package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Entité domaine : filiale du groupe Menko. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Filiale {
    private UUID identifiant;
    private String nom;
    /** "Holding" ou "Filiale" */
    private String type;
}
