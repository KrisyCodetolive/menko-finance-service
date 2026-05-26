package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public record EmployeRequest(
        @NotBlank(message = "Le nom est obligatoire")
        String nom,

        @NotBlank(message = "Le prénom est obligatoire")
        String prenom,

        @NotBlank(message = "Le poste est obligatoire")
        String poste,

        @NotNull(message = "Le salaire de base est obligatoire")
        @DecimalMin(value = "0.00", inclusive = false, message = "Le salaire doit être positif")
        BigDecimal salaireBase,

        @NotNull(message = "La date d'embauche est obligatoire")
        LocalDate dateEmbauche,

        @NotNull(message = "La filiale est obligatoire")
        UUID identifiantFiliale
) {}
