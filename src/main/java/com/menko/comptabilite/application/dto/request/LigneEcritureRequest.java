package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record LigneEcritureRequest(
        @NotNull(message = "Le compte est obligatoire")
        UUID identifiantCompte,

        @NotNull
        @DecimalMin(value = "0.00", message = "Le débit ne peut pas être négatif")
        BigDecimal debit,

        @NotNull
        @DecimalMin(value = "0.00", message = "Le crédit ne peut pas être négatif")
        BigDecimal credit
) {}
