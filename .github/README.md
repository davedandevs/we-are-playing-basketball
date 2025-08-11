# Basketball — Player & Game Tracking System

A compact backend for tracking basketball **players**, **teams**, **seasons**, **matches**, and per-match **player stats**. Built to be boringly reliable on game day: fast local dev, reproducible environments, clean architecture.

- **Live (prod):** https://basketball.rabko.online/
- **Stack:** Java • Spring Boot • Gradle (Kotlin DSL) • PostgreSQL • JPA/Hibernate • Spring Security • JWT (JJWT) • Testcontainers • JUnit 5 / Mockito / RestAssuredMockMvc • JaCoCo

---

## Overview

The service exposes a secured REST API for managing basketball data and computing/serving statistics. It uses JWT-based authentication with role-aware access, persists to PostgreSQL, and ships as a standalone Spring Boot application that runs locally or in Docker.

---

## Architecture & Tech Stack

- **Language & Runtime:** Java 17+ (21 recommended)
- **Frameworks:** Spring Boot (Web, Security, Validation), Spring Data JPA (Hibernate)
- **Database:** PostgreSQL (JSONB used for flexible stat payloads)
- **Auth:** JWT via JJWT (configurable expiration, issuer, secret)
- **Build:** Gradle (Kotlin DSL), Spring Boot plugin → fat JAR
- **Packaging & Ops:** Dockerfile for app image; Docker Compose for local Postgres
- **Testing:**
    - Unit & component tests with **JUnit 5**, **Mockito** (`@Mock`, `@InjectMocks`, `@ExtendWith(MockitoExtension.class)`)
    - Controller/service tests via **RestAssuredMockMvc** (no full Spring context spin-up)
    - Optional integration with **Testcontainers** (ephemeral Postgres)
    - Coverage via **JaCoCo** (target ≥ 80%)
- **Docs:** OpenAPI/Swagger (if enabled) at `/swagger-ui` and `/v3/api-docs`

> Philosophy: keep it simple, minimize surprises, and make local dev a two-command story.

---

## Requirements

- **Java:** 17+ (21 recommended)
- **Gradle:** wrapper included (`./gradlew` / `gradlew.bat`)
- **Docker & Docker Compose:** for local PostgreSQL (or use your own Postgres 14+)
- **PostgreSQL:** running instance reachable from the app

---

## Quick Start (Local)

```bash
# 1) Clone
git clone https://github.com/davedandevs/basketball.git
cd basketball
git checkout main

# 2) Start Postgres (Docker)
docker compose up -d

# 3) Configure environment (see section below), then:
./gradlew clean build
./gradlew bootRun
```

## Contributing

We welcome contributions from the community. Feel free to drop an ISSUE or branch out and drop a PR.

## License

This project is licensed under the MIT License. See the [LICENSE](../LICENSE) file for details.

## Contact

If you have any questions or feedback, please feel free to contact us or create an issue
in [Issues](https://github.com/davedandevs/basketball/issues/new/choose).
