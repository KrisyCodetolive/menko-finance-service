package com.menko.comptabilite.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/** Journal comptable : VTE, ACH, SAL, OI. */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JournalComptable {
    private UUID identifiant;
    private String code;
    private String nom;
    private UUID identifiantFiliale;
    private String nomFiliale;
}
