package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record JournalComptableRequest(
        @NotBlank(message = "Le code est obligatoire")
        String code,

        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotNull(message = "La filiale est obligatoire")
        UUID identifiantFiliale
) {}
