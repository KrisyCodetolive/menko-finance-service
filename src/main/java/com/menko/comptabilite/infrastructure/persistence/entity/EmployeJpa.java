package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "employe", schema = "menko_finance")
@Getter
@Setter
public class EmployeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false)
    private String poste;

    @Column(name = "salaire_base", nullable = false, precision = 15, scale = 2)
    private BigDecimal salaireBase;

    @Column(name = "date_embauche", nullable = false)
    private LocalDate dateEmbauche;

    @Column(nullable = false)
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale", nullable = false)
    private FilialeJpa filiale;
}
