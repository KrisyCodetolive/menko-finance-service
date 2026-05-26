package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record TiersRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le type est obligatoire")
        @Pattern(regexp = "Client|Fournisseur|Les deux",
                 message = "Le type doit être 'Client', 'Fournisseur' ou 'Les deux'")
        String type,

        String telephone,
        String email,
        String adresse,

        @NotNull(message = "La filiale est obligatoire")
        UUID identifiantFiliale
) {}
