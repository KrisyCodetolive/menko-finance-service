package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Client, fournisseur ou les deux. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Tiers {
    private UUID identifiant;
    private String nom;
    /** "Client", "Fournisseur" ou "Les deux" */
    private String type;
    private String telephone;
    private String email;
    private String adresse;
    private UUID identifiantFiliale;
    private String nomFiliale;
}
