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

    @Column(name = "nom", nullable = false)
    private String nom;

    @Column(name = "numero_compte", nullable = false, unique = true)
    private String numeroCompte;

    @Column(name = "banque", nullable = false)
    private String banque;

    @Column(name = "devise", nullable = false)
    private String devise = "XOF";

    @Column(name = "solde_initial", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeInitial = BigDecimal.ZERO;

    @Column(name = "statut", nullable = false)
    private String statut = "ACTIF";

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale", nullable = false)
    private FilialeJpa filiale;
}
