package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
    name = "compte_comptable",
    schema = "menko_finance",
    uniqueConstraints = @UniqueConstraint(columnNames = {"numero", "identifiant_filiale"})
)
@Getter
@Setter
public class CompteComptableJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String numero;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false)
    private Integer classe;

    @Column(nullable = false)
    private String type;

    @Column(nullable = false)
    private Boolean actif = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale", nullable = false)
    private FilialeJpa filiale;
}
