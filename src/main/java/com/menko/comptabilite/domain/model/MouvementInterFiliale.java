package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/** Virement financier entre deux filiales du groupe avec double écriture automatique. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MouvementInterFiliale {
    private UUID identifiant;
    private String libelle;
    private BigDecimal montant;
    private LocalDate dateMouvement;
    private UUID identifiantFilialeSource;
    private String nomFilialeSource;
    private UUID identifiantFilialeDestination;
    private String nomFilialeDestination;
    private UUID identifiantEcritureSource;
    private UUID identifiantEcritureDestination;
    private LocalDateTime dateCreation;
}
