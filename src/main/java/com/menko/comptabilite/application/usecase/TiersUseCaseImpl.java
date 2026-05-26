package com.menko.comptabilite.application.usecase;

import com.menko.comptabilite.application.dto.request.TiersRequest;
import com.menko.comptabilite.application.port.in.TiersUseCase;
import com.menko.comptabilite.application.port.out.TiersPort;
import com.menko.comptabilite.domain.exception.EntiteIntrouvableException;
import com.menko.comptabilite.domain.model.Tiers;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TiersUseCaseImpl implements TiersUseCase {

    private final TiersPort tiersPort;

    @Override
    public Page<Tiers> listerParFiliale(UUID filialeId, String type, Pageable pageable) {
        if (type != null && !type.isBlank()) {
            return tiersPort.findByFilialeIdAndType(filialeId, type, pageable);
        }
        return tiersPort.findByFilialeId(filialeId, pageable);
    }

    @Override
    public Tiers trouverParId(UUID id) {
        return tiersPort.findById(id)
                .orElseThrow(() -> new EntiteIntrouvableException("Tiers", id));
    }

    @Override
    public Tiers creer(TiersRequest request) {
        return tiersPort.save(Tiers.builder()
                .nom(request.nom())
                .type(request.type())
                .telephone(request.telephone())
                .email(request.email())
                .adresse(request.adresse())
                .identifiantFiliale(request.identifiantFiliale())
                .build());
    }

    @Override
    public Tiers modifier(UUID id, TiersRequest request) {
        Tiers tiers = trouverParId(id);
        tiers.setNom(request.nom());
        tiers.setType(request.type());
        tiers.setTelephone(request.telephone());
        tiers.setEmail(request.email());
        tiers.setAdresse(request.adresse());
        return tiersPort.save(tiers);
    }

    @Override
    public void supprimer(UUID id) {
        trouverParId(id);
        tiersPort.deleteById(id);
    }
}
