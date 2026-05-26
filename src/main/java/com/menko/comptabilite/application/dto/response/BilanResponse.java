package com.menko.comptabilite.application.dto.response;

import java.math.BigDecimal;
import java.util.List;

/** Bilan actif/passif SYSCOHADA. */
public record BilanResponse(
        String filiale,
        String periode,
        List<BalanceLigneResponse> actif,
        List<BalanceLigneResponse> passif,
        BigDecimal totalActif,
        BigDecimal totalPassif
) {}
