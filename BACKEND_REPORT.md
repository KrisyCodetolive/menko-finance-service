# Menko Finance Service — Backend Report

## Contexte du projet

**Menko Finance Service** est l'API REST backend du système de gestion comptable et de trésorerie de **Menko Holding** et de ses filiales (Menko Agro, Menko SARL, Menko Digitech, Menko Building).

Le projet a été récupéré dans un état initial limité (Spring Boot setup basique + migrations SQL), puis élevé à un backend professionnel, testé et documenté sur 8 étapes méthodiques.

---

## État initial vs état livré

| Dimension | État initial | État livré |
|---|---|---|
| Architecture | Flat (aucune séparation) | Hexagonale propre (Domain / Application / Infrastructure / Presentation) |
| Contrôleurs REST | 0 | 14 controllers, 55+ endpoints |
| Logique métier | Absente | 14 Use Cases complets |
| Tests | 0 | 65 tests (unitaires + intégration) |
| Base de données | Script SQL incomplet | 16 tables SYSCOHADA + migrations Supabase |
| Sécurité | Absente | Spring Security 6 + JWT stateless |
| Documentation API | Absente | Swagger UI complet avec @Tag et @Operation |
| CI / Intégration | Absente | Testcontainers PostgreSQL (SpringBootTest) |
| Docker | Absent | Prêt (config externalisée via env vars) |

---

## Stack technique

- **Runtime** : Java 17, Spring Boot 3.5.0
- **Persistance** : Spring Data JPA, Hibernate 6.6, PostgreSQL 16 (Supabase)
- **Sécurité** : Spring Security 6, JWT (jjwt 0.12.6), stateless
- **API** : Spring MVC REST, springdoc-openapi 2.6.0 (Swagger UI)
- **Tests** : JUnit 5, Mockito, Spring Boot Test, Testcontainers 1.21.0
- **Build** : Maven 3, Spring Boot Maven Plugin

---

## Architecture hexagonale

```
com.menko.comptabilite
├── domain/
│   ├── model/          ← 16 modèles métier purs (aucune dépendance Spring/JPA)
│   └── exception/      ← RegleMetierException, EntiteIntrouvableException
├── application/
│   ├── port/in/        ← 14 interfaces Use Case (ports entrants)
│   ├── port/out/       ← 15 interfaces Repository (ports sortants)
│   ├── usecase/        ← 14 implémentations Use Case
│   └── dto/            ← Request/Response DTOs, ApiResponse, PageResponse
├── infrastructure/
│   ├── persistence/
│   │   ├── entity/     ← 16 entités JPA
│   │   ├── repository/ ← 15 Spring Data JPA repositories
│   │   └── adapter/    ← 16 adapters (implémentent les ports sortants)
│   ├── security/       ← JwtFilter, JwtUtil, SecurityConfig, UserDetailsServiceImpl
│   └── config/         ← OpenApiConfig, JpaAuditConfig
└── presentation/
    ├── controller/     ← 14 REST controllers
    └── advice/         ← GlobalExceptionHandler
```

---

## Fonctionnalités implémentées

### Workflow comptable SYSCOHADA (ÉTAPE 1 & 2)
- Saisie des écritures en **statut BROUILLON**
- Transitions de statut : `BROUILLON → VALIDEE → CLOTUREE`
- Guards métier : modification/suppression interdites hors BROUILLON
- Vérification équilibre débit/crédit à la saisie
- Éditions automatiques : **balance générale**, **grand livre** (solde progressif), **bilan**, **compte de résultat**

### Bulletins de paie (ÉTAPE 3)
- Création avec génération automatique de l'écriture comptable de paie (journal SAL)
- Consultation par employé et par filiale/période

### Gestion des chèques (ÉTAPE 4)
- Émission avec génération d'écriture comptable
- Transitions de statut gardées : `EMIS → ENCAISSE / REJETE / ANNULE`
- Alertes chèques non encaissés depuis N jours

### Rapprochement bancaire (ÉTAPE 5)
- Sessions de rapprochement par compte bancaire
- Validation ligne par ligne
- **Clôture automatique** quand toutes les lignes sont réconciliées

### Dashboard financier (ÉTAPE 3)
- Indicateurs par filiale : trésorerie (classe 5), débit total, crédit total, chèques en attente
- Dashboard consolidé groupe (SUPER_ADMIN, DG)

### Mouvements inter-filiales
- Enregistrement avec génération de deux écritures comptables miroir

---

## Endpoints REST (55+)

| Tag Swagger | Préfixe | Méthodes |
|---|---|---|
| Authentification | `/api/v1/auth` | POST /login, POST/PUT/DELETE/GET /users |
| Filiales | `/api/v1/filiales` | GET, GET/{id}, POST, PUT/{id} |
| Plan comptable | `/api/v1/comptes-comptables` | GET, GET/classe/{n}, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Journaux comptables | `/api/v1/journaux` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Tiers | `/api/v1/tiers` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Employés | `/api/v1/employes` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id} |
| Écritures comptables | `/api/v1/ecritures` | GET, GET/{id}, POST, PUT/{id}, DELETE/{id}, POST/{id}/valider, POST/{id}/cloturer |
| Bulletins de paie | `/api/v1/bulletins-paie` | GET/employe/{id}, GET, GET/{id}, POST |
| Comptes bancaires | `/api/v1/comptes-bancaires` | GET, GET/{id}, POST, PUT/{id}, POST/{id}/cloturer |
| Chèques | `/api/v1/cheques` | GET, GET/alertes, GET/{id}, POST, PATCH/{id}/statut |
| Rapprochement bancaire | `/api/v1/rapprochements` | GET, GET/{id}, POST, POST/{id}/terminer, GET/{id}/lignes, POST/{id}/lignes, PATCH/lignes/{id}/valider |
| Mouvements inter-filiales | `/api/v1/mouvements-inter-filiales` | GET, GET/{id}, POST |
| Éditions comptables | `/api/v1/editions` | GET/balance, GET/grand-livre, GET/bilan, GET/compte-de-resultat |
| Dashboard financier | `/api/v1/dashboard` | GET/filiale/{id}, GET/groupe |

---

## Sécurité

- **JWT stateless** : token Bearer dans Authorization header
- **Secret externalisé** : `${JWT_SECRET:valeur-dev}` — à surcharger via env var en production
- **Rôles** : `SUPER_ADMIN`, `DG`, `COMPTABLE`, `TRESORIER`
- **`@PreAuthorize`** sur les endpoints sensibles
- **Null safety** : `@NonNull` Spring sur `JwtFilter.doFilterInternal`

---

## Tests

| Suite de tests | Nb tests | Type |
|---|---|---|
| `AuthUseCaseImplTest` | 7 | Unitaire (Mockito) |
| `BalanceUseCaseImplTest` | 6 | Unitaire |
| `BulletinPaieUseCaseImplTest` | 6 | Unitaire |
| `ChequeUseCaseImplTest` | 10 | Unitaire |
| `CompteComptableUseCaseImplTest` | 6 | Unitaire |
| `DashboardUseCaseImplTest` | 3 | Unitaire |
| `EcritureComptableUseCaseImplTest` | 12 | Unitaire |
| `RapprochementBancaireUseCaseImplTest` | 11 | Unitaire |
| `LigneEcritureAdapterTest` | 3 | Unitaire |
| `ComptabiliteCaisseApplicationTests` | 1 | Intégration (Testcontainers) |
| **Total** | **65** | **BUILD SUCCESS** |

---

## Base de données

- **16 tables** SYSCOHADA en schéma `menko_finance`
- Migration SQL dans `supabase/migrations/`
- `ddl-auto=validate` en production, `create-drop` en test
- Schéma créé automatiquement via HikariCP `connection-init-sql`

---

## Améliorations CI/CD

- **Testcontainers** PostgreSQL 16-alpine pour le `SpringBootTest`
- **Maven Surefire** configuré avec `DOCKER_HOST=tcp://localhost:2375` et `api.version=1.44` (compatibilité Docker Desktop 29.4.x sur Windows)
- Build reproductible hors dépendance à une base externe

---

## Historique des commits

| Commit | Description |
|---|---|
| `10a855d` | Create brouillons (état initial) |
| `99fe574` | refactor(build): upgrade to v1.0.0-dev — JWT, SpringDoc, AOP |
| `38001b7` | feat(domain): modèles métier purs SYSCOHADA |
| `ef85d0b` | feat(application): DTOs, ports, Use Cases |
| `37ddb22` | feat(infrastructure): entités JPA, repositories, adapters, JWT security |
| `24c0f55` | feat(presentation): 14 REST controllers, GlobalExceptionHandler |
| `a68e335` | feat(db): migration SQL Supabase — 16 tables SYSCOHADA |
| `694076d` | test(usecase): tests unitaires CompteComptable et EcritureComptable |
| `b428033` | fix(audit): bugs critiques JPA, logique métier, tests |
| `1d4022c` | ÉTAPE 1→6 : workflow écriture, tests métier, rapprochement, Testcontainers |

---

## Statistiques du projet

| Métrique | Valeur |
|---|---|
| Fichiers Java source | 153 |
| Fichiers Java test | 10 |
| Controllers REST | 14 |
| Use Cases | 14 |
| Entités JPA | 16 |
| Tables SQL | 16 |
| Tests | 65 |
| Endpoints REST | 55+ |
| Couverture Swagger | 100% des endpoints documentés |

---

## Prérequis techniques

- Java 17+
- Maven 3.9+
- PostgreSQL 16 (Supabase local ou cloud)
- Docker Desktop (pour les tests d'intégration Testcontainers)
- Variable d'environnement `JWT_SECRET` en production

## Lancement local

```bash
# Démarrer Supabase local
supabase start

# Lancer l'application
mvn spring-boot:run

# Swagger UI
open http://localhost:8080/swagger-ui.html

# Tests
mvn clean verify
```
