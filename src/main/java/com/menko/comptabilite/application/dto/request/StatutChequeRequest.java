package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record StatutChequeRequest(
        @NotBlank
        @Pattern(regexp = "En cours|Remis en banque|Encaissé|Rejeté",
                 message = "Statut invalide")
        String statut,

        LocalDate dateEncaissement
) {}
