# MENKO Holding — Contexte Projet Complet

## Description métier
Application de gestion comptable et de trésorerie pour Menko Holding
et ses filiales (Menko Agro, Menko SARL, Menko Digitech, Menko Building).

## Stack technique
- Java 17, Spring Boot 3.5.0, Spring Data JPA, Spring Security
- PostgreSQL 16 via Supabase
- Architecture MVC + Clean Architecture (Domain / Application / Infrastructure / Presentation)
- Docker pour la conteneurisation

## Rôles utilisateurs
- Super Administrateur : accès complet toutes filiales
- Directeur Général : lecture seule, tableaux de bord consolidés
- DFC/Comptable : saisie comptable, plan SYSCOHADA, bulletins de paie
- Trésorier : comptes bancaires, chèques, rapprochement bancaire

## Modèle de données (tables Supabase)
### Utilisateurs : filiale, role, utilisateur
### Comptabilité : compte_comptable, journal_comptable (VTE/ACH/SAL/OI),
  tiers, ecriture_comptable, ligne_ecriture, piece_justificative,
  employe, bulletin_paie, mouvement_inter_filiale
### Trésorerie : compte_bancaire, cheque (EMIS/RECU),
  rapprochement_bancaire, ligne_rapprochement

## Tâches du projet (issue tracker)

### EN COURS (Sprint 1 — Comptabilité)
- [x] Saisie comptable
- [x] Éditions automatiques (balance, grand livre)
- [ ] Front-end development (assigné WK, deadline 25/04/26 — EN RETARD)

### À FAIRE (Sprint 2 — Caisse & Trésorerie)
- [ ] Gestion des comptes bancaires par entité
- [ ] Gestion des chèques (émis et reçus)
- [ ] Rapprochement bancaire
- [ ] Mouvements inter-filiales

### Tests & Livraison
- [ ] Tests unitaires et d'intégration
- [ ] Build image Docker finale + push vers registry
- [ ] Documentation API