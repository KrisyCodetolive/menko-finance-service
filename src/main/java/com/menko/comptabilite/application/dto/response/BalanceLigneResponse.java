package com.menko.comptabilite.application.dto.response;

import java.math.BigDecimal;

/** Ligne de la balance générale SYSCOHADA. */
public record BalanceLigneResponse(
        String numero,
        String libelle,
        Integer classe,
        BigDecimal totalDebit,
        BigDecimal totalCredit,
        BigDecimal soldeDebiteur,
        BigDecimal soldeCrediteur
) {}
