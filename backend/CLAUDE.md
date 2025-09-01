# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

### Primary Commands
- **Build**: `./gradlew build` - Compiles Kotlin/Java code and runs all tests
- **Run Application**: `./gradlew bootRun` - Starts the Spring Boot application on port 8080
- **Run Tests**: `./gradlew test` - Runs all unit and integration tests
- **Run Single Test**: `./gradlew test --tests "ClassName.methodName"` or `./gradlew test --tests "ClassName"`

### Development Environment
- **Local Development**: Uses H2 in-memory database, profile activated via `application.yml` (currently set to `temp`)
- **Test Profile**: `@ActiveProfiles("test")` - Uses H2 with create-drop DDL mode
- **Docker**: `docker-compose up` - Runs full environment with external database

## Architecture Overview

### Technology Stack
- **Framework**: Spring Boot 3.5.3 with Kotlin 1.9.25 (migrating from Java)
- **Language**: Mixed Java/Kotlin codebase (ongoing migration to Kotlin)
- **Database**: H2 (dev/test), MySQL/PostgreSQL (production), Redis (caching/sessions)
- **Security**: Spring Security with JWT authentication and OAuth2 (Kakao)
- **Build**: Gradle with Kotlin DSL, Java 21

### Domain-Driven Architecture
Located in `src/main/java/com/back/domain/`, organized by business domains:

- **adoption**: Pet adoption process management
- **applicant**: Adoption applicant handling
- **care**: Pet care services and tracking
- **chat**: Real-time messaging system (WebSocket)
- **member**: User management and authentication
- **notification**: User notification system
- **pet**: Pet entity management
- **shelter**: Shelter information management

Each domain follows consistent structure:
```
domain/{name}/
├── controller/     # REST endpoints
├── dto/           # Data transfer objects
├── entity/        # JPA entities
├── enums/         # Domain enums
├── repository/    # Data access layer
└── service/       # Business logic
```

### Global Configuration (`src/main/java/com/back/global/`)
- **config/**: Application configuration (Swagger, Redis, Health checks)
- **security/**: JWT authentication, OAuth2, Spring Security setup
- **exception/**: Global exception handling
- **common/**: Shared DTOs, entities, enums
- **dev/**: Development utilities and controllers

### Key Integration Points

#### Authentication Flow
- JWT-based authentication with refresh tokens
- OAuth2 integration with Kakao social login
- Custom `UserDetailsService` implementation
- JWT filters handle request authentication

#### Entity Relationships
- Member entity has private `_email` and `_password` fields (accessed via getters)
- Bidirectional relationships between Member, Pet, Care, Adoption entities
- All entities use JPA auditing for timestamp management

#### Test Configuration
- Uses `@SpringBootTest` with `@AutoConfigureMockMvc` for integration tests
- H2 database with separate test configuration
- `@Transactional` for test isolation
- Custom test data initialization in `@BeforeEach` methods

### Important Development Notes

#### Kotlin Migration Status
- Pet domain has been migrated to Kotlin
- Member domain partially migrated (entities are Kotlin, some tests remain mixed)
- When creating new Member entities in tests, use `_email` and `_password` parameters
- Follow existing Kotlin conventions for new code

#### Environment Profiles
- Multiple environment configurations: dev, test, prod, docker, fly, render
- Default profile is `temp` - check `application.yml` for active profile
- JWT secrets and OAuth2 credentials configured per environment

#### Database Access Patterns
- Spring Data JPA repositories with custom query methods
- Uses Hibernate with SQL logging enabled in development
- PostgreSQL for production deployments, H2 for local development