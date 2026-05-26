package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(
    name = "bulletin_paie",
    schema = "menko_finance",
    uniqueConstraints = @UniqueConstraint(columnNames = {"identifiant_employe", "mois", "annee"})
)
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class BulletinPaieJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private Integer mois;

    @Column(nullable = false)
    private Integer annee;

    @Column(name = "salaire_brut", nullable = false, precision = 15, scale = 2)
    private BigDecimal salaireBrut;

    @Column(name = "charges_salariales", nullable = false, precision = 15, scale = 2)
    private BigDecimal chargesSalariales = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal impot = BigDecimal.ZERO;

    @Column(name = "salaire_net", nullable = false, precision = 15, scale = 2)
    private BigDecimal salaireNet;

    @Column(name = "charges_patronales", nullable = false, precision = 15, scale = 2)
    private BigDecimal chargesPatronales = BigDecimal.ZERO;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_employe", nullable = false)
    private EmployeJpa employe;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_ecriture", nullable = false)
    private EcritureComptableJpa ecriture;

    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
}
