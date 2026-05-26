package com.menko.comptabilite.application.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import java.util.UUID;

public record StatutLigneRapprochementRequest(
        @NotBlank
        @Pattern(regexp = "Réconciliée|Écart|En attente", message = "Statut invalide")
        String statut,

        UUID identifiantEcriture
) {}
