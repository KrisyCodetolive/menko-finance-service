package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.MouvementInterFilialePort;
import com.menko.comptabilite.domain.model.MouvementInterFiliale;
import com.menko.comptabilite.infrastructure.persistence.entity.MouvementInterFilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class MouvementInterFilialeAdapter implements MouvementInterFilialePort {

    private final MouvementInterFilialeJpaRepository repository;
    private final FilialeJpaRepository filialeRepo;
    private final EcritureComptableJpaRepository ecritureRepo;

    @Override
    public Page<MouvementInterFiliale> findByFilialeId(UUID filialeId, Pageable pageable) {
        return repository.findByFilialeId(filialeId, pageable).map(this::toDomain);
    }

    @Override
    public Optional<MouvementInterFiliale> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public MouvementInterFiliale save(MouvementInterFiliale m) {
        MouvementInterFilialeJpa jpa = new MouvementInterFilialeJpa();
        jpa.setLibelle(m.getLibelle());
        jpa.setMontant(m.getMontant());
        jpa.setDateMouvement(m.getDateMouvement());
        jpa.setFilialeSource(filialeRepo.findById(m.getIdentifiantFilialeSource())
                .orElseThrow(() -> new IllegalArgumentException("Filiale source introuvable")));
        jpa.setFilialeDestination(filialeRepo.findById(m.getIdentifiantFilialeDestination())
                .orElseThrow(() -> new IllegalArgumentException("Filiale destination introuvable")));
        jpa.setEcritureSource(ecritureRepo.findById(m.getIdentifiantEcritureSource())
                .orElseThrow(() -> new IllegalArgumentException("Écriture source introuvable")));
        jpa.setEcritureDestination(ecritureRepo.findById(m.getIdentifiantEcritureDestination())
                .orElseThrow(() -> new IllegalArgumentException("Écriture destination introuvable")));
        return toDomain(repository.save(jpa));
    }

    private MouvementInterFiliale toDomain(MouvementInterFilialeJpa jpa) {
        return MouvementInterFiliale.builder()
                .identifiant(jpa.getIdentifiant())
                .libelle(jpa.getLibelle())
                .montant(jpa.getMontant())
                .dateMouvement(jpa.getDateMouvement())
                .identifiantFilialeSource(jpa.getFilialeSource().getIdentifiant())
                .nomFilialeSource(jpa.getFilialeSource().getNom())
                .identifiantFilialeDestination(jpa.getFilialeDestination().getIdentifiant())
                .nomFilialeDestination(jpa.getFilialeDestination().getNom())
                .identifiantEcritureSource(jpa.getEcritureSource().getIdentifiant())
                .identifiantEcritureDestination(jpa.getEcritureDestination().getIdentifiant())
                .dateCreation(jpa.getDateCreation())
                .build();
    }
}
