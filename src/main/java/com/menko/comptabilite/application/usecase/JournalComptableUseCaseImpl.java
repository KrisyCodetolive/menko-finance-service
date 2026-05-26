package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.JournalComptableRequest;
import com.menko.comptabilite.application.port.in.JournalComptableUseCase;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.application.port.out.JournalComptablePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.exception.RegleMetierException;
import com.menko.comptabilite.domain.model.JournalComptable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JournalComptableUseCaseImpl implements JournalComptableUseCase {

    private final JournalComptablePort journalPort;
    private final FilialePort filialePort;

    @Override
    public List<JournalComptable> listerParFiliale(UUID filialeId) {
        return journalPort.findByFilialeId(filialeId);
    }

    @Override
    public JournalComptable trouverParId(UUID id) {
        return journalPort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Journal comptable", id));
    }

    @Override
    public JournalComptable creer(JournalComptableRequest request) {
        filialePort.findById(request.identifiantFiliale())
                .orElseThrow(() -> new EntiteIntrouvableException("Filiale", request.identifiantFiliale()));

        journalPort.findByCodeAndFilialeId(request.code(), request.identifiantFiliale()).ifPresent(j -> {
            throw new RegleMetierException("Un journal avec le code '" + request.code() + "' existe déjà pour cette filiale");
        });

        return journalPort.save(JournalComptable.builder()
                .code(request.code().toUpperCase())
                .nom(request.nom())
                .identifiantFiliale(request.identifiantFiliale())
                .build());
    }

    @Override
    public JournalComptable modifier(UUID id, JournalComptableRequest request) {
        JournalComptable journal = trouverParId(id);
        journal.setNom(request.nom());
        return journalPort.save(journal);
    }

    @Override
    public void supprimer(UUID id) {
        trouverParId(id);
        journalPort.deleteById(id);
    }
}
