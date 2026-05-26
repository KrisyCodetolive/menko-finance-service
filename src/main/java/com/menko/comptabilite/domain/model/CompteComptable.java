package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Compte du plan SYSCOHADA par filiale. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompteComptable {
    private UUID identifiant;
    private String numero;
    private String libelle;
    /** Classe SYSCOHADA 1 à 7. */
    private Integer classe;
    /** "Debit" ou "Credit" */
    private String type;
    private Boolean actif;
    private UUID identifiantFiliale;
    private String nomFiliale;
}
