package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "tiers", schema = "menko_finance")
@Getter
@Setter
public class TiersJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String type;

    private String telephone;
    private String email;
    private String adresse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale", nullable = false)
    private FilialeJpa filiale;
}
