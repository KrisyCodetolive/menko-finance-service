package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.LigneRapprochement;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LigneRapprochementPort {
    List<LigneRapprochement> findByRapprochementId(UUID rapprochementId);
    Optional<LigneRapprochement> findById(UUID id);
    LigneRapprochement save(LigneRapprochement ligne);
}
