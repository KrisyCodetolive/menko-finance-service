package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/** Fiche de paie mensuelle d'un employé. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulletinPaie {
    private UUID identifiant;
    private Integer mois;
    private Integer annee;
    private BigDecimal salaireBrut;
    private BigDecimal chargesSalariales;
    private BigDecimal impot;
    private BigDecimal salaireNet;
    private BigDecimal chargesPatronales;
    private UUID identifiantEmploye;
    private String nomEmploye;
    private String prenomEmploye;
    private UUID identifiantEcriture;
    private LocalDateTime dateCreation;
}
