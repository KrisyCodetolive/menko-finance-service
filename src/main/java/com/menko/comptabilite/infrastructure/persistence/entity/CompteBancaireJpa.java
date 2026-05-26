package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "compte_bancaire", schema = "menko_finance")
@Getter
@Setter
public class CompteBancaireJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(name = "nom_banque", nullable = false)
    private String nomBanque;

    @Column(name = "numero_compte", nullable = false)
    private String numeroCompte;

    @Column(name = "solde_initial", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    @Column(nullable = false)
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale", nullable = false)
    private FilialeJpa filiale;
}
