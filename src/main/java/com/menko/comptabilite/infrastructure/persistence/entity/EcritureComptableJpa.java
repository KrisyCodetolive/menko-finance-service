package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "ecriture_comptable", schema = "menko_finance")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class EcritureComptableJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(name = "numero_piece", nullable = false, unique = true)
    private String numeroPiece;

    @Column(nullable = false)
    private String statut = "BROUILLON";

    @Column(name = "date_ecriture", nullable = false)
    private LocalDate dateEcriture;

    @Column(nullable = false)
    private String libelle;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_journal", nullable = false)
    private JournalComptableJpa journal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale", nullable = false)
    private FilialeJpa filiale;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_utilisateur", nullable = false)
    private UtilisateurJpa utilisateur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_tiers")
    private TiersJpa tiers;

    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    @OneToMany(mappedBy = "ecriture", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LigneEcritureJpa> lignes = new ArrayList<>();
}
