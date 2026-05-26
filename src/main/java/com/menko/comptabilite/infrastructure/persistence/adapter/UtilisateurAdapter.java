package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.UtilisateurPort;
import com.menko.comptabilite.domain.model.Utilisateur;
import com.menko.comptabilite.infrastructure.persistence.entity.FilialeJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.RoleJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.UtilisateurJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.FilialeJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.RoleJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.UtilisateurJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UtilisateurAdapter implements UtilisateurPort {

    private final UtilisateurJpaRepository repository;
    private final RoleJpaRepository roleRepository;
    private final FilialeJpaRepository filialeRepository;

    @Override
    public List<Utilisateur> findAll() {
        return repository.findAll().stream().map(UtilisateurAdapter::toDomain).toList();
    }

    @Override
    public Optional<Utilisateur> findById(UUID id) {
        return repository.findById(id).map(UtilisateurAdapter::toDomain);
    }

    @Override
    public Optional<Utilisateur> findByEmail(String email) {
        return repository.findByEmail(email).map(UtilisateurAdapter::toDomain);
    }

    @Override
    public Utilisateur save(Utilisateur u) {
        UtilisateurJpa jpa = u.getIdentifiant() != null
                ? repository.findById(u.getIdentifiant()).orElse(new UtilisateurJpa())
                : new UtilisateurJpa();

        jpa.setNom(u.getNom());
        jpa.setPrenom(u.getPrenom());
        jpa.setEmail(u.getEmail());
        jpa.setMotDePasse(u.getMotDePasse());
        jpa.setActif(u.isActif());

        RoleJpa role = roleRepository.findById(u.getIdentifiantRole())
                .orElseThrow(() -> new IllegalArgumentException("Rôle introuvable"));
        jpa.setRole(role);

        if (u.getIdentifiantFiliale() != null) {
            FilialeJpa filiale = filialeRepository.findById(u.getIdentifiantFiliale())
                    .orElseThrow(() -> new IllegalArgumentException("Filiale introuvable"));
            jpa.setFiliale(filiale);
        } else {
            jpa.setFiliale(null);
        }

        return toDomain(repository.save(jpa));
    }

    @Override
    public boolean existsByEmail(String email) {
        return repository.existsByEmail(email);
    }

    static Utilisateur toDomain(UtilisateurJpa jpa) {
        return Utilisateur.builder()
                .identifiant(jpa.getIdentifiant())
                .nom(jpa.getNom())
                .prenom(jpa.getPrenom())
                .email(jpa.getEmail())
                .motDePasse(jpa.getMotDePasse())
                .identifiantRole(jpa.getRole().getIdentifiant())
                .nomRole(jpa.getRole().getNom())
                .identifiantFiliale(jpa.getFiliale() != null ? jpa.getFiliale().getIdentifiant() : null)
                .nomFiliale(jpa.getFiliale() != null ? jpa.getFiliale().getNom() : null)
                .actif(jpa.isActif())
                .dateCreation(jpa.getDateCreation())
                .build();
    }
}
