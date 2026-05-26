package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.CompteComptablePort;
import com.menko.comptabilite.domain.model.CompteComptable;
import com.menko.comptabilite.infrastructure.persistence.entity.CompteComptableJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteComptableJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.FilialeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompteComptableAdapter implements CompteComptablePort {

    private final CompteComptableJpaRepository repository;
    private final FilialeJpaRepository filialeRepository;

    @Override
    public Page<CompteComptable> findByFilialeId(UUID filialeId, Pageable pageable) {
        return repository.findByFilialeIdentifiant(filialeId, pageable).map(CompteComptableAdapter::toDomain);
    }

    @Override
    public List<CompteComptable> findByFilialeIdAndClasse(UUID filialeId, Integer classe) {
        return repository.findByFilialeIdentifiantAndClasse(filialeId, classe)
                .stream().map(CompteComptableAdapter::toDomain).toList();
    }

    @Override
    public Optional<CompteComptable> findById(UUID id) {
        return repository.findById(id).map(CompteComptableAdapter::toDomain);
    }

    @Override
    public Optional<CompteComptable> findByNumeroAndFilialeId(String numero, UUID filialeId) {
        return repository.findByNumeroAndFilialeIdentifiant(numero, filialeId)
                .map(CompteComptableAdapter::toDomain);
    }

    @Override
    public Optional<String> findMaxNumeroByClasseAndFilialeId(Integer classe, UUID filialeId) {
        return repository.findMaxNumeroByClasseAndFilialeId(classe, filialeId);
    }

    @Override
    public CompteComptable save(CompteComptable c) {
        CompteComptableJpa jpa = c.getIdentifiant() != null
                ? repository.findById(c.getIdentifiant()).orElse(new CompteComptableJpa())
                : new CompteComptableJpa();

        jpa.setNumero(c.getNumero());
        jpa.setLibelle(c.getLibelle());
        jpa.setClasse(c.getClasse());
        jpa.setType(c.getType());
        jpa.setActif(c.getActif());

        FilialeJpa filiale = filialeRepository.findById(c.getIdentifiantFiliale())
                .orElseThrow(() -> new IllegalArgumentException("Filiale introuvable"));
        jpa.setFiliale(filiale);

        return toDomain(repository.save(jpa));
    }

    static CompteComptable toDomain(CompteComptableJpa jpa) {
        return CompteComptable.builder()
                .identifiant(jpa.getIdentifiant())
                .numero(jpa.getNumero())
                .libelle(jpa.getLibelle())
                .classe(jpa.getClasse())
                .type(jpa.getType())
                .actif(jpa.getActif())
                .identifiantFiliale(jpa.getFiliale().getIdentifiant())
                .nomFiliale(jpa.getFiliale().getNom())
                .build();
    }
}
