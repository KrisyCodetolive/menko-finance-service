package com.menko.comptabilite.application.dto.response;

import java.math.BigDecimal;
import java.time.LocalDate;

/** Ligne du grand livre pour un compte sur une période. */
public record GrandLivreLigneResponse(
        LocalDate dateEcriture,
        String numeroPiece,
        String libelle,
        String codeJournal,
        BigDecimal debit,
        BigDecimal credit,
        BigDecimal soldeProgressif
) {}
