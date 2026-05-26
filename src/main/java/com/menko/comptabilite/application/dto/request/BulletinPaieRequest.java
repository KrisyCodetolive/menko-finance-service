package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.util.UUID;

public record BulletinPaieRequest(
        @NotNull @Min(1) @Max(12)
        Integer mois,

        @NotNull @Min(2000)
        Integer annee,

        @NotNull @DecimalMin("0.00")
        BigDecimal salaireBrut,

        @NotNull @DecimalMin("0.00")
        BigDecimal chargesSalariales,

        @NotNull @DecimalMin("0.00")
        BigDecimal impot,

        @NotNull @DecimalMin("0.00")
        BigDecimal salaireNet,

        @NotNull @DecimalMin("0.00")
        BigDecimal chargesPatronales,

        @NotNull(message = "L'employé est obligatoire")
        UUID identifiantEmploye,

        @NotNull(message = "L'écriture comptable est obligatoire")
        UUID identifiantEcriture
) {}
