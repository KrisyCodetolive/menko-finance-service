package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record RapprochementBancaireRequest(
        @NotBlank(message = "La période est obligatoire (ex: Mai 2026)")
        String periode,

        @NotNull(message = "Le solde du relevé est obligatoire")
        BigDecimal soldeReleve,

        @NotNull
        LocalDate dateImport,

        @NotNull(message = "Le compte bancaire est obligatoire")
        UUID identifiantCompteBancaire
) {}
