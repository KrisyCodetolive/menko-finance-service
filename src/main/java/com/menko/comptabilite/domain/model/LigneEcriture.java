package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

/** Ligne débit/crédit d'une écriture comptable. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneEcriture {
    private UUID identifiant;
    private UUID identifiantEcriture;
    private UUID identifiantCompte;
    private String numeroCompte;
    private String libelleCompte;
    private String libelle;
    private BigDecimal debit;
    private BigDecimal credit;
}
