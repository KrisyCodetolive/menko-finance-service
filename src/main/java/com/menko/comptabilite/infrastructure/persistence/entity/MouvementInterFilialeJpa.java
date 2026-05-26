package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "mouvement_inter_filiale", schema = "menko_finance")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class MouvementInterFilialeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String libelle;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal montant;

    @Column(name = "date_mouvement", nullable = false)
    private LocalDate dateMouvement;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale_source", nullable = false)
    private FilialeJpa filialeSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale_destination", nullable = false)
    private FilialeJpa filialeDestination;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_ecriture_source", nullable = false)
    private EcritureComptableJpa ecritureSource;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_ecriture_destination", nullable = false)
    private EcritureComptableJpa ecritureDestination;

    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
}
