package com.menko.comptabilite.infrastructure.persistence.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "piece_justificative", schema = "menko_finance")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class PieceJustificativeJpa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID identifiant;

    @Column(name = "nom_fichier", nullable = false)
    private String nomFichier;

    @Column(name = "url_fichier", nullable = false)
    private String urlFichier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "identifiant_ecriture", nullable = false)
    private EcritureComptableJpa ecriture;

    @CreatedDate
    @Column(name = "date_upload", nullable = false, updatable = false)
    private LocalDateTime dateUpload;
}
