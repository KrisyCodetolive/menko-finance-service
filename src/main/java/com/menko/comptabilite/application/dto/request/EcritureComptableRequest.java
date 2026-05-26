package com.menko.comptabilite.application.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public record EcritureComptableRequest(
        @NotNull(message = "La date est obligatoire")
        LocalDate dateEcriture,

        @NotBlank(message = "Le libellé est obligatoire")
        String libelle,

        @NotNull(message = "Le journal est obligatoire")
        UUID identifiantJournal,

        @NotNull(message = "La filiale est obligatoire")
        UUID identifiantFiliale,

        UUID identifiantTiers,

        @NotEmpty(message = "Au moins une ligne débit/crédit est requise")
        @Valid
        List<LigneEcritureRequest> lignes
) {}
