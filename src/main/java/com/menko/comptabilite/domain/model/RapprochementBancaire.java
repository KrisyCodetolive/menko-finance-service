package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Session de rapprochement bancaire (un par compte par période). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RapprochementBancaire {
    private UUID identifiant;
    private String periode;
    /** "En cours" ou "Terminé" */
    private String statut;
    private BigDecimal soldeReleve;
    private LocalDate dateImport;
    private UUID identifiantCompteBancaire;
    private String nomBanque;
    private String numeroCompte;
    private UUID identifiantUtilisateur;
    private String nomUtilisateur;
}
