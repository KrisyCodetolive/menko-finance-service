package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Compte bancaire d'une filiale (SGCI, Ecobank, UBA...). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompteBancaire {
    private UUID identifiant;
    /** Nom/libellé du compte (ex: "Compte courant SGCI"). */
    private String nom;
    private String numeroCompte;
    /** Institution bancaire (ex: "SGCI", "Ecobank"). */
    private String banque;
    private String devise;
    private BigDecimal soldeInitial;
    /** ACTIF ou CLOTURE. */
    private String statut;
    private UUID identifiantFiliale;
    private String nomFiliale;
    /** Solde calculé = soldeInitial + mouvements encaissés. */
    private BigDecimal soldeActuel;
}
