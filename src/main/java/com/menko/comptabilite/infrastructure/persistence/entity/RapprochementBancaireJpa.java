package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(
    name = "rapprochement_bancaire",
    schema = "menko_finance",
    uniqueConstraints = @UniqueConstraint(columnNames = {"identifiant_compte_bancaire", "periode"})
)
@Getter
@Setter
public class RapprochementBancaireJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String periode;

    @Column(nullable = false)
    private String statut = "En cours";

    @Column(name = "solde_releve", nullable = false, precision = 15, scale = 2)
    private BigDecimal soldeReleve;

    @Column(name = "date_import", nullable = false)
    private LocalDate dateImport;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_compte_bancaire", nullable = false)
    private CompteBancaireJpa compteBancaire;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_utilisateur", nullable = false)
    private UtilisateurJpa utilisateur;
}
