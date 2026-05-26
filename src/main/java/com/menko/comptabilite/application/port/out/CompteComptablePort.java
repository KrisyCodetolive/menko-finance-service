package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.CompteComptable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CompteComptablePort {
    Page<CompteComptable> findByFilialeId(UUID filialeId, Pageable pageable);
    List<CompteComptable> findByFilialeIdAndClasse(UUID filialeId, Integer classe);
    Optional<CompteComptable> findById(UUID id);
    Optional<CompteComptable> findByNumeroAndFilialeId(String numero, UUID filialeId);
    Optional<String> findMaxNumeroByClasseAndFilialeId(Integer classe, UUID filialeId);
    CompteComptable save(CompteComptable compte);
}
