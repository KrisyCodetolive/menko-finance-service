package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.FilialePort;
import com.menko.comptabilite.domain.model.Filiale;
import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.FilialeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FilialeAdapter implements FilialePort {

    private final FilialeJpaRepository repository;

    @Override
    public List<Filiale> findAll() {
        return repository.findAll().stream().map(FilialeAdapter::toDomain).toList();
    }

    @Override
    public Optional<Filiale> findById(UUID id) {
        return repository.findById(id).map(FilialeAdapter::toDomain);
    }

    @Override
    public Filiale save(Filiale filiale) {
        FilialeJpa jpa = filiale.getIdentifiant() != null
                ? repository.findById(filiale.getIdentifiant()).orElse(new FilialeJpa())
                : new FilialeJpa();
        jpa.setNom(filiale.getNom());
        jpa.setType(filiale.getType());
        return toDomain(repository.save(jpa));
    }

    static Filiale toDomain(FilialeJpa jpa) {
        return Filiale.builder()
                .identifiant(jpa.getIdentifiant())
                .nom(jpa.getNom())
                .type(jpa.getType())
                .build();
    }
}
