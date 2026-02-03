# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Common Development Commands

| Command | Description |
|---------|-------------|
| `mvn clean package` | Compile the project and build an executable jar in `target/`. |
| `mvn spring-boot:run` | Start the application in development mode (uses `application.properties` for dev settings). |
| `mvn test` | Run all unit and integration tests. |
| `mvn -Dtest=ClassName#methodName test` | Run a single test method (replace `ClassName` and `methodName`). |
| `mvn dependency:tree` | Inspect the dependency graph. |
| `mvn javadoc:javadoc` | Generate JavaDoc for the source code. |

## Project Architecture Overview

The project follows a conventional **Spring Boot** layered architecture:

1. **Controllers** (`src/main/java/com/tasksphere/controller`) – Handle HTTP requests and responses. They expose REST endpoints and delegate to services.
2. **Services** (`src/main/java/com/tasksphere/service`) – Contain business logic. Each service typically operates on a single domain concept (e.g., `UserService`, `TaskService`).
3. **Repositories** (`src/main/java/com/tasksphere/repository`) – Spring Data JPA interfaces for persistence. They abstract database interactions and are injected into services.
4. **Entities** (`src/main/java/com/tasksphere/model`) – JPA entity classes that map to database tables. They are never returned directly to clients.
5. **DTOs** (`src/main/java/com/tasksphere/dto`) – Plain data transfer objects used in controller responses and request bodies. They shape the API contract.
6. **Security** – Configured via `SecurityConfig` in `src/main/java/com/tasksphere/config`. Uses JWT tokens for stateless authentication and role‑based access control.
7. **Configuration** – Application properties reside in `src/main/resources/application.yml`. Database URL, JWT secret, and other settings are externalized.

### Build and Runtime
- The build uses **Maven** with the Spring Boot parent POM (`spring-boot-starter-parent`).
- Java **version 25** is configured (see `<java.version>` in `pom.xml`).
- The application runs as an executable JAR that can be started with `java -jar target/tasksphere-0.0.1-SNAPSHOT.jar`.

### Testing
- Tests are written with Spring's test starters (`spring-boot-starter-data-jpa-test` and `spring-boot-starter-webmvc-test`).
- They reside under `src/test/java` and follow the same package structure as production code.

## Useful References
- [Spring Boot Reference Guide](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/)
- [Spring Data JPA Guide](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/)
- [Spring Security Reference](https://docs.spring.io/spring-security/reference/)
- Maven documentation for running specific tests or plugins.

## Important Code Patterns
- **No direct exposure of JPA entities** – All data exposed through the API uses DTOs.
- **JWT authentication** – Tokens are signed with HS256 using a secret from `application.yml`.
- **Role based access** – `@PreAuthorize("hasRole('ADMIN')")` annotations guard privileged operations.
- **Service‑layer transaction management** – `@Transactional` is applied at the service level to ensure atomic operations.

## Claude Operating Rules (Mandatory)

### Commit workflow
When preparing a commit or when the user types "commit":

1. Automatically run:
    - `git status --porcelain`
    - `git diff`
2. Analyze the changes and perform a lightweight code review.
3. Identify ONE logical, complete change suitable for a single commit.
4. Ignore unfinished, experimental, or unrelated code.
5. Propose ONE commit including:
    - Summary of the change
    - Files to be included
    - A conventional commit message
6. Ask the user to approve, edit, or skip.
7. NEVER commit or push without explicit user approval.

### Code review behavior
- Point out security risks, especially around authentication and authorization.
- Flag architectural violations (controller logic leakage, entity exposure, etc.).
- Flag unused imports, dead code, and obvious smells.
- Do NOT refactor code unless explicitly asked.
- Do NOT apply changes automatically.

### General behavior
- Prefer clarity over cleverness.
- Prefer small, incremental changes.
- Avoid premature abstractions.
- Be explicit about tradeoffs and assumptions.


Feel free to refer to the README or the `HELP.md` for additional setup instructions or external documentation links.
