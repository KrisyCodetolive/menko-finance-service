package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "ligne_ecriture", schema = "menko_finance")
@Getter
@Setter
public class LigneEcritureJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_ecriture", nullable = false)
    private EcritureComptableJpa ecriture;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_compte", nullable = false)
    private CompteComptableJpa compte;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal debit = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal credit = BigDecimal.ZERO;
}
