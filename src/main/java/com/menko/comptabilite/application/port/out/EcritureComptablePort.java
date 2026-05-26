package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.EcritureComptable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public interface EcritureComptablePort {
    Page<EcritureComptable> findByFilialeId(UUID filialeId, LocalDate debut, LocalDate fin, Pageable pageable);
    Optional<EcritureComptable> findById(UUID id);
    Optional<String> findMaxNumeroPiece(String prefixe, int annee, UUID filialeId);
    EcritureComptable save(EcritureComptable ecriture);
    void deleteById(UUID id);
}
