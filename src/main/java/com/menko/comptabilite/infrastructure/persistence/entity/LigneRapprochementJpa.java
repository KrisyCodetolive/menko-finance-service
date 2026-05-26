package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "ligne_rapprochement", schema = "menko_finance")
@Getter
@Setter
public class LigneRapprochementJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(name = "date_releve", nullable = false)
    private LocalDate dateReleve;

    @Column(name = "libelle_releve", nullable = false)
    private String libelleReleve;

    @Column(name = "montant_releve", nullable = false, precision = 15, scale = 2)
    private BigDecimal montantReleve;

    @Column(nullable = false)
    private String statut = "En attente";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_rapprochement", nullable = false)
    private RapprochementBancaireJpa rapprochement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_ecriture")
    private EcritureComptableJpa ecriture;
}
