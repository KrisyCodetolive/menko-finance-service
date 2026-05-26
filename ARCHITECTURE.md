# Menko Finance Service — Architecture technique

## Vue d'ensemble

Le service respecte l'**Architecture Hexagonale** (Ports & Adapters), aussi appelée Clean Architecture. L'invariant fondamental est la **direction des dépendances** : les couches externes dépendent toujours des couches internes, jamais l'inverse.

```
┌─────────────────────────────────────────────────────────────┐
│  PRESENTATION (Controllers, GlobalExceptionHandler)         │
│      ↓ dépend de                                            │
│  APPLICATION (UseCases, Ports In/Out, DTOs)                 │
│      ↓ dépend de                                            │
│  DOMAIN (Models, Exceptions)                                │
│      ↑ ne dépend de rien                                    │
│  INFRASTRUCTURE (JPA, Security, Config)                     │
│      implémente les ports sortants définis dans Application │
└─────────────────────────────────────────────────────────────┘
```

---

## Couche Domain

**Package** : `com.menko.comptabilite.domain`

### Responsabilité
Contient les règles et modèles métier purs. **Aucune dépendance** vers Spring, JPA, ou toute bibliothèque externe.

### Modèles (16)
```
Filiale, Role, Utilisateur
CompteComptable, JournalComptable, Tiers
Employe, BulletinPaie
EcritureComptable, LigneEcriture, PieceJustificative
MouvementInterFiliale
CompteBancaire, Cheque
RapprochementBancaire, LigneRapprochement
```

### Exceptions métier
- `RegleMetierException` — violation de règle métier (ex: modification d'une écriture VALIDEE)
- `EntiteIntrouvableException` — entité inexistante en base

### Garanties
- Aucun import `org.springframework.*` ou `jakarta.persistence.*`
- Modèles instanciables sans contexte Spring
- Testables en pur Java sans container

---

## Couche Application

**Package** : `com.menko.comptabilite.application`

### Responsabilité
Orchestre les cas d'utilisation métier. Définit les **ports** (interfaces) que les couches externes doivent implémenter.

### Ports entrants (Use Cases) — `port/in/`
Interfaces définissant les opérations disponibles pour la couche Présentation :
```
AuthUseCase, FilialeUseCase, CompteComptableUseCase, JournalComptableUseCase
TiersUseCase, EmployeUseCase, EcritureComptableUseCase, BalanceUseCase
BulletinPaieUseCase, ChequeUseCase, CompteBancaireUseCase
RapprochementBancaireUseCase, MouvementInterFilialeUseCase, DashboardUseCase
```

### Ports sortants (Repositories) — `port/out/`
Interfaces définissant les opérations de persistance, implémentées par l'Infrastructure :
```
15 interfaces Port (une par agrégat + RolePort, UtilisateurPort)
```

### Implémentations Use Case — `usecase/`
Contiennent toute la logique métier. Dépendent uniquement des ports sortants (jamais des JPA repositories directement).

**Exemple — workflow écriture** (`EcritureComptableUseCaseImpl`) :
```
creer()   → statut BROUILLON, vérification équilibre débit/crédit
valider() → BROUILLON → VALIDEE (guard : rejette si déjà VALIDEE/CLOTUREE)
cloturer()→ VALIDEE → CLOTUREE
supprimer()→ guard : interdit si statut != BROUILLON
modifier() → guard : interdit si statut != BROUILLON
```

### DTOs — `dto/`
- **Request** : objets de saisie avec validation Bean Validation (`@NotNull`, `@NotBlank`, etc.)
- **Response** : projections pour les éditions comptables
- `ApiResponse<T>` : envelope standard pour toutes les réponses REST
- `PageResponse<T>` : envelope pour les réponses paginées

---

## Couche Infrastructure

**Package** : `com.menko.comptabilite.infrastructure`

### Responsabilité
Connecte le domaine au monde extérieur (base de données, sécurité, configuration).

### Persistence — `persistence/`

#### Entités JPA (`entity/`)
16 classes annotées `@Entity`, mappées sur le schéma `menko_finance`. Séparées des modèles domaine.

#### Repositories Spring Data (`repository/`)
15 interfaces étendant `JpaRepository`. Contiennent les requêtes JPQL complexes (`@Query`) pour :
- Balance générale avec `COALESCE` et `CASE WHEN`
- Grand livre avec solde progressif initialisé à `0.0BD`
- Alertes chèques avec paramètre date dynamique

#### Adapters (`adapter/`)
16 classes `@Service` implémentant les **ports sortants** de la couche Application. Chaque adapter :
1. Reçoit un appel avec un modèle domaine
2. Le traduit en entité JPA
3. Délègue au repository Spring Data
4. Retraduit l'entité JPA en modèle domaine

**Garantie d'isolation** : la couche Application ne voit jamais une entité JPA.

### Security — `security/`

```
JwtUtil       ← génération et validation des tokens JWT
JwtFilter     ← OncePerRequestFilter, extrait userId et role du token
SecurityConfig← configuration Spring Security 6 (CSRF disabled, stateless, filtres)
UserDetailsServiceImpl ← chargement utilisateur depuis la base
```

**Flux d'authentification** :
```
POST /api/v1/auth/login
  → AuthController → AuthUseCaseImpl
  → UserDetailsServiceImpl.loadUserByUsername()
  → JwtUtil.genererToken(utilisateur)
  → TokenResponse { token, expiresIn }

Requêtes authentifiées :
  Header Authorization: Bearer <token>
  → JwtFilter.doFilterInternal()
  → JwtUtil.estValide(token) → extraireUserId() + extraireRole()
  → SecurityContextHolder.setAuthentication()
```

### Config — `config/`
- `OpenApiConfig` : configuration Swagger UI avec schéma de sécurité Bearer JWT
- `JpaAuditConfig` : activation de l'audit JPA (`@CreatedDate`, `@LastModifiedDate`)

---

## Couche Presentation

**Package** : `com.menko.comptabilite.presentation`

### Responsabilité
Interface HTTP uniquement. Les controllers ne contiennent **aucune logique métier**.

### Controllers (14)
- Injectent un unique Use Case via `@RequiredArgsConstructor`
- Délèguent immédiatement au Use Case
- Retournent un `ResponseEntity<ApiResponse<T>>`
- Annotés `@Tag` et `@Operation` pour la documentation Swagger

**Règle vérifiée** : aucun controller ne manipule d'entité JPA, de modèle de persistance, ou ne contient de bloc logique `if/else` métier.

### GlobalExceptionHandler (`advice/`)
Centralise la gestion des exceptions :
- `EntiteIntrouvableException` → HTTP 404
- `RegleMetierException` → HTTP 400
- `MethodArgumentNotValidException` → HTTP 400 (erreurs de validation Bean)
- `Exception` → HTTP 500 (avec log)

---

## Stratégie de tests

### Tests unitaires (Use Cases)
- `@ExtendWith(MockitoExtension.class)` + `@InjectMocks` / `@Mock`
- Aucun contexte Spring, aucune base de données
- Test de la logique métier pure : transitions de statut, guards, calculs
- Vérification des exceptions levées avec `assertThrows`

### Tests d'intégration (Application Context)
- `@SpringBootTest` + `@Testcontainers`
- `PostgreSQLContainer<>("postgres:16-alpine")` + `@ServiceConnection`
- Valide le démarrage complet du contexte Spring avec une vraie base PostgreSQL
- `ddl-auto=create-drop` + `connection-init-sql=CREATE SCHEMA IF NOT EXISTS menko_finance`

---

## Respect des principes SOLID

| Principe | Application |
|---|---|
| **S**ingle Responsibility | Chaque classe a une responsabilité unique (Use Case, Adapter, Controller séparés) |
| **O**pen/Closed | Nouveaux comportements via nouveaux Use Cases, pas de modification des existants |
| **L**iskov Substitution | Les adapters sont interchangeables via les ports (interfaces) |
| **I**nterface Segregation | 14 ports entrants distincts (pas un seul "ServiceFacade") |
| **D**ependency Inversion | Application dépend d'abstractions (ports), pas d'implémentations (JPA) |

---

## Flux de données complet

```
HTTP Request
  ↓
JwtFilter (auth)
  ↓
Controller (validation @Valid, extraction SecurityContext)
  ↓
UseCase Interface (port entrant)
  ↓
UseCaseImpl (logique métier, exceptions domaine)
  ↓
Port sortant (interface)
  ↓
Adapter (traduction domaine ↔ JPA)
  ↓
JpaRepository (SQL via Hibernate)
  ↓
PostgreSQL (schéma menko_finance)
  ↑
(chemin retour inverse)
  ↑
ResponseEntity<ApiResponse<T>>
  ↑
HTTP Response
```
