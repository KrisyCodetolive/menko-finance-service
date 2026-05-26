package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.CompteBancaire;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteBancairePort {
    List<CompteBancaire> findByFilialeId(UUID filialeId);
    Optional<CompteBancaire> findById(UUID id);
    CompteBancaire save(CompteBancaire compte);
    /** Somme des chèques encaissés - chèques émis encaissés pour un compte. */
    BigDecimal calculerSoldeActuel(UUID compteBancaireId);
}
