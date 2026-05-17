```markdown
# Employee Leave Management System

A REST API built with Spring Boot and PostgreSQL to manage employee leave requests.
Employees can apply for leave, and managers can approve or reject requests.

---

## Tech Stack

| Technology | Purpose |
|---|---|
| Java 21 | Programming language |
| Spring Boot 3.5 | Web framework |
| Spring Data JPA | Database ORM (Java to SQL) |
| Hibernate | JPA implementation |
| PostgreSQL | Relational database |
| Maven | Build tool |
| Docker | Run PostgreSQL locally |

---

## Project Structure

```
src/
└── main/
    ├── java/com/devops/leavemgmt/
    │   ├── model/                        # Data layer - maps Java classes to DB tables
    │   │   ├── Employee.java             # Employee entity (maps to 'employees' table)
    │   │   ├── LeaveRequest.java         # Leave request entity (maps to 'leave_requests' table)
    │   │   ├── Role.java                 # Enum: EMPLOYEE | MANAGER
    │   │   ├── LeaveType.java            # Enum: ANNUAL | SICK | UNPAID
    │   │   └── LeaveStatus.java          # Enum: PENDING | APPROVED | REJECTED
    │   │
    │   ├── repository/                   # Database access layer (no SQL needed)
    │   │   ├── EmployeeRepository.java   # CRUD operations for Employee
    │   │   └── LeaveRequestRepository.java # CRUD + custom queries for LeaveRequest
    │   │
    │   ├── service/                      # Business logic layer
    │   │   └── LeaveService.java         # Leave submission and approval rules
    │   │
    │   ├── controller/                   # REST API layer - handles HTTP requests
    │   │   ├── EmployeeController.java   # Endpoints: /api/employees
    │   │   └── LeaveController.java      # Endpoints: /api/leaves
    │   │
    │   └── LeavemgmtApplication.java     # Spring Boot entry point (main method)
    │
    └── resources/
        └── application.properties        # App config (DB connection, port, JPA settings)
```

---

## How the Layers Work Together

```
HTTP Request
    ↓
Controller  →  receives the request, validates input
    ↓
Service     →  applies business rules (balance check, status validation)
    ↓
Repository  →  talks to the database (JPA generates SQL automatically)
    ↓
PostgreSQL  →  stores data permanently
```

---

## REST API Endpoints

### Employees

| Method | URL | Description |
|---|---|---|
| POST | `/api/employees` | Create a new employee |
| GET | `/api/employees` | Get all employees |
| GET | `/api/employees/{id}` | Get employee by ID |
| GET | `/api/employees/{id}/balance` | Check leave balance |

### Leave Requests

| Method | URL | Description |
|---|---|---|
| POST | `/api/leaves/employee/{id}` | Submit a leave request |
| GET | `/api/leaves` | Get all leave requests |
| GET | `/api/leaves/pending` | Get all pending requests |
| GET | `/api/leaves/employee/{id}` | Get leaves for an employee |
| PUT | `/api/leaves/{id}/review` | Approve or reject a request |

---

## How to Run Locally

### Prerequisites
- Docker installed
- Java 21 installed
- Maven (or use `./mvnw`)

### Step 1 — Start PostgreSQL
```bash
docker start postgres-local
```

### Step 2 — Run the app
```bash
./mvnw spring-boot:run
```

### Step 3 — Test the API
```bash
# Create a manager
curl -X POST http://localhost:8082/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Alice Smith","email":"alice@company.com","department":"Engineering","role":"MANAGER"}'

# Create an employee
curl -X POST http://localhost:8082/api/employees \
  -H "Content-Type: application/json" \
  -d '{"name":"Bob Jones","email":"bob@company.com","department":"Engineering","role":"EMPLOYEE"}'

# Submit leave request
curl -X POST http://localhost:8082/api/leaves/employee/2 \
  -H "Content-Type: application/json" \
  -d '{"startDate":"2025-06-01","endDate":"2025-06-05","leaveType":"ANNUAL","reason":"Family vacation"}'

# Approve leave
curl -X PUT "http://localhost:8082/api/leaves/1/review?managerId=1&decision=APPROVED"

# Check balance
curl http://localhost:8082/api/employees/2/balance
```

---

## Key Files

| File | Purpose |
|---|---|
| `pom.xml` | Maven dependencies and build config |
| `application.properties` | DB connection, port, JPA settings |
| `.env` | Secret credentials (never committed to Git) |
| `.gitignore` | Files excluded from Git (target/, .env, *.jar) |
| `mvnw` | Maven wrapper — run Maven without installing it |

---

## DevOps Roadmap

This project is built progressively through 9 DevOps stages:

| Stage | Topic |
|---|---|
| 1 | Run app locally (Spring Boot + PostgreSQL) |
| 2 | Git + GitHub |
| 3 | Maven build + JAR |
| 4 | Docker + multi-stage build |
| 5 | Docker Compose (app + DB together) |
| 6 | Jenkins CI pipeline |
| 7 | Deploy to AWS EC2 |
| 8 | SonarQube code quality |
| 9 | Nexus artifact registry |
```

Save it as `README.md` in the project root. Once you push to GitHub it will render beautifully as the homepage of your repo!

Ready for Stage 2 — Git + GitHub?
Create repo
clone 
create main(default) and feature branch
push changes in feature branch
Raise PR from feature to main
Merge PR in main

Stage 3 - Maven build + JAR
## How to Build

### Build the JAR artifact
```bash
./mvnw clean package -DskipTests
```

### Run the JAR directly (no Maven needed)
```bash
java -jar target/leavemgmt-0.0.1-SNAPSHOT.jar
```

### Difference
| Command | Used for |
|---|---|
| `./mvnw spring-boot:run` | Development only | Need Maven installed and entire source code required
| `java -jar target/app.jar` | Production/deployment | Need only Java installed and only JAR artifact file is enough
