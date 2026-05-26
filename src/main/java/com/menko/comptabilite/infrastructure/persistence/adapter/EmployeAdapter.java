package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.EmployePort;
import com.menko.comptabilite.domain.model.Employe;
import com.menko.comptabilite.infrastructure.persistence.entity.EmployeJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.EmployeJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.FilialeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EmployeAdapter implements EmployePort {

    private final EmployeJpaRepository repository;
    private final FilialeJpaRepository filialeRepository;

    @Override
    public Page<Employe> findByFilialeId(UUID filialeId, Pageable pageable) {
        return repository.findByFilialeIdentifiant(filialeId, pageable).map(this::toDomain);
    }

    @Override
    public Optional<Employe> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Employe save(Employe e) {
        EmployeJpa jpa = e.getIdentifiant() != null
                ? repository.findById(e.getIdentifiant()).orElse(new EmployeJpa())
                : new EmployeJpa();
        jpa.setNom(e.getNom());
        jpa.setPrenom(e.getPrenom());
        jpa.setPoste(e.getPoste());
        jpa.setSalaireBase(e.getSalaireBase());
        jpa.setDateEmbauche(e.getDateEmbauche());
        jpa.setActif(e.getActif());
        FilialeJpa filiale = filialeRepository.findById(e.getIdentifiantFiliale())
                .orElseThrow(() -> new IllegalArgumentException("Filiale introuvable"));
        jpa.setFiliale(filiale);
        return toDomain(repository.save(jpa));
    }

    private Employe toDomain(EmployeJpa jpa) {
        return Employe.builder()
                .identifiant(jpa.getIdentifiant())
                .nom(jpa.getNom())
                .prenom(jpa.getPrenom())
                .poste(jpa.getPoste())
                .salaireBase(jpa.getSalaireBase())
                .dateEmbauche(jpa.getDateEmbauche())
                .actif(jpa.getActif())
                .identifiantFiliale(jpa.getFiliale().getIdentifiant())
                .nomFiliale(jpa.getFiliale().getNom())
                .build();
    }
}
