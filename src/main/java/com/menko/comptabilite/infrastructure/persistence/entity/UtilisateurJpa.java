package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "utilisateur", schema = "menko_finance")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class UtilisateurJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "mot_de_passe", nullable = false)
    private String motDePasse;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_role", nullable = false)
    private RoleJpa role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_filiale")
    private FilialeJpa filiale;

    @Column(nullable = false)
    private boolean actif = true;

    @CreatedDate
    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;
}
