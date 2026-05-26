package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.application.dto.response.BalanceLigneResponse;
import com.menko.comptabilite.application.dto.response.GrandLivreLigneResponse;
import com.menko.comptabilite.domain.model.LigneEcriture;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface LigneEcriturePort {
    List<LigneEcriture> findByEcritureId(UUID ecritureId);
    LigneEcriture save(LigneEcriture ligne);
    void deleteByEcritureId(UUID ecritureId);
    List<BalanceLigneResponse> calculerBalance(UUID filialeId, LocalDate debut, LocalDate fin);
    List<GrandLivreLigneResponse> calculerGrandLivre(UUID compteId, LocalDate debut, LocalDate fin);
}
