package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.JournalComptablePort;
import com.menko.comptabilite.domain.model.JournalComptable;
import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.JournalComptableJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.FilialeJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.JournalComptableJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class JournalComptableAdapter implements JournalComptablePort {

    private final JournalComptableJpaRepository repository;
    private final FilialeJpaRepository filialeRepository;

    @Override
    public List<JournalComptable> findByFilialeId(UUID filialeId) {
        return repository.findByFilialeIdentifiant(filialeId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<JournalComptable> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<JournalComptable> findByCodeAndFilialeId(String code, UUID filialeId) {
        return repository.findByCodeAndFilialeIdentifiant(code, filialeId).map(this::toDomain);
    }

    @Override
    public JournalComptable save(JournalComptable j) {
        JournalComptableJpa jpa = j.getIdentifiant() != null
                ? repository.findById(j.getIdentifiant()).orElse(new JournalComptableJpa())
                : new JournalComptableJpa();
        jpa.setCode(j.getCode());
        jpa.setNom(j.getNom());
        FilialeJpa filiale = filialeRepository.findById(j.getIdentifiantFiliale())
                .orElseThrow(() -> new IllegalArgumentException("Filiale introuvable"));
        jpa.setFiliale(filiale);
        return toDomain(repository.save(jpa));
    }

    @Override
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }

    private JournalComptable toDomain(JournalComptableJpa jpa) {
        return JournalComptable.builder()
                .identifiant(jpa.getIdentifiant())
                .code(jpa.getCode())
                .nom(jpa.getNom())
                .identifiantFiliale(jpa.getFiliale().getIdentifiant())
                .nomFiliale(jpa.getFiliale().getNom())
                .build();
    }
}
