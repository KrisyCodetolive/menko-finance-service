package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record ChequeRequest(
        @NotBlank @Pattern(regexp = "EMIS|RECU", message = "Le type doit être 'EMIS' ou 'RECU'")
        String type,

        @NotBlank(message = "Le numéro de chèque est obligatoire")
        String numeroCheque,

        @NotBlank(message = "Le nom du tiers est obligatoire")
        String tiersNom,

        @NotNull @DecimalMin(value = "0.01", message = "Le montant doit être positif")
        BigDecimal montant,

        String motif,

        @NotNull(message = "La date du chèque est obligatoire")
        LocalDate dateCheque,

        @NotNull(message = "Le compte bancaire est obligatoire")
        UUID identifiantCompteBancaire,

        UUID identifiantEcriture
) {}
