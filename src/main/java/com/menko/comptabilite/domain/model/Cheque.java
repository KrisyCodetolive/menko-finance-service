package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Chèque émis vers un fournisseur ou reçu d'un client. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Cheque {
    private UUID identifiant;
    /** "EMIS" ou "RECU" */
    private String type;
    private String numeroCheque;
    private String tiersNom;
    private BigDecimal montant;
    private String motif;
    private LocalDate dateCheque;
    private LocalDate dateEncaissement;
    /** "En cours" | "Remis en banque" | "Encaissé" | "Rejeté" */
    private String statut;
    private UUID identifiantCompteBancaire;
    private String nomBanque;
    private UUID identifiantEcriture;
}
