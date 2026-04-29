# Case Manager

Monorepo for the Case Manager backend (Spring Boot) and frontend (Vite + React).

This file is intentionally practical: what is in the repo, how to run it, and what to do when something breaks.

## Repository layout

- `src/` - Spring Boot backend source code
- `itsm-frontend/` - React frontend
- `docker-compose.yml` - local Postgres + ELK stack
- `.env.example` - environment variable template for backend secrets and app config

## Tech stack

Backend:

- Java 25
- Spring Boot 4
- Spring Security + GitHub OAuth2 login
- Spring Data JPA (PostgreSQL)
- SpringDoc OpenAPI

Frontend:

- React 19 + TypeScript
- Vite
- React Router

Local infra:

- PostgreSQL 16
- Elasticsearch + Kibana + Filebeat

## Prerequisites

- JDK 25
- Maven (or use `./mvnw`)
- Node.js 20+ and npm
- Docker + Docker Compose

## Environment setup

Create local environment file from the template:

```bash
cp .env.example .env
```

Minimum variables to fill for backend startup:

- `GITHUB_CLIENT_ID`
- `GITHUB_CLIENT_SECRET`
- `SPRING_DATASOURCE_URL`
- `SPRING_DATASOURCE_USERNAME`
- `SPRING_DATASOURCE_PASSWORD`
- `AWS_S3_BUCKET`
- `AWS_REGION`
- `AWS_S3_PUBLIC_BASE_URL`

For local development, backend can run with the `dev` profile and local Postgres from Docker compose:

- datasource URL: `jdbc:postgresql://localhost:5432/case_manager_db`
- username: `root`
- password: `root`

## Run local infrastructure

Start Postgres and ELK stack:

```bash
docker compose up -d
```

Stop everything:

```bash
docker compose down
```

## Run backend

Run in dev profile:

```bash
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

Backend defaults to:

- API base URL: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/v3/api-docs`

## Run frontend

In another terminal:

```bash
cd itsm-frontend
npm install
npm run dev
```

Frontend defaults to:

- UI: `http://localhost:5173`
- backend API: `http://localhost:8080`

## Tests

Backend tests:

```bash
./mvnw test
```

Frontend lint/build:

```bash
cd itsm-frontend
npm run lint
npm run build
```

## Notes

- Backend logs are written to `logs/application.log`.
- `application-test.yml` uses in-memory H2 and disables storage provider (`app.storage.provider: none`).
- If GitHub login redirects fail, confirm `APP_FRONTEND_URL` and OAuth callback settings match your local URLs.
