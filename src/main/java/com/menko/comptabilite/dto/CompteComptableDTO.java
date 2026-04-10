package com.menko.comptabilite.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class CompteComptableDTO {

    private UUID identifiant;

    // Généré automatiquement par le système — retourné en réponse, pas envoyé en création
    private String numero;

    @NotBlank(message = "Le libellé est obligatoire")
    private String libelle;

    @NotNull(message = "La classe est obligatoire")
    @Min(value = 1, message = "La classe doit être entre 1 et 7")
    @Max(value = 7, message = "La classe doit être entre 1 et 7")
    private Integer classe;

    @NotBlank(message = "Le type est obligatoire")
    @Pattern(regexp = "Debit|Credit", message = "Le type doit être Debit ou Credit")
    private String type;

    private Boolean actif;

    @NotNull(message = "La filiale est obligatoire")
    private UUID identifiantFiliale;

    private String nomFiliale;
}
