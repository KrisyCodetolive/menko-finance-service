package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record MouvementInterFilialeRequest(
        @NotBlank(message = "Le libellé est obligatoire")
        String libelle,

        @NotNull @DecimalMin(value = "0.01", message = "Le montant doit être positif")
        BigDecimal montant,

        @NotNull(message = "La date du mouvement est obligatoire")
        LocalDate dateMouvement,

        @NotNull(message = "La filiale source est obligatoire")
        UUID identifiantFilialeSource,

        @NotNull(message = "La filiale destination est obligatoire")
        UUID identifiantFilialeDestination,

        /** Compte débité côté source (ex: compte banque 5xxx). */
        @NotNull UUID identifiantCompteDebitSource,

        /** Compte crédité côté source (ex: compte interco 181xxx). */
        @NotNull UUID identifiantCompteCreditSource,

        /** Compte débité côté destination (ex: compte interco 181xxx). */
        @NotNull UUID identifiantCompteDebitDestination,

        /** Compte crédité côté destination (ex: compte banque 5xxx). */
        @NotNull UUID identifiantCompteCreditDestination,

        UUID identifiantJournalSource,
        UUID identifiantJournalDestination
) {}
