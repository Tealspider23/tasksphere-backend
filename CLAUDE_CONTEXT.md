# TaskSphere Backend – Project Context

TaskSphere is a Spring Boot backend for a collaborative task management system.

Core stack:
- Java 17+
- Spring Boot
- Spring Security (JWT, stateless)
- JPA / Hibernate
- PostgreSQL

Current features:
- User registration & login
- BCrypt password hashing
- JWT-based authentication
- Role-based authorization (USER, ADMIN)
- Tasks owned by users
- Pagination & filtering
- DTO-based API design

Upcoming features:
- Teams
- Team membership & roles
- Team-owned tasks
- Collaboration features

Architecture rules:
- Controllers handle HTTP only
- Services contain business logic
- Repositories contain persistence logic
- Entities are NOT exposed directly
- DTOs are used for all API responses

Coding principles:
- Clean separation of concerns
- Small, meaningful commits
- No rushed abstractions
- Security-first design
