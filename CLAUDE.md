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

1. **Controllers** (`src/main/java/com/tasksphere/controller`) – Handle HTTP requests and responses. They expose REST endpoints and delegate to services (e.g., `TeamController`).
2. **Services** (`src/main/java/com/tasksphere/service`) – Contain business logic. Each service typically operates on a single domain concept (e.g., `UserService`, `TaskService`, `TeamService`).
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

### Automated Commit Workflow

When preparing commits or when the user types "commit":

1. Automatically run:
    - `git status --porcelain`
    - `git diff`

2. Analyze ALL current changes.

3. Group changes into the MINIMUM number of logical commits.
    - Prefer 1 commit if possible.
    - Use multiple commits only if concerns are clearly separate.

4. For each proposed commit:
    - Select relevant files automatically.
    - Write a clean conventional commit message.
    - Provide a short summary.

5. Present ALL proposed commits at once in this format:

   Commit Plan:
    - Commit 1: <message>
      Files: ...
    - Commit 2: <message>
      Files: ...

6. Ask the user ONCE:

   "Proceed with this commit plan? (yes/edit/cancel)"

7. If approved, execute the following steps in order for each commit:

   a. Stage files:
   `git add <file1> <file2> ...`

   b. Create commit:
   `git commit -m "<commit message>"`

8. After all commits are created, ask:

   "Push to remote? (yes/no)"

9. If approved, run:
   `git push`

10. Do NOT skip steps, and do NOT invent commands.


Feel free to refer to the README or the `HELP.md` for additional setup instructions or external documentation links.
