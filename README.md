# RevenueOS — Backend

Spring Boot backend for RevenueOS, a multi-tenant revenue and operations management platform.

## 🚀 Live demo

> **Not deployed yet.** After deploying, add your own links here, e.g.
> App `https://<your-app>.vercel.app` · API `https://<your-backend>.onrender.com`.
> (Don't use `revenue-os.vercel.app` / `revenue-os-backend.onrender.com` — those
> are placeholders that may belong to unrelated apps.)

Once live, sign in with **`admin@demo.io` / `Password123!`**. API docs (Swagger UI)
are at `/swagger-ui.html`. See [`DEPLOYMENT.md`](DEPLOYMENT.md) for the free
Vercel + Render + Neon setup.

## Stack

- Java 21, Spring Boot 3.3
- Spring Security with JWT (access + rotating refresh tokens)
- Spring Data JPA / Hibernate, PostgreSQL
- Flyway migrations, MapStruct, Lombok
- springdoc OpenAPI, Actuator

## Prerequisites

- JDK 21
- PostgreSQL 14+ (or use the bundled `docker-compose.yml`)

## Running locally

1. Create the database and copy the environment template:

   ```bash
   createdb revenueos
   cp .env.example .env   # adjust DB credentials if needed
   ```

2. Start the app (uses the Maven wrapper):

   ```bash
   ./mvnw spring-boot:run
   ```

   The API is served at `http://localhost:8080`. On the `dev` profile a demo
   company is seeded automatically — **admin@demo.io / Password123!**

### With Docker

```bash
docker compose up --build
```

This starts PostgreSQL and the backend together.

## API docs

Swagger UI: `http://localhost:8080/swagger-ui.html`

### Authentication endpoints (Phase 1)

| Method | Path | Description |
| ------ | ---- | ----------- |
| POST | `/api/v1/auth/register-company` | Create an organization and its first admin |
| POST | `/api/v1/auth/login` | Log in, returns access + refresh tokens |
| POST | `/api/v1/auth/refresh` | Rotate tokens using a refresh token |
| POST | `/api/v1/auth/logout` | Revoke a refresh token |
| POST | `/api/v1/auth/forgot-password` | Request a password reset token |
| POST | `/api/v1/auth/reset-password` | Set a new password with a reset token |
| GET | `/api/v1/auth/me` | Current authenticated user |

## Tests

```bash
./mvnw test
```

## Architecture

Feature-first packages under `com.revenueos` (auth, organization, user, role,
permission, …). Each module keeps its controller, service, repository, entity,
DTOs and mapper together. Business logic lives in services; controllers stay
thin. Every tenant-scoped query is filtered by the authenticated user's
organization — organization ids are never trusted from the client.
