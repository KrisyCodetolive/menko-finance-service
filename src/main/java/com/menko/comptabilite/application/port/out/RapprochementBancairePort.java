package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.RapprochementBancaire;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RapprochementBancairePort {
    List<RapprochementBancaire> findByCompteBancaireId(UUID compteBancaireId);
    Optional<RapprochementBancaire> findById(UUID id);
    boolean existsByCompteBancaireIdAndPeriode(UUID compteBancaireId, String periode);
    RapprochementBancaire save(RapprochementBancaire rapprochement);
}
