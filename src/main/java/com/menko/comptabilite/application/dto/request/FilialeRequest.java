package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FilialeRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le type est obligatoire")
        @Pattern(regexp = "Holding|Filiale", message = "Le type doit être 'Holding' ou 'Filiale'")
        String type
) {}
