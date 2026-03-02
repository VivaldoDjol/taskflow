# TaskFlow - Task Management Application with Spring Boot Backend

A task management application showcasing backend development expertise with Spring Boot, built as part of my continuous learning journey.

## Project Context

This project demonstrates my **backend development skills** with Spring Boot, Java, and RESTful API design. The backend was developed by following best practices tutorials and implementing core features independently. The frontend serves as a functional interface to demonstrate the API capabilities, though my primary expertise lies in backend development.

**Current Focus**: Backend architecture, API design, database modeling, and server-side logic  
**Learning Goals**: Understanding client-side integration to design more robust and developer-friendly backend APIs

## Project Overview

TaskFlow is a task management application that enables users to organize their work through task lists and individual tasks. The application features a Spring Boot backend with PostgreSQL persistence and a React frontend interface.


## Key Features (Backend Implementation)

### Core Backend Features
- **RESTful API Design**: Fully functional REST endpoints following industry standards
- **Task List Management**: Complete CRUD operations with automatic progress calculation
- **Task Operations**: Nested resource management within task lists
- **Priority & Status System**: Enum-based task organization (LOW, MEDIUM, HIGH priority)
- **Database Relationships**: Proper JPA entity relationships and cascade operations
- **Exception Handling**: Global exception handler with meaningful error responses
- **Progress Tracking**: Service-layer logic for calculating task completion percentages

### Frontend Features
- Basic CRUD interface for task and task list management
- Progress visualization with NextUI components
- Responsive design with Tailwind CSS

## Technology Stack

### Backend (Primary Focus)
- **Framework**: Spring Boot 3.5.6
- **Language**: Java 17
- **Database**: PostgreSQL (production), H2 (testing)
- **ORM**: Spring Data JPA / Hibernate
- **Build Tool**: Maven
- **Containerization**: Docker & Docker Compose
- **Testing**: JUnit, Spring Boot Test

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
├── TaskflowApplication.java    # Spring Boot main application
├── controllers/                # REST API endpoints
│   ├── TaskController.java
│   ├── TaskListController.java
│   └── GlobalExceptionHandler.java
├── services/                   # Business logic layer
│   ├── TaskService.java        # Service interface
│   ├── TaskListService.java    # Service interface
│   └── impl/
│       ├── TaskServiceImpl.java      # Implementation of task business logic
│       └── TaskListServiceImpl.java  # Implementation of task list business logic
├── repositories/               # Data access layer (Spring Data JPA)
│   ├── TaskRepository.java
│   └── TaskListRepository.java
├── domain/
│   ├── entities/              # JPA entities
│   │   ├── Task.java
│   │   ├── TaskList.java
│   │   ├── TaskStatus.java    # Enum: OPEN, CLOSED
│   │   └── TaskPriority.java  # Enum: LOW, MEDIUM, HIGH
│   └── dto/                   # Data Transfer Objects
│       ├── TaskDTO.java
│       ├── TaskListDTO.java
│       └── ErrorResponse.java
└── mappers/                   # Entity-DTO conversion
├── TaskMapper.java        # Mapper interface
├── TaskListMapper.java    # Mapper interface
└── impl/
├── TaskMapperImpl.java
└── TaskListMapperImpl.java

# Test Directory
backend/src/test/java/com/gozzerks/taskflow/
├── controllers/                    # REST API integration tests
│   ├── TaskControllerTest.java
│   ├── TaskListControllerTest.java
│   └── GlobalExceptionHandlerTest.java
├── domain/
│   └── dto/                        # DTO validation tests
│       ├── TaskDtoTest.java
│       ├── TaskListDtoTest.java
│       └── CreateTaskDtoTest.java
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
private Long calculateProgress(TaskList taskList) {
    List<Task> tasks = taskList.getTasks();
    if (tasks == null || tasks.isEmpty()) {
        return 0L;
    }
    long completedTasks = tasks.stream()
        .filter(task -> task.getStatus() == TaskStatus.COMPLETED)
        .count();
    return (completedTasks * 100) / tasks.size();
}
```

## Database Schema & Relationships

### TaskList Entity
```java
@Entity
@Table(name = "task_list")
public class TaskList {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String name;
    private String description;
    
    @OneToMany(mappedBy = "taskList", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Task> tasks = new ArrayList<>();
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

### Task Entity
```java
@Entity
@Table(name = "task")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    private String title;
    private String description;
    
    @Enumerated(EnumType.STRING)
    private TaskPriority priority;
    
    @Enumerated(EnumType.STRING)
    private TaskStatus status;
    
    @ManyToOne
    @JoinColumn(name = "task_list_id", nullable = false)
    private TaskList taskList;
    
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
```

**Relationship**: One-to-Many (TaskList → Tasks) with cascade operations

## API Endpoints 

### Task Lists
```http
GET    /task-lists              # Retrieve all task lists with progress
GET    /task-lists/{id}         # Get specific task list with tasks
POST   /task-lists              # Create new task list
PUT    /task-lists/{id}         # Update task list details
DELETE /task-lists/{id}         # Delete task list (cascades to tasks)
```

### Tasks (Nested Resource)
```http
GET    /task-lists/{task_list_id}/tasks          # Get all tasks in a list
GET    /task-lists/{task_list_id}/tasks/{id}     # Get specific task
POST   /task-lists/{task_list_id}/tasks          # Create new task
PUT    /task-lists/{task_list_id}/tasks/{id}     # Update task
DELETE /task-lists/{task_list_id}/tasks/{id}     # Delete task
```

### Example Request/Response

**Create Task List:**

Request:
```
POST /task-lists
Content-Type: application/json
```

Request Body:
```json
{
  "tittle": "Sprint 1 Tasks",
  "description": "Tasks for the first sprint"
}
```

Response (201 Created):
```json
{
  "id": 1,
  "title": "Sprint 1 Tasks",
  "description": "Tasks for the first sprint",
  "progress": 0,
  "tasks": [],
  "createdAt": "2024-01-15T10:30:00",
  "updatedAt": "2024-01-15T10:30:00"
}
```

**Create Task:**

Request:
```
POST /task-lists/1/tasks
Content-Type: application/json
```

Request Body:
```json
{
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication",
  "priority": "HIGH",
  "status": "OPEN"
}
```

Response (201 Created):
```json
{
  "id": 1,
  "title": "Implement user authentication",
  "description": "Add JWT-based authentication",
  "priority": "HIGH",
  "status": "OPEN",
  "taskListId": 1,
  "createdAt": "2024-01-15T10:35:00",
  "updatedAt": "2024-01-15T10:35:00"
}
```

## Getting Started

### Pre-requisites
- Java 17 or higher
- Maven 3.8+
- Docker & Docker Compose (for PostgreSQL)
- Node.js 18+ and npm (for frontend)

### Backend Setup

1. **Clone the repository**
```bash
git clone https://github.com/gozzerks/taskflow.git
cd taskflow
```

2. **Start PostgreSQL with Docker Compose**
```bash
docker-compose up -d
```

This starts PostgreSQL on `localhost:5432` with:
- Database: `taskflow`
- Username: `taskflow`
- Password: `taskflow123`

3. **Build and run the backend**
```bash
cd backend
mvn clean install
mvn spring-boot:run
```

Backend API will be available at `http://localhost:8080`

### Testing the API

You can test the API using curl, Postman, or any HTTP client:

```bash
# Get all task lists
curl http://localhost:8080/api/task-lists

# Create a new task list
curl -X POST http://localhost:8080/api/task-lists \
  -H "Content-Type: application/json" \
  -d '{"title":"My Tasks","description":"Personal task list"}'
```

## Testing & Quality Assurance

### Controller Tests

REST API testing using **MockMvc**, **Mockito**, and **AAA pattern** (Arrange-Act-Assert):

**TaskListControllerTest.java** (15+ tests):
- List operations (all lists, empty list scenarios)
- Get single task list (success and 404 cases)
- Create task list with validation (title required, blank detection, max length)
- Update task list (success, validation errors, 404 handling)
- Delete task list (success, 404 handling, cascade delete verification)
- Error handling (malformed JSON, invalid UUID format)

**TaskControllerTest.java** (10+ tests):
- List tasks within task list (populated and empty)
- Get single task (success and 404 cases)
- Create task with nested routing validation
- Update task status and priority
- Delete task operations
- Task list relationship validation

### Repository Tests

Data layer testing using **@DataJpaTest** with **H2 in-memory database**:

**TaskRepositoryTest.java** (15+ tests):
- Custom query methods (`findByTaskListId`, `findByTaskListIdAndId`)
- Cascade delete operations
- Entity relationship integrity
- Empty result handling
- Task-TaskList bidirectional relationship
- Transactional behavior verification

### Service Tests

Business logic testing using **JUnit 5**, **Mockito**, and **AssertJ**:

**TaskListServiceImplTest.java** (20+ tests):
- Find all operations (multiple lists, empty results)
- Get task list (existing ID, non-existent ID, null handling)
- Create task list (successful creation, timestamp setting, validation)
- Update task list (successful update, timestamp updates, non-existent ID)
- Delete task list (successful deletion, non-existent ID, cascade verification)

**TaskServiceImplTest.java** (20+ tests):
- Task creation (with valid list, relationship setup, validation)
- Task retrieval (by list ID, by ID, empty results)
- Task updates (field updates, relationship preservation, validation)
- Task deletion (successful, non-existent, exception handling)

### Mapper Tests

Entity-DTO conversion testing using **JUnit 5** and **AssertJ**:

**TaskMapperImplTest.java** (30+ tests):
- toDTO: Entity to DTO mapping (all fields, null handling, relationships)
- fromDTO: DTO to Entity mapping (field validation, null safety)
- Null entity/DTO handling
- Field mapping accuracy

**TaskListMapperImplTest.java** (20+ tests):
- toDTO: Entity to DTO with progress calculation
- fromDTO: DTO to Entity conversion
- Progress calculation accuracy
- Empty/null task list handling

### DTO Validation Tests

Bean validation testing using **Jakarta Validator** and **AssertJ**:

**TaskDtoTest.java** (20+ tests):
- Title validation (null, blank, max length, special characters)
- Description validation (optional, max length)
- Priority/Status enum validation
- Due date validation (null allowed, past/future dates)
- TaskList ID validation
- Multiple validation error handling

**TaskListDtoTest.java** (10+ tests):
- Title validation (required, max length, whitespace)
- Description validation (optional, max length)
- Progress validation (range, negative values)
- Task count validation
- Complete DTO validation

**CreateTaskDtoTest.java** (20+ tests):
- Required field validation (title, priority, status)
- Optional field validation (description, dueDate)
- TaskList ID requirement
- Field length constraints
- Special character handling

### Exception Handler Tests

Global error handling testing using **MockMvc** and **@WebMvcTest**:

**GlobalExceptionHandlerTest.java** (15+ tests):
- IllegalArgumentException handling (400 responses)
- Error response structure validation
- Custom/empty/null exception messages
- Special characters and emojis in messages
- Long exception message handling
- JSON content type verification
- Request path inclusion in errors
- Security (no stack trace exposure)

### Test Coverage
```bash
# Run all controller tests
cd backend
mvn test

# Run specific test class
mvn test -Dtest=TaskControllerTest
mvn test -Dtest=TaskListControllerTest
mvn test -Dtest=TaskRepositoryTest
mvn test -Dtest=TaskListServiceImplTest
mvn test -Dtest=TaskServiceImplTest
```

### Frontend Setup

1. **Navigate to frontend directory**
```bash
cd frontend
```

2. **Install dependencies**
```bash
npm install
```

3. **Start development server**
```bash
npm run dev
```

Frontend will start on `http://localhost:5173`

### Running Backend Tests

```bash
cd backend
mvn test
```

Tests use H2 in-memory database for isolation and speed.


## Technical Skills Demonstrated

### Backend
 **Spring Boot Application Development**
- Dependency injection and IoC container
- Spring MVC for REST controllers
- Spring Data JPA for persistence

 **RESTful API Design**
- Proper HTTP methods and status codes
- Resource-based URLs
- Nested resource handling

 **Database Management**
- PostgreSQL for production
- H2 for testing
- JPA entity relationships
- Database migrations with Hibernate

 **Code Organization**
- Layered architecture (Controller → Service → Repository)
- Separation of concerns
- DTO pattern for API contracts

 **Error Handling**
- Global exception handling
- Custom error responses
- Input validation

 **Build & Deployment**
- Maven project management
- Docker containerization
- Environment configuration

### Frontend (Functional Interface)
- Basic React component structure
- TypeScript type definitions
- REST API integration with Axios
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
3. **Work with relational databases** - entity relationships, transactions, query optimization
4. **Apply design patterns** - Repository, Service Layer, DTO patterns
5. **Handle real-world scenarios** - error handling, validation, data mapping