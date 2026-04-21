# TaskFlow - Task Management Application with Spring Boot Backend

A task management application showcasing backend development expertise with Spring Boot, built as part of my continuous learning journey.

## Project Context

This project demonstrates my **backend development skills** with Spring Boot, Java, and RESTful API design. On top of the core CRUD layer, I've layered the production-readiness features a real backend portfolio needs:

- Stateless **JWT authentication** with Spring Security 6 (register, login, per-user resource scoping)
- **OpenAPI 3 / Swagger UI** documentation, annotated end-to-end
- **Flyway** versioned schema migrations
- **Bean Validation** on every request DTO, with a uniform `ErrorResponse` shape
- **Multi-stage Dockerfile** and a full `docker compose` stack (Postgres + backend in one command)
- **Externalised secrets** via environment variables (DB credentials, JWT signing key)
- **GitHub Actions CI** — backend tests, frontend lint + build, and Docker image build

The frontend serves as a functional interface to demonstrate the API; my primary expertise and focus is on the backend.

**Current Focus**: Backend architecture, API design, authentication, and production-readiness tooling.

## Project Overview

TaskFlow is a task management application that enables users to organize their work through task lists and individual tasks. The application features a Spring Boot backend with PostgreSQL persistence and a React frontend interface.


## Key Features (Backend Implementation)

### Core Backend Features
- **RESTful API Design**: Fully functional REST endpoints following industry standards
- **Task List Management**: Complete CRUD operations with automatic progress calculation
- **Task Operations**: Nested resource management within task lists
- **Priority & Status System**: Enum-based task organization (LOW, MEDIUM, HIGH priority)
- **Database Relationships**: Proper JPA entity relationships and cascade operations
- **Exception Handling**: Global exception handler with a uniform `ErrorResponse` shape
- **Progress Tracking**: Service-layer logic for calculating task completion percentages

### Security & Authentication
- **Stateless JWT authentication** (JJWT 0.12.x, HS512)
- **Spring Security 6** with a custom `JwtAuthFilter` in front of `UsernamePasswordAuthenticationFilter`
- **BCrypt** password hashing
- **Per-user resource scoping** — task lists and their tasks are only visible to the owning user
- Custom `AuthenticationEntryPoint` (401) and `AccessDeniedHandler` (403) that return the project's `ErrorResponse` JSON shape
- JWT signing secret read from the `JWT_SECRET` env var (required to decode to ≥ 64 bytes for HS512)

### Production-Readiness
- **Flyway** versioned migrations (`V1` baseline schema, `V2` users + task-list ownership)
- **OpenAPI 3 / Swagger UI** (`/api/swagger-ui.html`) with bearer-auth integrated — click "Authorize" and paste a JWT to try protected endpoints
- **Bean Validation** (`@NotBlank`, `@Size`, etc.) on every request DTO with field-level error reporting
- **Externalised secrets** via env vars (DB credentials, JWT secret) with safe dev defaults
- **Multi-stage Dockerfile** and docker-compose wiring (Postgres + backend, one command)
- **GitHub Actions CI** running on every push / PR to `master`

### Frontend Features
- Basic CRUD interface for task and task list management
- Login / Register flow with JWT persistence + Axios interceptor
- Progress visualization with NextUI components
- Responsive design with Tailwind CSS

## Technology Stack

### Backend (Primary Focus)
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Security**: Spring Security 6, JJWT 0.12.x (stateless JWT), BCrypt
- **Database**: PostgreSQL 16 (production), H2 (testing)
- **ORM**: Spring Data JPA / Hibernate
- **Migrations**: Flyway
- **API docs**: SpringDoc OpenAPI 2.8.9 (Swagger UI at `/api/swagger-ui.html`)
- **Validation**: Jakarta Bean Validation
- **Build Tool**: Maven
- **Containerization**: Multi-stage Docker build + Docker Compose
- **CI**: GitHub Actions (backend tests, frontend lint+build, Docker image build)
- **Testing**: JUnit 5, Mockito, Spring Boot Test, AssertJ (130 tests)

### Frontend (Supporting Interface)
- **Framework**: React 18 with TypeScript
- **Build Tool**: Vite
- **UI Library**: NextUI
- **Styling**: Tailwind CSS
- **Routing**: React Router v6
- **HTTP Client**: Axios

## Backend Architecture 

### Project Structure

### Backend Project Structure

```
# Main Directory
backend/src/main/java/com/gozzerks/taskflow/
├── TaskflowApplication.java      # Spring Boot main application
├── auth/                         # Security + JWT stack (my own addition)
│   ├── AuthController.java       # /api/auth/register, /api/auth/login
│   ├── AuthService.java
│   ├── JwtService.java           # sign / parse / validate
│   ├── JwtAuthFilter.java        # per-request bearer-token filter
│   ├── SecurityConfig.java       # stateless chain, entry point, denied handler
│   └── dto/                      # RegisterRequest, LoginRequest, AuthResponse
├── config/
│   └── OpenApiConfig.java        # bearerAuth scheme for Swagger UI
├── controllers/                  # REST API endpoints
│   ├── TaskController.java
│   ├── TaskListController.java
│   └── GlobalExceptionHandler.java
├── exceptions/
│   └── NotFoundException.java    # → 404 via GlobalExceptionHandler
├── services/                     # Business logic layer
│   ├── TaskService.java          # Service interface
│   ├── TaskListService.java      # Service interface
│   └── impl/
│       ├── TaskServiceImpl.java
│       └── TaskListServiceImpl.java
├── repositories/                 # Data access layer (Spring Data JPA)
│   ├── TaskRepository.java
│   ├── TaskListRepository.java
│   └── UserRepository.java
├── domain/
│   ├── entities/                 # JPA entities
│   │   ├── User.java             # BCrypt-hashed password, owns task lists
│   │   ├── TaskList.java
│   │   ├── Task.java
│   │   ├── TaskStatus.java       # Enum: OPEN, CLOSED
│   │   └── TaskPriority.java     # Enum: LOW, MEDIUM, HIGH
│   └── dto/                      # Data Transfer Objects (+ OpenAPI @Schema)
│       ├── TaskDTO.java
│       ├── TaskListDTO.java
│       └── ErrorResponse.java
└── mappers/                      # Entity-DTO conversion
    ├── TaskMapper.java
    ├── TaskListMapper.java
    └── impl/
        ├── TaskMapperImpl.java
        └── TaskListMapperImpl.java

backend/src/main/resources/
├── application.properties        # env-driven config (DB creds, JWT secret)
└── db/migration/                 # Flyway versioned migrations
    ├── V1__init_schema.sql
    └── V2__add_users_and_task_list_ownership.sql

# Test Directory
backend/src/test/java/com/gozzerks/taskflow/
├── controllers/                    # REST API integration tests
│   ├── TaskControllerTest.java
│   ├── TaskListControllerTest.java
│   └── GlobalExceptionHandlerTest.java
├── mappers/impl/                   # Mapper conversion tests
│   ├── TaskMapperImplTest.java
│   └── TaskListMapperImplTest.java
├── repositories/                   # Data layer tests
│   └── TaskRepositoryTest.java
└── services/impl/                  # Service layer implementation tests
    ├── TaskListServiceImplTest.java
    └── TaskServiceImplTest.java

```

### Frontend Project Structure


```
frontend/src/
├── main.tsx                   # Application entry point
├── App.tsx                    # Root component with routing
├── App.css                    # Global styles
├── index.css                  # Tailwind CSS imports
├── AppProvider.tsx            # Global state management, API calls & useAppContext hook
├── components/                # React components
│   ├── TaskListsScreen.tsx    # Display all task lists
│   ├── TasksScreen.tsx        # Display tasks in a list
│   ├── CreateUpdateTaskListScreen.tsx  # Create/edit task lists
│   └── CreateUpdateTaskScreen.tsx      # Create/edit tasks
└── domain/                    # TypeScript types & interfaces
    ├── Task.ts                # Task interface
    ├── TaskList.ts            # TaskList interface
    ├── TaskPriority.ts        # Enum: LOW, MEDIUM, HIGH
    └── TaskStatus.ts          # Enum: OPEN, CLOSED
```


### Design Patterns & Best Practices Implemented

**Repository Pattern**
- Clean separation of data access logic
- Spring Data JPA repositories with custom query methods

**Service Layer Pattern**
- Business logic encapsulation
- Transaction management with `@Transactional`

**DTO Pattern**
- API response/request objects separate from domain entities
- Custom mappers for entity-DTO conversion
- Calculated fields (e.g., progress percentage) in DTOs

**Exception Handling**
- Centralized error handling with `@ControllerAdvice`
- Custom exception types for different error scenarios
- Meaningful HTTP status codes and error messages

**Progress Calculation Logic**
```java
// Implemented in TaskListMapperImpl
private Double calculateTaskListProgress(List<Task> tasks) {
    if (null == tasks) {
        return null;
    }
    long closedTaskCount = tasks.stream()
        .filter(task -> TaskStatus.CLOSED == task.getStatus())
        .count();
    return (double) closedTaskCount / tasks.size();
}
```

## Database Schema & Relationships

### TaskList Entity
```java
@Entity
@Table(name = "task_list")
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String title;
    private String description;
    
    @OneToMany(mappedBy = "taskList", cascade = {CascadeType.REMOVE, CascadeType.PERSIST})
    private List<Task> tasks;
    
    private LocalDateTime created;
    private LocalDateTime updated;
}
```

### Task Entity
```java
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    private String title;
    private String description;
    private LocalDateTime dueDate;
    
    private TaskStatus status;
    private TaskPriority priority;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "task_list_id")
    private TaskList taskList;
    
    private LocalDateTime created;
    private LocalDateTime updated;
}
```

**Relationship**: One-to-Many (TaskList → Tasks) with cascade operations

## API Endpoints

All endpoints live under the `/api` context path. Every endpoint except `/api/auth/**` and the Swagger docs requires an `Authorization: Bearer <jwt>` header.

Interactive documentation is available at **`http://localhost:8080/api/swagger-ui.html`** — click **Authorize** and paste a JWT to try protected endpoints from the browser.

### Authentication (public)
```http
POST   /api/auth/register       # Create a new user, returns { token, username }
POST   /api/auth/login          # Exchange credentials for a JWT
```

### Task Lists (authenticated, owner-scoped)
```http
GET    /api/task-lists                  # Retrieve all task lists for the caller
GET    /api/task-lists/{id}             # Get specific task list with its tasks
POST   /api/task-lists                  # Create new task list
PUT    /api/task-lists/{id}             # Update task list details
DELETE /api/task-lists/{id}             # Delete task list (cascades to tasks)
```

### Tasks (authenticated, nested resource)
```http
GET    /api/task-lists/{task_list_id}/tasks         # Get all tasks in a list
GET    /api/task-lists/{task_list_id}/tasks/{id}    # Get specific task
POST   /api/task-lists/{task_list_id}/tasks         # Create new task
PUT    /api/task-lists/{task_list_id}/tasks/{id}    # Update task
DELETE /api/task-lists/{task_list_id}/tasks/{id}    # Delete task
```

### Example auth flow

**1. Register:**

```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "alice",
  "password": "correct-horse-battery-staple"
}
```

Response `200 OK`:
```json
{
  "token": "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhbGljZSJ9.signature",
  "username": "alice"
}
```

**2. Create a task list using the JWT:**

```http
POST /api/task-lists
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "title": "Sprint 12",
  "description": "Work items for the sprint ending 2026-05-02."
}
```

Response `200 OK`:
```json
{
  "id": "11111111-1111-1111-1111-111111111111",
  "title": "Sprint 12",
  "description": "Work items for the sprint ending 2026-05-02.",
  "count": 0,
  "progress": 0.0,
  "tasks": []
}
```

**3. Add a task to that list:**

```http
POST /api/task-lists/11111111-1111-1111-1111-111111111111/tasks
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
Content-Type: application/json

{
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication",
  "priority": "HIGH",
  "status": "OPEN"
}
```

### Error shape

All 4xx and 5xx responses use a uniform shape:

```json
{
  "status": 400,
  "message": "Validation failed",
  "details": "/api/task-lists",
  "errors": [
    { "field": "title", "message": "Title must not be blank" }
  ]
}
```

`errors` is only present on 400 responses from Bean Validation.

## Getting Started

### Pre-requisites
- Docker & Docker Compose (for the one-command quick start)
- *(Optional, for host-side dev)* Java 17+, Maven 3.8+, Node.js 20+, npm

### Quick start (recommended)

```bash
git clone https://github.com/gozzerks/taskflow.git
cd taskflow
docker compose up
```

That brings up PostgreSQL and the backend together. The backend waits for the DB's healthcheck before starting, so no race on first boot.

| Service | URL |
|---|---|
| Backend API | `http://localhost:8080/api` |
| Swagger UI | `http://localhost:8080/api/swagger-ui.html` |
| PostgreSQL | `localhost:5432` |

### Configuration

Defaults live in `application.properties` and `docker-compose.yml` and work out of the box for local dev. To override anything (recommended for anything non-dev), copy `.env.example` to `.env`:

```bash
cp .env.example .env
# edit .env -- at a minimum replace JWT_SECRET with a fresh ≥64-byte base64 key
```

### Testing the API

```bash
# Register a user and grab the token in one go:
TOKEN=$(curl -sX POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"alice","password":"correct-horse-battery-staple"}' \
  | jq -r .token)

# Create a task list using the token:
curl -X POST http://localhost:8080/api/task-lists \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"title":"My Tasks","description":"Personal task list"}'
```

Or just open **`http://localhost:8080/api/swagger-ui.html`** and click around.

### Host-side development (without the backend container)

If you'd rather run the Spring Boot backend on the host (hot reload, IDE debugger, etc.):

```bash
# Start only Postgres
docker compose up -d db

# Run the backend on the host
cd backend
mvn spring-boot:run
```

## Testing & Quality Assurance

**130 tests, all passing.** Split across unit, slice, and repository layers. Security is verified both at the filter level (via `JwtServiceTest`) and at the controller level (via `AuthControllerTest` and owner-scoping tests on `TaskListServiceImplTest`).

### Auth Tests

Security layer testing using **JUnit 5**, **Mockito**, and **@WebMvcTest**:

**JwtServiceTest.java** (3 tests):
- Signs a token with the HS512 secret and parses back the subject claim
- Rejects tokens signed with a different secret
- Rejects expired tokens

**AuthControllerTest.java** (6 tests):
- `POST /api/auth/register` creates a user and returns a bearer token
- Duplicate-username registration returns a meaningful 400
- `POST /api/auth/login` happy-path returns a token for valid credentials
- Wrong password returns 401 via `BadCredentialsException` handler
- Missing / malformed request bodies return 400 with the `ErrorResponse` shape
- Bean Validation failures surface per-field errors

**TaskList ownership tests** (3 tests in `TaskListServiceImplTest.java`):
- A user cannot read another user's task list (404, indistinguishable from missing)
- A user cannot update another user's task list
- A user cannot delete another user's task list

### Controller Tests

REST API testing using **MockMvc**, **Mockito**, and **AAA pattern** (Arrange-Act-Assert):

**TaskListControllerTest.java** (10 tests):
- List operations (all lists, empty list scenarios)
- Get single task list (success, not found)
- Create task list (valid data, service validation errors)
- Update task list (success, not found)
- Delete task list
- Error handling (malformed JSON)

**TaskControllerTest.java** (8 tests):
- List tasks within task list (populated and empty)
- Get single task (success, not found)
- Create task (valid data, service validation errors)
- Update task status and priority
- Delete task operations

### Repository Tests

Data layer testing using **@DataJpaTest** with **H2 in-memory database**:

**TaskRepositoryTest.java** (15 tests):
- Custom query methods (`findByTaskListId`, `findByTaskListIdAndId`)
- Delete operations (`deleteByTaskListIdAndId`)
- Entity relationship integrity
- Empty result handling
- Task-TaskList bidirectional relationship
- Cascade delete verification

### Service Tests

Business logic testing using **JUnit 5**, **Mockito**, and **AssertJ**:

**TaskListServiceImplTest.java** (18 tests):
- Find all operations (multiple lists, empty results)
- Get task list (existing ID, non-existent ID)
- Create task list (successful creation, ID conflict, title validation)
- Update task list (successful update, timestamp updates, not found, ID mismatch)
- Delete task list
- Ownership enforcement (cross-user read/update/delete all reject)

**TaskServiceImplTest.java** (24 tests):
- Task creation (valid task list, default priority, forced OPEN status, validation)
- Task retrieval (by list and task ID, empty results)
- Task updates (field updates, not found, ID validation, priority/status required)
- Task deletion

### Mapper Tests

Entity-DTO conversion testing using **JUnit 5** and **AssertJ**:

**TaskMapperImplTest.java** (19 tests):
- toDTO: Entity to DTO mapping (all fields, null handling, enums)
- fromDTO: DTO to Entity mapping (field validation, null safety)
- All priority and status combinations
- Field mapping accuracy

**TaskListMapperImplTest.java** (10 tests):
- toDTO: Entity to DTO with progress calculation
- fromDTO: DTO to Entity conversion
- Progress calculation accuracy (all open, all closed, mixed)
- Empty/null task list handling

### Exception Handler Tests

Global error handling testing using **MockMvc** and **@WebMvcTest**:

**GlobalExceptionHandlerTest.java** (16 tests):
- IllegalArgumentException handling (400 responses)
- Error response structure validation
- Custom/empty/null exception messages
- Special characters and emojis in messages
- Long exception message handling
- JSON content type verification
- Request details inclusion in errors
- Security (no stack trace exposure)

### Running the Tests
```bash
cd backend
mvn test                         # run the full suite (130 tests, ~5s)
mvn test -Dtest=AuthControllerTest         # single class
mvn test -Dtest=JwtServiceTest
mvn test -Dtest=TaskListServiceImplTest
```

Tests use an H2 in-memory database with `ddl-auto=create-drop` for isolation and speed. Flyway is disabled in the test profile so JPA annotations drive the test schema.

### Frontend Setup

```bash
cd frontend
npm install
npm run dev          # http://localhost:5173 (Vite dev server, proxies /api to :8080)
npm run lint
npm run build
```

## Continuous Integration

GitHub Actions runs on every push and PR to `master`. The workflow (`.github/workflows/ci.yml`) defines three parallel jobs:

| Job | What it runs |
|---|---|
| `backend` | `mvn verify` — compile + full test suite against H2 |
| `frontend` | `npm ci && npm run lint && npm run build` |
| `docker` | `docker buildx build ./backend` — verifies the backend image still produces cleanly |

All three run in parallel so a red check tells you exactly where to look.


## Technical Skills Demonstrated

### Backend
 **Spring Boot Application Development**
- Dependency injection and IoC container
- Spring MVC for REST controllers
- Spring Data JPA for persistence
- Externalised configuration via env-var placeholders

 **Security (Spring Security 6 + JWT)**
- Stateless security chain with a custom `JwtAuthFilter`
- JJWT 0.12.x for signing (HS512) and parsing
- BCrypt password hashing
- Per-user resource scoping enforced at the service layer
- Custom `AuthenticationEntryPoint` (401) and `AccessDeniedHandler` (403) that emit the project's `ErrorResponse` JSON shape

 **RESTful API Design**
- Proper HTTP methods and status codes
- Resource-based URLs
- Nested resource handling
- OpenAPI 3 / Swagger UI with `@Tag`, `@Operation`, `@ApiResponses`, `@Schema` annotations

 **Database Management**
- PostgreSQL 16 for production
- H2 for testing
- JPA entity relationships (User → TaskList → Task)
- Flyway versioned migrations (`V1`, `V2`) with `ddl-auto=validate`

 **Code Organization**
- Layered architecture (Controller → Service → Repository)
- Separation of concerns
- DTO pattern for API contracts

 **Error Handling & Validation**
- Global exception handling via `@ControllerAdvice`
- Uniform `ErrorResponse` shape across 4xx / 5xx / security errors
- Jakarta Bean Validation on every request DTO with field-level errors

 **Build, CI & Deployment**
- Maven project management
- Multi-stage Dockerfile (Maven build stage + slim JRE runtime)
- `docker compose` stack with healthcheck-gated startup (Postgres → backend)
- GitHub Actions CI with parallel backend / frontend / Docker-image jobs

### Frontend (Functional Interface)
- Basic React component structure
- TypeScript type definitions
- REST API integration with Axios
- Login / Register flow with JWT persistence via `localStorage` + Axios interceptor
- NextUI component usage

## Learning Resources Used

- Spring Boot Official Documentation
- Baeldung Spring Tutorials
- Youtube Tutorials [Devtiro, Amigoscode]
- Spring Data JPA Documentation

## Why This Project?

This project allowed me to:
1. **Strengthen Spring Boot fundamentals** - dependency injection, Spring MVC, Spring Data JPA
2. **Implement REST API best practices** - proper endpoint design, HTTP methods, status codes
3. **Work with relational databases** - entity relationships, transactions, Flyway migrations
4. **Apply design patterns** - Repository, Service Layer, DTO patterns
5. **Handle real-world scenarios** - error handling, validation, data mapping
6. **Secure a REST API properly** - stateless JWT, Spring Security 6, per-user resource scoping, BCrypt
7. **Document the API for consumers** - OpenAPI 3 / Swagger UI with annotated controllers and DTOs
8. **Ship production-ready tooling** - Docker multi-stage build, `docker compose` stack, GitHub Actions CI, externalised secrets