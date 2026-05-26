package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(
    name = "journal_comptable",
    schema = "menko_finance",
    uniqueConstraints = @UniqueConstraint(columnNames = {"code", "identifiant_filiale"})
)
@Getter
@Setter
public class JournalComptableJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String nom;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale", nullable = false)
    private FilialeJpa filiale;
}
