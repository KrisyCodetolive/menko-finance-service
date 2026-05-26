package com.menko.comptabilite.infrastructure.persistence.adapter;

import com.menko.comptabilite.application.port.out.EcritureComptablePort;
import com.menko.comptabilite.domain.model.EcritureComptable;
import com.menko.comptabilite.domain.model.LigneEcriture;
import com.menko.comptabilite.infrastructure.persistence.entity.*;
import com.menko.comptabilite.infrastructure.persistence.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class EcritureComptableAdapter implements EcritureComptablePort {

    private final EcritureComptableJpaRepository ecritureRepo;
    private final JournalComptableJpaRepository journalRepo;
    private final FilialeJpaRepository filialeRepo;
    private final UtilisateurJpaRepository utilisateurRepo;
    private final TiersJpaRepository tiersRepo;
    private final CompteComptableJpaRepository compteRepo;

    @Override
    public Page<EcritureComptable> findByFilialeId(UUID filialeId, LocalDate debut, LocalDate fin, Pageable pageable) {
        return ecritureRepo.findByFilialeAndPeriode(filialeId, debut, fin, pageable)
                .map(this::toDomain);
    }

    @Override
    public Optional<EcritureComptable> findById(UUID id) {
        return ecritureRepo.findById(id).map(this::toDomain);
    }

    @Override
    public Optional<String> findMaxNumeroPiece(String prefixe, int annee, UUID filialeId) {
        return ecritureRepo.findMaxNumeroPiece(prefixe, annee, filialeId);
    }

    @Override
    @Transactional
    public EcritureComptable save(EcritureComptable e) {
        EcritureComptableJpa jpa = e.getIdentifiant() != null
                ? ecritureRepo.findById(e.getIdentifiant()).orElse(new EcritureComptableJpa())
                : new EcritureComptableJpa();

        jpa.setNumeroPiece(e.getNumeroPiece());
        jpa.setDateEcriture(e.getDateEcriture());
        jpa.setLibelle(e.getLibelle());

        jpa.setJournal(journalRepo.findById(e.getIdentifiantJournal())
                .orElseThrow(() -> new IllegalArgumentException("Journal introuvable")));
        jpa.setFiliale(filialeRepo.findById(e.getIdentifiantFiliale())
                .orElseThrow(() -> new IllegalArgumentException("Filiale introuvable")));
        jpa.setUtilisateur(utilisateurRepo.findById(e.getIdentifiantUtilisateur())
                .orElseThrow(() -> new IllegalArgumentException("Utilisateur introuvable")));

        if (e.getIdentifiantTiers() != null) {
            jpa.setTiers(tiersRepo.findById(e.getIdentifiantTiers()).orElse(null));
        }

        jpa.setStatut(e.getStatut() != null ? e.getStatut() : "BROUILLON");

        jpa.getLignes().clear();
        if (e.getLignes() != null) {
            for (LigneEcriture l : e.getLignes()) {
                LigneEcritureJpa ligne = new LigneEcritureJpa();
                ligne.setEcriture(jpa);
                ligne.setLibelle(l.getLibelle() != null ? l.getLibelle() : jpa.getLibelle());
                ligne.setCompte(compteRepo.findById(l.getIdentifiantCompte())
                        .orElseThrow(() -> new IllegalArgumentException("Compte introuvable : " + l.getIdentifiantCompte())));
                ligne.setDebit(l.getDebit());
                ligne.setCredit(l.getCredit());
                jpa.getLignes().add(ligne);
            }
        }

        return toDomain(ecritureRepo.save(jpa));
    }

    @Override
    public void deleteById(UUID id) {
        ecritureRepo.deleteById(id);
    }

    private EcritureComptable toDomain(EcritureComptableJpa jpa) {
        List<LigneEcriture> lignes = jpa.getLignes().stream()
                .map(l -> LigneEcriture.builder()
                        .identifiant(l.getIdentifiant())
                        .identifiantEcriture(jpa.getIdentifiant())
                        .identifiantCompte(l.getCompte().getIdentifiant())
                        .numeroCompte(l.getCompte().getNumero())
                        .libelleCompte(l.getCompte().getLibelle())
                        .libelle(l.getLibelle())
                        .debit(l.getDebit())
                        .credit(l.getCredit())
                        .build())
                .toList();

        return EcritureComptable.builder()
                .identifiant(jpa.getIdentifiant())
                .numeroPiece(jpa.getNumeroPiece())
                .dateEcriture(jpa.getDateEcriture())
                .libelle(jpa.getLibelle())
                .statut(jpa.getStatut())
                .identifiantJournal(jpa.getJournal().getIdentifiant())
                .codeJournal(jpa.getJournal().getCode())
                .nomJournal(jpa.getJournal().getNom())
                .identifiantFiliale(jpa.getFiliale().getIdentifiant())
                .nomFiliale(jpa.getFiliale().getNom())
                .identifiantUtilisateur(jpa.getUtilisateur().getIdentifiant())
                .nomUtilisateur(jpa.getUtilisateur().getNom() + " " + jpa.getUtilisateur().getPrenom())
                .identifiantTiers(jpa.getTiers() != null ? jpa.getTiers().getIdentifiant() : null)
                .nomTiers(jpa.getTiers() != null ? jpa.getTiers().getNom() : null)
                .dateCreation(jpa.getDateCreation())
                .lignes(lignes)
                .build();
    }
}
