-- ============================================================
-- Menko Finance Service — Schéma initial complet
-- SYSCOHADA Comptabilité + Caisse & Trésorerie
-- ============================================================

CREATE SCHEMA IF NOT EXISTS menko_finance;

-- ============================================================
-- 1. RÔLES
-- ============================================================
CREATE TABLE menko_finance.role (
    identifiant UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nom         VARCHAR(50) NOT NULL UNIQUE
);

INSERT INTO menko_finance.role (nom) VALUES
    ('SUPER_ADMIN'),
    ('DG'),
    ('DAF'),
    ('COMPTABLE'),
    ('CAISSIER'),
    ('RH'),
    ('LECTURE');

-- ============================================================
-- 2. FILIALES
-- ============================================================
CREATE TABLE menko_finance.filiale (
    identifiant  UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nom          VARCHAR(150) NOT NULL,
    code         VARCHAR(20)  NOT NULL UNIQUE,
    pays         VARCHAR(100),
    devise       VARCHAR(10)  NOT NULL DEFAULT 'XOF',
    actif        BOOLEAN      NOT NULL DEFAULT TRUE,
    date_creation TIMESTAMP   NOT NULL DEFAULT now()
);

-- ============================================================
-- 3. UTILISATEURS
-- ============================================================
CREATE TABLE menko_finance.utilisateur (
    identifiant         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    email               VARCHAR(255) NOT NULL UNIQUE,
    mot_de_passe        VARCHAR(255) NOT NULL,
    identifiant_role    UUID         NOT NULL REFERENCES menko_finance.role(identifiant),
    identifiant_filiale UUID         REFERENCES menko_finance.filiale(identifiant),
    actif               BOOLEAN      NOT NULL DEFAULT TRUE,
    date_creation       TIMESTAMP    NOT NULL DEFAULT now()
);

CREATE INDEX idx_utilisateur_email ON menko_finance.utilisateur(email);

-- ============================================================
-- 4. JOURNAUX COMPTABLES
-- ============================================================
CREATE TABLE menko_finance.journal_comptable (
    identifiant         UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(10) NOT NULL,
    libelle             VARCHAR(150) NOT NULL,
    identifiant_filiale UUID        NOT NULL REFERENCES menko_finance.filiale(identifiant),
    UNIQUE (code, identifiant_filiale)
);

CREATE INDEX idx_journal_filiale ON menko_finance.journal_comptable(identifiant_filiale);

-- ============================================================
-- 5. PLAN COMPTABLE SYSCOHADA
-- ============================================================
CREATE TABLE menko_finance.compte_comptable (
    identifiant         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    numero              VARCHAR(20)  NOT NULL,
    libelle             VARCHAR(200) NOT NULL,
    classe              INTEGER      NOT NULL CHECK (classe BETWEEN 1 AND 9),
    type_compte         VARCHAR(50),
    actif               BOOLEAN      NOT NULL DEFAULT TRUE,
    identifiant_filiale UUID         NOT NULL REFERENCES menko_finance.filiale(identifiant),
    UNIQUE (numero, identifiant_filiale)
);

CREATE INDEX idx_compte_filiale       ON menko_finance.compte_comptable(identifiant_filiale);
CREATE INDEX idx_compte_classe        ON menko_finance.compte_comptable(classe);

-- ============================================================
-- 6. TIERS (clients & fournisseurs)
-- ============================================================
CREATE TABLE menko_finance.tiers (
    identifiant         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nom                 VARCHAR(200) NOT NULL,
    type                VARCHAR(20)  NOT NULL CHECK (type IN ('CLIENT','FOURNISSEUR','LES_DEUX')),
    email               VARCHAR(255),
    telephone           VARCHAR(30),
    adresse             TEXT,
    identifiant_filiale UUID         NOT NULL REFERENCES menko_finance.filiale(identifiant)
);

CREATE INDEX idx_tiers_filiale ON menko_finance.tiers(identifiant_filiale);
CREATE INDEX idx_tiers_type    ON menko_finance.tiers(type);

-- ============================================================
-- 7. ÉCRITURES COMPTABLES
-- ============================================================
CREATE TABLE menko_finance.ecriture_comptable (
    identifiant         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_piece        VARCHAR(50)  NOT NULL,
    libelle             VARCHAR(255) NOT NULL,
    date_ecriture       DATE         NOT NULL,
    statut              VARCHAR(20)  NOT NULL DEFAULT 'BROUILLON'
                                    CHECK (statut IN ('BROUILLON','VALIDEE','CLOTUREE')),
    identifiant_journal UUID         NOT NULL REFERENCES menko_finance.journal_comptable(identifiant),
    identifiant_filiale UUID         NOT NULL REFERENCES menko_finance.filiale(identifiant),
    identifiant_utilisateur UUID     NOT NULL REFERENCES menko_finance.utilisateur(identifiant),
    date_creation       TIMESTAMP    NOT NULL DEFAULT now(),
    UNIQUE (numero_piece, identifiant_filiale)
);

CREATE INDEX idx_ecriture_filiale ON menko_finance.ecriture_comptable(identifiant_filiale);
CREATE INDEX idx_ecriture_date    ON menko_finance.ecriture_comptable(date_ecriture);
CREATE INDEX idx_ecriture_journal ON menko_finance.ecriture_comptable(identifiant_journal);

-- ============================================================
-- 8. LIGNES D'ÉCRITURE (partie double)
-- ============================================================
CREATE TABLE menko_finance.ligne_ecriture (
    identifiant          UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    libelle              VARCHAR(255)   NOT NULL,
    debit                NUMERIC(15,2)  NOT NULL DEFAULT 0 CHECK (debit >= 0),
    credit               NUMERIC(15,2)  NOT NULL DEFAULT 0 CHECK (credit >= 0),
    identifiant_ecriture UUID           NOT NULL REFERENCES menko_finance.ecriture_comptable(identifiant) ON DELETE CASCADE,
    identifiant_compte   UUID           NOT NULL REFERENCES menko_finance.compte_comptable(identifiant),
    identifiant_tiers    UUID           REFERENCES menko_finance.tiers(identifiant)
);

CREATE INDEX idx_ligne_ecriture   ON menko_finance.ligne_ecriture(identifiant_ecriture);
CREATE INDEX idx_ligne_compte     ON menko_finance.ligne_ecriture(identifiant_compte);

-- ============================================================
-- 9. PIÈCES JUSTIFICATIVES
-- ============================================================
CREATE TABLE menko_finance.piece_justificative (
    identifiant          UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    nom_fichier          VARCHAR(255) NOT NULL,
    url_fichier          TEXT         NOT NULL,
    type_mime            VARCHAR(100),
    date_upload          TIMESTAMP    NOT NULL DEFAULT now(),
    identifiant_ecriture UUID         NOT NULL REFERENCES menko_finance.ecriture_comptable(identifiant) ON DELETE CASCADE
);

-- ============================================================
-- 10. EMPLOYÉS
-- ============================================================
CREATE TABLE menko_finance.employe (
    identifiant         UUID         PRIMARY KEY DEFAULT gen_random_uuid(),
    matricule           VARCHAR(30)  NOT NULL,
    nom                 VARCHAR(100) NOT NULL,
    prenom              VARCHAR(100) NOT NULL,
    poste               VARCHAR(150),
    salaire_base        NUMERIC(15,2) NOT NULL CHECK (salaire_base >= 0),
    date_embauche       DATE         NOT NULL,
    actif               BOOLEAN      NOT NULL DEFAULT TRUE,
    identifiant_filiale UUID         NOT NULL REFERENCES menko_finance.filiale(identifiant),
    UNIQUE (matricule, identifiant_filiale)
);

CREATE INDEX idx_employe_filiale ON menko_finance.employe(identifiant_filiale);

-- ============================================================
-- 11. BULLETINS DE PAIE
-- ============================================================
CREATE TABLE menko_finance.bulletin_paie (
    identifiant           UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    mois                  INTEGER       NOT NULL CHECK (mois BETWEEN 1 AND 12),
    annee                 INTEGER       NOT NULL,
    salaire_brut          NUMERIC(15,2) NOT NULL CHECK (salaire_brut >= 0),
    cotisations_sociales  NUMERIC(15,2) NOT NULL DEFAULT 0,
    impots                NUMERIC(15,2) NOT NULL DEFAULT 0,
    salaire_net           NUMERIC(15,2) NOT NULL CHECK (salaire_net >= 0),
    date_paiement         DATE,
    statut                VARCHAR(20)   NOT NULL DEFAULT 'EN_ATTENTE'
                                        CHECK (statut IN ('EN_ATTENTE','VALIDE','PAYE')),
    identifiant_employe   UUID          NOT NULL REFERENCES menko_finance.employe(identifiant),
    UNIQUE (identifiant_employe, mois, annee)
);

CREATE INDEX idx_bulletin_employe ON menko_finance.bulletin_paie(identifiant_employe);

-- ============================================================
-- 12. COMPTES BANCAIRES
-- ============================================================
CREATE TABLE menko_finance.compte_bancaire (
    identifiant         UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    nom                 VARCHAR(200)  NOT NULL,
    numero_compte       VARCHAR(50)   NOT NULL UNIQUE,
    banque              VARCHAR(150)  NOT NULL,
    devise              VARCHAR(10)   NOT NULL DEFAULT 'XOF',
    solde_initial       NUMERIC(15,2) NOT NULL DEFAULT 0,
    statut              VARCHAR(20)   NOT NULL DEFAULT 'ACTIF'
                                      CHECK (statut IN ('ACTIF','CLOTURE')),
    identifiant_filiale UUID          NOT NULL REFERENCES menko_finance.filiale(identifiant)
);

CREATE INDEX idx_compte_bancaire_filiale ON menko_finance.compte_bancaire(identifiant_filiale);

-- ============================================================
-- 13. CHÈQUES
-- ============================================================
CREATE TABLE menko_finance.cheque (
    identifiant                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    type                         VARCHAR(10)   NOT NULL CHECK (type IN ('EMIS','RECU')),
    numero_cheque                VARCHAR(50)   NOT NULL,
    tiers_nom                    VARCHAR(200)  NOT NULL,
    montant                      NUMERIC(15,2) NOT NULL CHECK (montant > 0),
    motif                        TEXT,
    date_cheque                  DATE          NOT NULL,
    date_encaissement            DATE,
    statut                       VARCHAR(20)   NOT NULL DEFAULT 'En cours'
                                               CHECK (statut IN ('En cours','Encaissé','Rejeté','Annulé')),
    identifiant_compte_bancaire  UUID          NOT NULL REFERENCES menko_finance.compte_bancaire(identifiant),
    identifiant_ecriture         UUID          REFERENCES menko_finance.ecriture_comptable(identifiant)
);

CREATE INDEX idx_cheque_compte  ON menko_finance.cheque(identifiant_compte_bancaire);
CREATE INDEX idx_cheque_statut  ON menko_finance.cheque(statut);
CREATE INDEX idx_cheque_date    ON menko_finance.cheque(date_cheque);

-- ============================================================
-- 14. MOUVEMENTS INTER-FILIALES
-- ============================================================
CREATE TABLE menko_finance.mouvement_inter_filiale (
    identifiant                UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    montant                    NUMERIC(15,2) NOT NULL CHECK (montant > 0),
    libelle                    VARCHAR(255)  NOT NULL,
    date_mouvement             DATE          NOT NULL,
    identifiant_filiale_source UUID          NOT NULL REFERENCES menko_finance.filiale(identifiant),
    identifiant_filiale_dest   UUID          NOT NULL REFERENCES menko_finance.filiale(identifiant),
    identifiant_ecriture_src   UUID          REFERENCES menko_finance.ecriture_comptable(identifiant),
    identifiant_ecriture_dest  UUID          REFERENCES menko_finance.ecriture_comptable(identifiant),
    identifiant_utilisateur    UUID          NOT NULL REFERENCES menko_finance.utilisateur(identifiant),
    date_creation              TIMESTAMP     NOT NULL DEFAULT now()
);

CREATE INDEX idx_mouvement_source ON menko_finance.mouvement_inter_filiale(identifiant_filiale_source);
CREATE INDEX idx_mouvement_dest   ON menko_finance.mouvement_inter_filiale(identifiant_filiale_dest);

-- ============================================================
-- 15. RAPPROCHEMENTS BANCAIRES
-- ============================================================
CREATE TABLE menko_finance.rapprochement_bancaire (
    identifiant                 UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    periode                     VARCHAR(7)    NOT NULL,   -- format YYYY-MM
    statut                      VARCHAR(20)   NOT NULL DEFAULT 'En cours'
                                              CHECK (statut IN ('En cours','Terminé')),
    solde_releve                NUMERIC(15,2) NOT NULL,
    date_import                 DATE          NOT NULL,
    identifiant_compte_bancaire UUID          NOT NULL REFERENCES menko_finance.compte_bancaire(identifiant),
    identifiant_utilisateur     UUID          NOT NULL REFERENCES menko_finance.utilisateur(identifiant),
    UNIQUE (identifiant_compte_bancaire, periode)
);

CREATE INDEX idx_rapprochement_compte ON menko_finance.rapprochement_bancaire(identifiant_compte_bancaire);

-- ============================================================
-- 16. LIGNES DE RAPPROCHEMENT
-- ============================================================
CREATE TABLE menko_finance.ligne_rapprochement (
    identifiant                  UUID          PRIMARY KEY DEFAULT gen_random_uuid(),
    date_releve                  DATE          NOT NULL,
    libelle_releve               VARCHAR(255)  NOT NULL,
    montant_releve               NUMERIC(15,2) NOT NULL,
    statut                       VARCHAR(20)   NOT NULL DEFAULT 'En attente'
                                               CHECK (statut IN ('En attente','Rapproché','Écart')),
    identifiant_rapprochement    UUID          NOT NULL REFERENCES menko_finance.rapprochement_bancaire(identifiant) ON DELETE CASCADE,
    identifiant_ecriture         UUID          REFERENCES menko_finance.ecriture_comptable(identifiant)
);

CREATE INDEX idx_ligne_rappr ON menko_finance.ligne_rapprochement(identifiant_rapprochement);
