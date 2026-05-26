package com.menko.comptabilite.application.dto.response;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/** Tableau de bord consolidé par filiale. */
public record DashboardFilialeResponse(
        UUID identifiantFiliale,
        String nomFiliale,
        long nombreEcritures,
        long nombreComptes,
        BigDecimal totalDebit,
        BigDecimal totalCredit,
        BigDecimal soldeTresorerie,
        long chequesEnCours,
        long rapprochementsEnCours,
        List<EcritureRecenteResponse> dernieresEcritures
) {
    public record EcritureRecenteResponse(
            String numeroPiece,
            String libelle,
            String codeJournal,
            String dateEcriture,
            BigDecimal totalDebit,
            BigDecimal totalCredit
    ) {}
}
