package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.ChequePort;
import com.menko.comptabilite.domain.model.Cheque;
import com.menko.comptabilite.infrastructure.persistence.entity.ChequeJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.CompteBancaireJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.EcritureComptableJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.ChequeJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.CompteBancaireJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.EcritureComptableJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class ChequeAdapter implements ChequePort {

    private final ChequeJpaRepository repository;
    private final CompteBancaireJpaRepository compteBancaireRepository;
    private final EcritureComptableJpaRepository ecritureRepository;

    @Override
    public Page<Cheque> findByCompteBancaireId(UUID compteBancaireId, Pageable pageable) {
        return repository.findByCompteBancaireIdentifiant(compteBancaireId, pageable).map(this::toDomain);
    }

    @Override
    public Page<Cheque> findByCompteBancaireIdAndStatut(UUID compteBancaireId, String statut, Pageable pageable) {
        return repository.findByCompteBancaireIdentifiantAndStatut(compteBancaireId, statut, pageable).map(this::toDomain);
    }

    @Override
    public List<Cheque> findChequesNonEncaissesBefore(UUID filialeId, LocalDate limite) {
        return repository.findChequesNonEncaissesBefore(filialeId, limite).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<Cheque> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public Cheque save(Cheque c) {
        ChequeJpa jpa = c.getIdentifiant() != null
                ? repository.findById(c.getIdentifiant()).orElse(new ChequeJpa())
                : new ChequeJpa();

        jpa.setType(c.getType());
        jpa.setNumeroCheque(c.getNumeroCheque());
        jpa.setTiersNom(c.getTiersNom());
        jpa.setMontant(c.getMontant());
        jpa.setMotif(c.getMotif());
        jpa.setDateCheque(c.getDateCheque());
        jpa.setDateEncaissement(c.getDateEncaissement());
        jpa.setStatut(c.getStatut() != null ? c.getStatut() : "En cours");

        CompteBancaireJpa compte = compteBancaireRepository.findById(c.getIdentifiantCompteBancaire())
                .orElseThrow(() -> new IllegalArgumentException("Compte bancaire introuvable"));
        jpa.setCompteBancaire(compte);

        if (c.getIdentifiantEcriture() != null) {
            EcritureComptableJpa ecriture = ecritureRepository.findById(c.getIdentifiantEcriture()).orElse(null);
            jpa.setEcriture(ecriture);
        }

        return toDomain(repository.save(jpa));
    }

    private Cheque toDomain(ChequeJpa jpa) {
        return Cheque.builder()
                .identifiant(jpa.getIdentifiant())
                .type(jpa.getType())
                .numeroCheque(jpa.getNumeroCheque())
                .tiersNom(jpa.getTiersNom())
                .montant(jpa.getMontant())
                .motif(jpa.getMotif())
                .dateCheque(jpa.getDateCheque())
                .dateEncaissement(jpa.getDateEncaissement())
                .statut(jpa.getStatut())
                .identifiantCompteBancaire(jpa.getCompteBancaire().getIdentifiant())
                .nomBanque(jpa.getCompteBancaire().getNomBanque())
                .identifiantEcriture(jpa.getEcriture() != null ? jpa.getEcriture().getIdentifiant() : null)
                .build();
    }
}
