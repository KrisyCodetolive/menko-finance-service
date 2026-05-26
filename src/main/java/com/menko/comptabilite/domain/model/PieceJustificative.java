package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

/** Document justificatif attaché à une écriture (facture, reçu...). */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PieceJustificative {
    private UUID identifiant;
    private String nomFichier;
    private String urlFichier;
    private UUID identifiantEcriture;
    private LocalDateTime dateUpload;
}
