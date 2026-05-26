package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.RapprochementBancairePort;
import com.menko.comptabilite.domain.model.RapprochementBancaire;
import com.menko.comptabilite.infrastructure.persistence.entity.CompteBancaireJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.RapprochementBancaireJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.UtilisateurJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteBancaireJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.RapprochementBancaireJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.UtilisateurJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class RapprochementBancaireAdapter implements RapprochementBancairePort {

    private final RapprochementBancaireJpaRepository repository;
    private final CompteBancaireJpaRepository compteRepository;
    private final UtilisateurJpaRepository utilisateurRepository;

    @Override
    public List<RapprochementBancaire> findByCompteBancaireId(UUID compteBancaireId) {
        return repository.findByCompteBancaireIdentifiant(compteBancaireId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<RapprochementBancaire> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByCompteBancaireIdAndPeriode(UUID compteBancaireId, String periode) {
        return repository.existsByCompteBancaireIdentifiantAndPeriode(compteBancaireId, periode);
    }

    @Override
    public RapprochementBancaire save(RapprochementBancaire r) {
        RapprochementBancaireJpa jpa = new RapprochementBancaireJpa();
        jpa.setPeriode(r.getPeriode());
        jpa.setStatut(r.getStatut() != null ? r.getStatut() : "En cours");
        jpa.setSoldeReleve(r.getSoldeReleve());
        jpa.setDateImport(r.getDateImport());

        CompteBancaireJpa compte = compteRepository.findById(r.getIdentifiantCompteBancaire())
                .orElseThrow(() -> new IllegalArgumentException("Compte bancaire introuvable"));
        jpa.setCompteBancaire(compte);

        UtilisateurJpa utilisateur = utilisateurRepository.findById(r.getIdentifiantUtilisateur())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable"));
        jpa.setUtilisateur(utilisateur);

        return toDomain(repository.save(jpa));
    }

    private RapprochementBancaire toDomain(RapprochementBancaireJpa jpa) {
        return RapprochementBancaire.builder()
                .identifiant(jpa.getIdentifiant())
                .periode(jpa.getPeriode())
                .statut(jpa.getStatut())
                .soldeReleve(jpa.getSoldeReleve())
                .dateImport(jpa.getDateImport())
                .identifiantCompteBancaire(jpa.getCompteBancaire().getIdentifiant())
                .nomBanque(jpa.getCompteBancaire().getNom())
                .numeroCompte(jpa.getCompteBancaire().getNumeroCompte())
                .identifiantUtilisateur(jpa.getUtilisateur().getIdentifiant())
                .nomUtilisateur(jpa.getUtilisateur().getNom() + " " + jpa.getUtilisateur().getPrenom())
                .build();
    }
}
