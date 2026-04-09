# Menko Comptabilité & Caisse — API

API REST du microservice Comptabilité & Caisse pour **Menko Holding**.

---

## Prérequis

| Outil | Version |
|---|---|
| Java | 17 |
| Maven | 3.9+ |
| Docker | dernière version |
| Supabase CLI | 2.84.2+ |

---

## Installation locale (développement)

**1. Cloner le projet**
```bash
git clone <url-du-repo>
cd comptabilite-caisse
```

**2. Démarrer Supabase**
```bash
cd ..
supabase start
```

**3. Créer les tables**
```bash
supabase db reset
```

**4. Lancer l'API**
```bash
cd comptabilite-caisse
./mvnw spring-boot:run
```

L'API est accessible sur : `http://localhost:8080`

---

## Configuration

Le fichier `src/main/resources/application.properties` contient la configuration de connexion à la base de données.

Pour le développement local avec Supabase :

```properties
spring.datasource.url=jdbc:postgresql://127.0.0.1:54322/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
```

---

## Stack technique

| Outil | Version | Rôle |
|---|---|---|
| Java | 17 | Langage |
| Spring Boot | 3.5.0 | Framework API REST |
| Spring Data JPA | - | Accès base de données |
| Spring Security | - | Gestion des rôles |
| PostgreSQL | 16 | Base de données |
| Maven | 3.9 | Build |
| Docker | - | Conteneurisation |

---

## Accès Supabase local

| Service | URL |
|---|---|
| Studio (interface visuelle) | http://127.0.0.1:54323 |
| PostgreSQL | postgresql://postgres:postgres@127.0.0.1:54322/postgres |

---

## Build et compilation

```bash
./mvnw compile        # compiler
./mvnw test           # lancer les tests
./mvnw package        # générer le .jar
./mvnw spring-boot:run # lancer en local
```
