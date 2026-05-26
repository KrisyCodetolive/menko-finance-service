# Menko Finance Service — API Backend

API REST du microservice Comptabilité & Caisse pour **Menko Holding**.

Architecture hexagonale · JWT stateless · 55+ endpoints · 65 tests

---

## Stack technique

| Outil | Version | Rôle |
|---|---|---|
| Java | 17 | Langage |
| Spring Boot | 3.5.0 | Framework API REST |
| Spring Data JPA | — | Persistance ORM |
| Spring Security 6 | — | Authentification JWT stateless |
| jjwt | 0.12.6 | Génération / validation tokens JWT |
| springdoc-openapi | 2.x | Documentation Swagger UI |
| PostgreSQL | 16 | Base de données |
| Maven | 3.9 | Build |
| Docker | — | Conteneurisation multi-stage |
| Testcontainers | 1.21.0 | Tests d'intégration PostgreSQL |

---

## Prérequis

| Outil | Version |
|---|---|
| Java | 17 |
| Maven | 3.9+ |
| Docker | Desktop ou Engine |

---

## Lancement via Docker Compose (recommandé)

Lance PostgreSQL + l'application en un seul commande :

```bash
JWT_SECRET=mon-secret-256-bits-minimum docker compose up -d
```

- API disponible sur : `http://localhost:8080`
- PostgreSQL disponible sur : `localhost:54322`

> Les migrations SQL du schéma `menko_finance` sont appliquées automatiquement au démarrage de PostgreSQL.

Pour arrêter :

```bash
docker compose down
```

---

## Lancement local (développement)

**1. Démarrer PostgreSQL via Docker**

```bash
docker compose up postgres -d
```

**2. Lancer l'application**

```bash
./mvnw spring-boot:run
```

L'API est accessible sur : `http://localhost:8080`

---

## Build et tests

```bash
# Compiler
./mvnw compile

# Lancer les 65 tests (64 unitaires + 1 intégration Testcontainers)
./mvnw clean verify

# Générer le .jar
./mvnw clean package -DskipTests

# Lancer en local
./mvnw spring-boot:run
```

> Les tests d'intégration requièrent Docker (Testcontainers lance un container PostgreSQL automatiquement).

---

## Documentation API (Swagger UI)

Une fois l'application lancée :

| Interface | URL |
|---|---|
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/api-docs |

---

## Authentification

L'API utilise JWT Bearer stateless. Pour accéder aux endpoints protégés :

**1. Obtenir un token**

```http
POST /api/v1/auth/login
Content-Type: application/json

{
  "login": "votre-login",
  "motDePasse": "votre-mot-de-passe"
}
```

**2. Utiliser le token**

```http
Authorization: Bearer <token>
```

---

## Variables d'environnement

| Variable | Description | Défaut (dev) |
|---|---|---|
| `JWT_SECRET` | Secret JWT (256 bits minimum en prod) | valeur dev interne |
| `SPRING_DATASOURCE_URL` | URL JDBC PostgreSQL | `localhost:54322` |
| `SPRING_DATASOURCE_USERNAME` | Utilisateur base de données | `postgres` |
| `SPRING_DATASOURCE_PASSWORD` | Mot de passe base de données | `postgres` |

---

## Rôles et accès

| Rôle | Accès |
|---|---|
| `SUPER_ADMIN` | Accès complet (gestion filiales, utilisateurs, plans) |
| `DG` | Lecture globale, tableaux de bord |
| `COMPTABLE` | Écritures, journaux, balance, grand livre |
| `TRESORIER` | Chèques, comptes bancaires, rapprochements |

---

## Accès base de données locale

| Service | URL |
|---|---|
| PostgreSQL | `postgresql://postgres:postgres@127.0.0.1:54322/postgres` |
| Schéma | `menko_finance` |
