# Task Manager REST API

A backend-only task manager built with Spring Boot 3, Java 17, Spring Data JPA, Bean Validation, Actuator, and an in-memory H2 database.

## Prerequisites

- Java 17 or newer
- Internet access on the first Maven Wrapper run so it can download Maven

## Build, test, and run

```sh
./mvnw clean test
./mvnw clean package
./mvnw clean verify
java -jar target/taskmanager-0.0.1-SNAPSHOT.jar
```

On Windows, use `mvnw.cmd` instead of `./mvnw`. JaCoCo writes its report to `target/site/jacoco/index.html` during `verify` and checks for at least 70% line coverage.

The API listens on port 8080. H2 uses an in-memory database, so data is reset when the application stops. The H2 console is available at `http://localhost:8080/h2-console`.

## Endpoints

| Method | Endpoint | Description |
| --- | --- | --- |
| POST | `/api/tasks` | Create a task |
| GET | `/api/tasks` | List tasks; optionally filter by `status` and/or `priority` |
| GET | `/api/tasks/{id}` | Get one task |
| PUT | `/api/tasks/{id}` | Fully update a task |
| PATCH | `/api/tasks/{id}/status` | Update only the task status |
| DELETE | `/api/tasks/{id}` | Delete a task |
| GET | `/actuator/health` | Application health |

Valid statuses: `TODO`, `IN_PROGRESS`, `DONE`. Valid priorities: `LOW`, `MEDIUM`, `HIGH`. If omitted at creation, status defaults to `TODO` and priority defaults to `MEDIUM`.

## Example requests

Create:

```sh
curl -i -X POST http://localhost:8080/api/tasks -H 'Content-Type: application/json' \
  -d '{"title":"Write unit tests","description":"Cover the service layer","status":"TODO","priority":"HIGH","dueDate":"2026-11-01"}'
```

List all tasks:

```sh
curl http://localhost:8080/api/tasks
```

Filter by status and priority:

```sh
curl 'http://localhost:8080/api/tasks?status=TODO&priority=HIGH'
```

Get a task:

```sh
curl http://localhost:8080/api/tasks/1
```

Fully update a task:

```sh
curl -i -X PUT http://localhost:8080/api/tasks/1 -H 'Content-Type: application/json' \
  -d '{"title":"Write integration tests","description":"Cover API flow","status":"IN_PROGRESS","priority":"MEDIUM","dueDate":"2026-11-10"}'
```

Update only its status:

```sh
curl -i -X PATCH http://localhost:8080/api/tasks/1/status -H 'Content-Type: application/json' \
  -d '{"status":"DONE"}'
```

Delete:

```sh
curl -i -X DELETE http://localhost:8080/api/tasks/1
```
