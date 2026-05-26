package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record CompteBancaireRequest(
        @NotBlank(message = "Le nom du compte est obligatoire")
        String nom,

        @NotBlank(message = "Le numéro de compte est obligatoire")
        String numeroCompte,

        @NotBlank(message = "Le nom de la banque est obligatoire")
        String banque,

        /** Devise ISO 4217 — XOF par défaut. */
        String devise,

        @NotNull @DecimalMin("0.00")
        BigDecimal soldeInitial,

        @NotNull(message = "La filiale est obligatoire")
        UUID identifiantFiliale
) {}
