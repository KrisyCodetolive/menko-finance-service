package com.menko.comptabilite.application.port.in;

import com.menko.comptabilite.application.dto.request.CompteComptableRequest;
import com.menko.comptabilite.domain.model.CompteComptable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/** Plan comptable SYSCOHADA par filiale. */
public interface CompteComptableUseCase {
    Page<CompteComptable> listerParFiliale(UUID filialeId, Pageable pageable);
    List<CompteComptable> listerParFilialeEtClasse(UUID filialeId, Integer classe);
    CompteComptable trouverParId(UUID id);
    CompteComptable creer(CompteComptableRequest request);
    CompteComptable modifier(UUID id, CompteComptableRequest request);
    void archiver(UUID id);
}
