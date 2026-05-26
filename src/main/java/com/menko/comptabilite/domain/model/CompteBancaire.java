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
    private String nomBanque;
    private String numeroCompte;
    private BigDecimal soldeInitial;
    private Boolean actif;
    private UUID identifiantFiliale;
    private String nomFiliale;
    /** Solde calculé = soldeInitial + mouvements. */
    private BigDecimal soldeActuel;
}
