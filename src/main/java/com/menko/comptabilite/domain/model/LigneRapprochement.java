package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

/** Ligne du relevé bancaire comparée aux écritures du système. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LigneRapprochement {
    private UUID identifiant;
    private LocalDate dateReleve;
    private String libelleReleve;
    private BigDecimal montantReleve;
    /** "Réconciliée" | "Écart" | "En attente" */
    private String statut;
    private UUID identifiantRapprochement;
    private UUID identifiantEcriture;
}
