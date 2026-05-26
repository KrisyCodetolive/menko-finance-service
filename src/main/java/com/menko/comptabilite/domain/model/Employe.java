package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Employé d'une filiale. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Employe {
    private UUID identifiant;
    private String nom;
    private String prenom;
    private String poste;
    private BigDecimal salaireBase;
    private LocalDate dateEmbauche;
    private Boolean actif;
    private UUID identifiantFiliale;
    private String nomFiliale;
}
