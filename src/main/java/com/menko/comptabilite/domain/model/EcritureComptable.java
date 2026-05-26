package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/** En-tête d'une opération comptable avec ses lignes débit/crédit. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EcritureComptable {
    private UUID identifiant;
    private String numeroPiece;
    private LocalDate dateEcriture;
    private String libelle;
    /** Workflow: BROUILLON → VALIDEE → CLOTUREE */
    private String statut;
    private UUID identifiantJournal;
    private String codeJournal;
    private String nomJournal;
    private UUID identifiantFiliale;
    private String nomFiliale;
    private UUID identifiantUtilisateur;
    private String nomUtilisateur;
    private UUID identifiantTiers;
    private String nomTiers;
    private LocalDateTime dateCreation;
    private List<LigneEcriture> lignes;
}
