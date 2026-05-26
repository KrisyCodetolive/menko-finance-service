package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.FilialeRequest;
import com.menko.comptabilite.application.port.in.FilialeUseCase;
import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.Filiale;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FilialeUseCaseImpl implements FilialeUseCase {

    private final FilialePort filialePort;

    @Override
    public List<Filiale> listerToutes() {
        return filialePort.findAll();
    }

    @Override
    public Filiale trouverParId(UUID id) {
        return filialePort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Filiale", id));
    }

    @Override
    public Filiale creer(FilialeRequest request) {
        return filialePort.save(Filiale.builder()
                .nom(request.nom())
                .type(request.type())
                .build());
    }

    @Override
    public Filiale modifier(UUID id, FilialeRequest request) {
        Filiale filiale = trouverParId(id);
        filiale.setNom(request.nom());
        filiale.setType(request.type());
        return filialePort.save(filiale);
    }
}
