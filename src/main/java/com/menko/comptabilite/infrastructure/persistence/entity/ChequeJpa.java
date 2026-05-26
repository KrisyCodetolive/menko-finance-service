package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "cheque", schema = "menko_finance")
@Getter
@Setter
public class ChequeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String type;

    @Column(name = "numero_cheque", nullable = false)
    private String numeroCheque;

    @Column(name = "tiers_nom", nullable = false)
    private String tiersNom;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    private String motif;

    @Column(name = "date_cheque", nullable = false)
    private LocalDate dateCheque;

    @Column(name = "date_encaissement")
    private LocalDate dateEncaissement;

    @Column(nullable = false)
    private String statut = "En cours";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_compte_bancaire", nullable = false)
    private CompteBancaireJpa compteBancaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_ecriture")
    private EcritureComptableJpa ecriture;
}
