package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.*;

import java.util.UUID;

public record CompteComptableRequest(
        @NotBlank(message = "Le libellé est obligatoire")
        String libelle,

        @NotNull(message = "La classe est obligatoire")
        @Min(value = 1, message = "La classe doit être entre 1 et 7")
        @Max(value = 7, message = "La classe doit être entre 1 et 7")
        Integer classe,

        @NotBlank(message = "Le type est obligatoire")
        @Pattern(regexp = "Debit|Credit", message = "Le type doit être 'Debit' ou 'Credit'")
        String type,

        @NotNull(message = "La filiale est obligatoire")
        UUID identifiantFiliale
) {}
