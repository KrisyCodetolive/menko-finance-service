package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "filiale", schema = "menko_finance")
@Getter
@Setter
public class FilialeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String type;
}
