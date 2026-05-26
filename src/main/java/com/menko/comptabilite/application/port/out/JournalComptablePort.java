package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.JournalComptable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JournalComptablePort {
    List<JournalComptable> findByFilialeId(UUID filialeId);
    Optional<JournalComptable> findById(UUID id);
    Optional<JournalComptable> findByCodeAndFilialeId(String code, UUID filialeId);
    JournalComptable save(JournalComptable journal);
    void deleteById(UUID id);
}
