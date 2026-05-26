package com.menko.comptabilite.application.port.out;

import com.menko.comptabilite.domain.model.Cheque;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChequePort {
    Page<Cheque> findByCompteBancaireId(UUID compteBancaireId, Pageable pageable);
    Page<Cheque> findByCompteBancaireIdAndStatut(UUID compteBancaireId, String statut, Pageable pageable);
    List<Cheque> findChequesNonEncaissesBefore(UUID filialeId, LocalDate limite);
    Optional<Cheque> findById(UUID id);
    Cheque save(Cheque cheque);
}
