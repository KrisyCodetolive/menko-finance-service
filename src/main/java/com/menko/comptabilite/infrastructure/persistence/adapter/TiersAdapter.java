package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.TiersPort;
import com.menko.comptabilite.domain.model.Tiers;
import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.TiersJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.FilialeJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.TiersJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class TiersAdapter implements TiersPort {

    private final TiersJpaRepository repository;
    private final FilialeJpaRepository filialeRepository;

    @Override
    public Page<Tiers> findByFilialeId(UUID filialeId, Pageable pageable) {
        return repository.findByFilialeIdentifiant(filialeId, pageable).map(this::toDomain);
    }

    @Override
    public Page<Tiers> findByFilialeIdAndType(UUID filialeId, String type, Pageable pageable) {
        return repository.findByFilialeIdentifiantAndType(filialeId, type, pageable).map(this::toDomain);
    }

    @Override
    public Optional<Tiers> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Tiers save(Tiers t) {
        TiersJpa jpa = t.getIdentifiant() != null
                ? repository.findById(t.getIdentifiant()).orElse(new TiersJpa())
                : new TiersJpa();
        jpa.setNom(t.getNom());
        jpa.setType(t.getType());
        jpa.setTelephone(t.getTelephone());
        jpa.setEmail(t.getEmail());
        jpa.setAdresse(t.getAdresse());
        FilialeJpa filiale = filialeRepository.findById(t.getIdentifiantFiliale())
                .orElseThrow(() -> new IllegalArgumentException("Filiale introuvable"));
        jpa.setFiliale(filiale);
        return toDomain(repository.save(jpa));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private Tiers toDomain(TiersJpa jpa) {
        return Tiers.builder()
                .identifiant(jpa.getIdentifiant())
                .nom(jpa.getNom())
                .type(jpa.getType())
                .telephone(jpa.getTelephone())
                .email(jpa.getEmail())
                .adresse(jpa.getAdresse())
                .identifiantFiliale(jpa.getFiliale().getIdentifiant())
                .nomFiliale(jpa.getFiliale().getNom())
                .build();
    }
}
