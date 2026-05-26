package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.CompteBancairePort;
import com.menko.comptabilite.domain.model.CompteBancaire;
import com.menko.comptabilite.infrastructure.persistence.entity.CompteBancaireJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteBancaireJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.FilialeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CompteBancaireAdapter implements CompteBancairePort {

    private final CompteBancaireJpaRepository repository;
    private final FilialeJpaRepository filialeRepository;

    @Override
    public List<CompteBancaire> findByFilialeId(UUID filialeId) {
        return repository.findByFilialeIdentifiant(filialeId).stream()
                .map(jpa -> toDomain(jpa, repository.calculerSoldeActuel(jpa.getIdentifiant())))
                .toList();
    }

    @Override
    public Optional<CompteBancaire> findById(UUID id) {
        return repository.findById(id)
                .map(jpa -> toDomain(jpa, repository.calculerSoldeActuel(jpa.getIdentifiant())));
    }

    @Override
    public CompteBancaire save(CompteBancaire c) {
        CompteBancaireJpa jpa = c.getIdentifiant() != null
                ? repository.findById(c.getIdentifiant()).orElse(new CompteBancaireJpa())
                : new CompteBancaireJpa();
        jpa.setNomBanque(c.getNomBanque());
        jpa.setNumeroCompte(c.getNumeroCompte());
        jpa.setSoldeInitial(c.getSoldeInitial());
        jpa.setActif(c.getActif());
        FilialeJpa filiale = filialeRepository.findById(c.getIdentifiantFiliale())
                .orElseThrow(() -> new IllegalArgumentException("Filiale introuvable"));
        jpa.setFiliale(filiale);
        CompteBancaireJpa saved = repository.save(jpa);
        return toDomain(saved, repository.calculerSoldeActuel(saved.getIdentifiant()));
    }

    @Override
    public BigDecimal calculerSoldeActuel(UUID compteBancaireId) {
        return repository.calculerSoldeActuel(compteBancaireId);
    }

    private CompteBancaire toDomain(CompteBancaireJpa jpa, BigDecimal soldeActuel) {
        return CompteBancaire.builder()
                .identifiant(jpa.getIdentifiant())
                .nomBanque(jpa.getNomBanque())
                .numeroCompte(jpa.getNumeroCompte())
                .soldeInitial(jpa.getSoldeInitial())
                .actif(jpa.getActif())
                .identifiantFiliale(jpa.getFiliale().getIdentifiant())
                .nomFiliale(jpa.getFiliale().getNom())
                .soldeActuel(soldeActuel != null ? soldeActuel : jpa.getSoldeInitial())
                .build();
    }
}
