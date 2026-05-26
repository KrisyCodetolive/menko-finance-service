package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record LigneRapprochementRequest(
        @NotNull LocalDate dateReleve,
        @NotBlank String libelleReleve,
        @NotNull BigDecimal montantReleve,
        UUID identifiantEcriture
) {}
