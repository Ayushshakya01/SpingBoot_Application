# Project Spec: Task Manager REST API

> Use this file as the single source of truth when asking an AI coding assistant to build the application. Build exactly what is described here. Do not add DevOps, CI/CD, Docker or deployment files.

## 1. Overview

A backend-only REST API for managing tasks (a simple to-do / project tracker). There is no frontend. The app is intentionally small and standard so it can later be used as the sample application for a CI/CD pipeline project.

## 2. Tech Stack

- Java 17
- Spring Boot 3.x
- Maven (with Maven Wrapper)
- Spring Web
- Spring Data JPA
- Spring Validation (Bean Validation)
- Spring Boot Actuator
- H2 in-memory database
- Lombok (optional, for boilerplate reduction)
- JUnit 5 and Mockito for tests
- JaCoCo plugin for coverage reporting

**Project coordinates**
- Group: `com.example`
- Artifact: `taskmanager`
- Base package: `com.example.taskmanager`
- Java version: 17
- Packaging: jar

## 3. Package Structure

```
com.example.taskmanager
├── TaskManagerApplication.java
├── controller
│   └── TaskController.java
├── service
│   └── TaskService.java
├── repository
│   └── TaskRepository.java
├── model
│   ├── Task.java
│   ├── TaskStatus.java
│   └── TaskPriority.java
├── dto
│   ├── TaskRequest.java
│   ├── TaskResponse.java
│   ├── StatusUpdateRequest.java
│   └── ErrorResponse.java
└── exception
    ├── TaskNotFoundException.java
    └── GlobalExceptionHandler.java
```

## 4. Data Model

### Entity: `Task`

| Field | Type | Rules |
|---|---|---|
| id | Long | Auto-generated primary key |
| title | String | Required, not blank, max 100 characters |
| description | String | Optional, max 500 characters |
| status | TaskStatus | Required, defaults to `TODO` |
| priority | TaskPriority | Required, defaults to `MEDIUM` |
| dueDate | LocalDate | Optional |
| createdAt | LocalDateTime | Set automatically on create |
| updatedAt | LocalDateTime | Set automatically on create and update |

### Enums

- `TaskStatus`: `TODO`, `IN_PROGRESS`, `DONE`
- `TaskPriority`: `LOW`, `MEDIUM`, `HIGH`

## 5. API Endpoints

Base path: `/api/tasks`

| Method | Endpoint | Description | Success | Errors |
|---|---|---|---|---|
| POST | `/api/tasks` | Create a task | 201 + created task | 400 on validation failure |
| GET | `/api/tasks` | List all tasks. Optional query params: `status`, `priority` | 200 + list | 400 on invalid enum value |
| GET | `/api/tasks/{id}` | Get a single task | 200 + task | 404 if not found |
| PUT | `/api/tasks/{id}` | Fully update a task | 200 + updated task | 400, 404 |
| PATCH | `/api/tasks/{id}/status` | Update only the status | 200 + updated task | 400, 404 |
| DELETE | `/api/tasks/{id}` | Delete a task | 204 no content | 404 |

### Request body: create / update (`TaskRequest`)

```json
{
  "title": "Write unit tests",
  "description": "Cover the service layer",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-11-01"
}
```

- `title` is required.
- `status` and `priority` are optional on create (use defaults).

### Request body: status update (`StatusUpdateRequest`)

```json
{ "status": "DONE" }
```

### Response body (`TaskResponse`)

```json
{
  "id": 1,
  "title": "Write unit tests",
  "description": "Cover the service layer",
  "status": "TODO",
  "priority": "HIGH",
  "dueDate": "2026-11-01",
  "createdAt": "2026-10-17T17:40:00",
  "updatedAt": "2026-10-17T17:40:00"
}
```

### Error response (`ErrorResponse`)

```json
{
  "timestamp": "2026-10-17T17:40:00",
  "status": 400,
  "error": "Bad Request",
  "message": "title: must not be blank",
  "path": "/api/tasks"
}
```

## 6. Behaviour Requirements

1. Use a layered architecture: Controller -> Service -> Repository.
2. Never expose the entity directly from the controller. Use DTOs and map between them.
3. Validate input with Bean Validation (`@NotBlank`, `@Size`, `@Valid`).
4. Throw `TaskNotFoundException` when an id does not exist; map it to HTTP 404.
5. Implement a `@RestControllerAdvice` (`GlobalExceptionHandler`) that returns the `ErrorResponse` JSON for:
   - Validation errors (`MethodArgumentNotValidException`) -> 400
   - Malformed JSON or invalid enum values (`HttpMessageNotReadableException`) -> 400
   - Invalid query param enum (`MethodArgumentTypeMismatchException`) -> 400
   - `TaskNotFoundException` -> 404
   - Any other exception -> 500
6. Set `createdAt` / `updatedAt` automatically (`@PrePersist` / `@PreUpdate` or JPA auditing).
7. Filtering on `GET /api/tasks`: if `status` and/or `priority` are provided, filter by them (both together means AND). With neither, return all tasks.

## 7. Configuration

`src/main/resources/application.properties`:

```properties
spring.application.name=taskmanager
server.port=8080

spring.datasource.url=jdbc:h2:mem:taskdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.hibernate.ddl-auto=update
spring.jpa.open-in-view=false
spring.h2.console.enabled=true

management.endpoints.web.exposure.include=health,info
management.endpoint.health.show-details=always
```

## 8. Testing Requirements

Target: at least 70% line coverage.

- **Service tests** (`TaskServiceTest`, JUnit 5 + Mockito): create, get by id, get not found, list with and without filters, update, update status, delete, delete not found.
- **Controller tests** (`TaskControllerTest`, `@WebMvcTest` + `MockMvc`, service mocked): every endpoint's success path, validation failure (blank title, title too long), 404 handling, invalid enum value.
- **Integration test** (`TaskIntegrationTest`, `@SpringBootTest` + `@AutoConfigureMockMvc`): full create -> get -> update -> patch status -> delete flow against H2.
- **Repository test** (optional, `@DataJpaTest`): filtering queries.

Add the JaCoCo Maven plugin so `mvn clean verify` generates a coverage report at `target/site/jacoco/index.html`.

## 9. Definition of Done

- `./mvnw clean test` passes
- `./mvnw clean package` produces `target/taskmanager-0.0.1-SNAPSHOT.jar`
- `java -jar target/taskmanager-0.0.1-SNAPSHOT.jar` starts the app on port 8080
- All six endpoints work as described (verify with curl or Postman)
- `GET /actuator/health` returns `{"status":"UP", ...}`
- Coverage is at least 70%

## 10. README Requirements

Create a `README.md` containing:
- Short project description
- Prerequisites (Java 17)
- How to build, test and run
- Endpoint table
- Example curl commands for each endpoint

## 11. Suggested Build Order for the AI

1. Generate the Maven project with the dependencies listed in section 2.
2. Create the enums and the `Task` entity.
3. Create `TaskRepository` (with filter queries).
4. Create the DTOs and a mapper (manual mapping is fine).
5. Create `TaskService`.
6. Create `TaskController`.
7. Create the exception classes and `GlobalExceptionHandler`.
8. Add `application.properties`.
9. Write the tests (service, controller, integration).
10. Add the JaCoCo plugin.
11. Write the README.
12. Run `./mvnw clean verify` and fix any failures.

## 12. Out of Scope

Do **not** add: authentication, a frontend, Dockerfiles, GitHub Actions workflows, Kubernetes manifests, or any deployment configuration. Those will be added separately later.





#How to run the application

Open a terminal in the springBoot rest API folder and run:
./mvnw spring-boot:run


Then open http://localhost:8080/api/tasks. Check the app health at http://localhost:8080/actuator/health.


To build and run the packaged app instead:
./mvnw clean package
java -jar target/taskmanager-0.0.1-



# Result

Once the app is running:
- http://localhost:8080/api/tasks returns a JSON list of tasks. On a fresh start, it will likely be empty:
  []

  
  Tasks you create are stored in the in-memory H2 database and disappear when the app stops.
- http://localhost:8080/actuator/health returns the app’s health status, typically:
  {
    "status": "UP",
    "components": {
      "db": {
        "status": "UP"
      },
      "diskSpace": {
        "status": "UP"
      },
      "ping": {
        "status": "UP"
      }
    }
  }
  The health response can include other components or details, depending on Spring Boot’s checks.