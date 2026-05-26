package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.BulletinPaiePort;
import com.menko.comptabilite.domain.model.BulletinPaie;
import com.menko.comptabilite.infrastructure.persistence.entity.BulletinPaieJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.EcritureComptableJpa;
import com.menko.comptabilite.infrastructure.persistence.entity.EmployeJpa;
import com.menko.comptabilite.infrastructure.persistence.repository.BulletinPaieJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.EcritureComptableJpaRepository;
import com.menko.comptabilite.infrastructure.persistence.repository.EmployeJpaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class BulletinPaieAdapter implements BulletinPaiePort {

    private final BulletinPaieJpaRepository repository;
    private final EmployeJpaRepository employeRepository;
    private final EcritureComptableJpaRepository ecritureRepository;

    @Override
    public List<BulletinPaie> findByEmployeId(UUID employeId) {
        return repository.findByEmployeIdentifiant(employeId).stream().map(this::toDomain).toList();
    }

    @Override
    public List<BulletinPaie> findByFilialeIdAndPeriode(UUID filialeId, int mois, int annee) {
        return repository.findByFilialeAndPeriode(filialeId, mois, annee).stream().map(this::toDomain).toList();
    }

    @Override
    public Optional<BulletinPaie> findById(UUID id) {
        return repository.findById(id).map(this::toDomain);
    }

    @Override
    public boolean existsByEmployeIdAndMoisAndAnnee(UUID employeId, int mois, int annee) {
        return repository.existsByEmployeIdentifiantAndMoisAndAnnee(employeId, mois, annee);
    }

    @Override
    public BulletinPaie save(BulletinPaie b) {
        BulletinPaieJpa jpa = new BulletinPaieJpa();
        jpa.setMois(b.getMois());
        jpa.setAnnee(b.getAnnee());
        jpa.setSalaireBrut(b.getSalaireBrut());
        jpa.setChargesSalariales(b.getChargesSalariales());
        jpa.setImpot(b.getImpot());
        jpa.setSalaireNet(b.getSalaireNet());
        jpa.setChargesPatronales(b.getChargesPatronales());

        EmployeJpa employe = employeRepository.findById(b.getIdentifiantEmploye())
                .orElseThrow(() -> new IllegalArgumentException("Employé introuvable"));
        jpa.setEmploye(employe);

        EcritureComptableJpa ecriture = ecritureRepository.findById(b.getIdentifiantEcriture())
                .orElseThrow(() -> new IllegalArgumentException("Écriture introuvable"));
        jpa.setEcriture(ecriture);

        return toDomain(repository.save(jpa));
    }

    private BulletinPaie toDomain(BulletinPaieJpa jpa) {
        return BulletinPaie.builder()
                .identifiant(jpa.getIdentifiant())
                .mois(jpa.getMois())
                .annee(jpa.getAnnee())
                .salaireBrut(jpa.getSalaireBrut())
                .chargesSalariales(jpa.getChargesSalariales())
                .impot(jpa.getImpot())
                .salaireNet(jpa.getSalaireNet())
                .chargesPatronales(jpa.getChargesPatronales())
                .identifiantEmploye(jpa.getEmploye().getIdentifiant())
                .nomEmploye(jpa.getEmploye().getNom())
                .prenomEmploye(jpa.getEmploye().getPrenom())
                .identifiantEcriture(jpa.getEcriture().getIdentifiant())
                .dateCreation(jpa.getDateCreation())
                .build();
    }
}
