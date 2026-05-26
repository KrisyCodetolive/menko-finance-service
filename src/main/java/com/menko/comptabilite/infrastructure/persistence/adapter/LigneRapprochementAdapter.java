package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.LigneRapprochementPort;
import com.menko.comptabilite.domain.model.LigneRapprochement;
import com.menko.comptabilite.infrastructure.persistence.entity.EcritureComptableJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.LigneRapprochementJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.RapprochementBancaireJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.EcritureComptableJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.LigneRapprochementJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.RapprochementBancaireJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class LigneRapprochementAdapter implements LigneRapprochementPort {

    private final LigneRapprochementJpaRepository repository;
    private final RapprochementBancaireJpaRepository rapprochementRepository;
    private final EcritureComptableJpaRepository ecritureRepository;

    @Override
    public List<LigneRapprochement> findByRapprochementId(UUID rapprochementId) {
        return repository.findByRapprochementIdentifiant(rapprochementId).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<LigneRapprochement> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public LigneRapprochement save(LigneRapprochement l) {
        LigneRapprochementJpa jpa = l.getIdentifiant() != null
                ? repository.findById(l.getIdentifiant()).orElse(new LigneRapprochementJpa())
                : new LigneRapprochementJpa();

        jpa.setDateReleve(l.getDateReleve());
        jpa.setLibelleReleve(l.getLibelleReleve());
        jpa.setMontantReleve(l.getMontantReleve());
        jpa.setStatut(l.getStatut() != null ? l.getStatut() : "En attente");

        RapprochementBancaireJpa rapprochement = rapprochementRepository
                .findById(l.getIdentifiantRapprochement())
                .orElseThrow(() -> new IllegalArgumentException("Rapprochement introuvable"));
        jpa.setRapprochement(rapprochement);

        if (l.getIdentifiantEcriture() != null) {
            EcritureComptableJpa ecriture = ecritureRepository.findById(l.getIdentifiantEcriture()).orElse(null);
            jpa.setEcriture(ecriture);
        } else {
            jpa.setEcriture(null);
        }

        return toDomain(repository.save(jpa));
    }

    private LigneRapprochement toDomain(LigneRapprochementJpa jpa) {
        return LigneRapprochement.builder()
                .identifiant(jpa.getIdentifiant())
                .dateReleve(jpa.getDateReleve())
                .libelleReleve(jpa.getLibelleReleve())
                .montantReleve(jpa.getMontantReleve())
                .statut(jpa.getStatut())
                .identifiantRapprochement(jpa.getRapprochement().getIdentifiant())
                .identifiantEcriture(jpa.getEcriture() != null ? jpa.getEcriture().getIdentifiant() : null)
                .build();
    }
}
