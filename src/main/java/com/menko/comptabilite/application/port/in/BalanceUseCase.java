package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.BilanResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/** Éditions comptables : balance générale, grand livre, bilan, compte de résultat. */
public interface BalanceUseCase {
    /** Balance générale : cumuls débit/crédit/soldes par compte sur une période. */
    List<BalanceLigneResponse> balanceGenerale(UUID filialeId, LocalDate debut, LocalDate fin);

    /** Grand livre : détail chronologique des écritures sur un compte. */
    List<GrandLivreLigneResponse> grandLivre(UUID compteId, LocalDate debut, LocalDate fin);

    /** Bilan actif/passif SYSCOHADA. */
    BilanResponse bilan(UUID filialeId, LocalDate fin);

    /** Compte de résultat : produits (cl.7) vs charges (cl.6). */
    List<BalanceLigneResponse> compteDeResultat(UUID filialeId, LocalDate debut, LocalDate fin);
}
