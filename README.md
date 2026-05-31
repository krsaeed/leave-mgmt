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
curl -X POST http://localhost:8082/api/employees -H "Content-Type: application/json" -d '{"name":"Alice Smith","email":"alice@company.com","department":"Engineering","role":"MANAGER"}'

# Create an employee
curl -X POST http://localhost:8082/api/employees -H "Content-Type: application/json" -d '{"name":"Bob Jones","email":"bob@company.com","department":"Engineering","role":"EMPLOYEE"}'

# Submit leave request
curl -X POST http://localhost:8082/api/leaves/employee/2 -H "Content-Type: application/json" -d '{"startDate":"2025-06-01","endDate":"2025-06-05","leaveType":"ANNUAL","reason":"Family vacation"}'

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
| 4 | Docker |
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
| Command | Used for | Example |
|---|---|---|
| `./mvnw spring-boot:run` | Development only | Need Maven installed and entire source code required |
| `java -jar target/app.jar` | Production/deployment | Need only Java installed and only JAR artifact file is enough |

## Stage 4: Docker — Containerizing the Application

### What was done
- Written a Dockerfile to package the Spring Boot JAR into a Docker image
- Built the Docker image locally
- Run the application as a Docker container
- Connected app container to PostgreSQL container via a shared Docker network

### Dockerfile
```dockerfile
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY target/leavemgmt-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### Key concepts learned

**JAR vs Docker Image:**
| | JAR | Docker Image |
|---|---|---|
| Contains | Code + dependencies | JAR + Java + OS |
| Needs Java installed? | ✅ Yes | ❌ No |
| Runs anywhere? | Only where Java exists | Anywhere Docker exists |

**Dockerfile instructions:**
| Instruction | Purpose |
|---|---|
| `FROM` | Base image to start from (Java 21 JRE) |
| `WORKDIR` | Set working directory inside container |
| `COPY` | Copy JAR from machine into container |
| `ENTRYPOINT` | Command to run when container starts |

**Why JRE not JDK in production:**
- JDK = full kit to develop and build Java (not needed on server)
- JRE = just enough to run Java (smaller, more secure)

### Commands

**Build image:**
```bash
docker build -t leavemgmt:1.0 .
```

**Create network (so app and DB can talk by name):**
```bash
docker network create leavemgmt-network
```

**Start PostgreSQL on network:**
```bash
docker run -d \
  --name postgres-local \
  --network leavemgmt-network \
  -e POSTGRES_DB=leavemgmt \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=yourpassword \
  -p 5432:5432 \
  postgres:16-alpine
```

**Run app container:**
```bash
docker run -p 8082:8082 \
  --network leavemgmt-network \
  -e DB_URL=jdbc:postgresql://postgres-local:5432/leavemgmt \
  -e DB_USER=postgres \
  -e DB_PASS=yourpassword \
  leavemgmt:1.0
```

### Why Docker network matters
Without a shared network, containers are isolated and can't find
each other by name. By placing both containers on `leavemgmt-network`,
the app can reach PostgreSQL using the container name `postgres-local`
instead of an IP address which changes on every restart.

### Problem this stage revealed
Running multiple containers manually is painful:
- Create network manually
- Start each container separately
- Pass connection strings manually
- No guaranteed startup order

**This is exactly what Docker Compose solves in Stage 5!**

### WSL + Docker Desktop setup
When using Docker on WSL, images persist only when WSL is connected
to Docker Desktop engine:
```bash
# One time setup
docker context use default   # use Docker Desktop engine from WSL
# Enable WSL Integration in Docker Desktop Settings → Resources → WSL Integration
```

### DevOps pipeline position
```
Code → Maven builds JAR → Docker builds Image → Container runs on server
                                ↑
                          Stage 4 lives here
```

## Stage 5: Docker Compose — Multi-Container Orchestration

### What was done
- Written a docker-compose.yml to manage both app and PostgreSQL containers
- Replaced manual docker run commands with single command startup
- Added healthcheck so app waits for PostgreSQL to be ready
- Added named volume so database data persists across restarts

### docker-compose.yml structure
```yaml
services:
  postgres:                          # DB container
    image: postgres:16-alpine
    healthcheck:                     # is postgres ready?
      test: ["CMD-SHELL", "pg_isready -U postgres"]
    volumes:
      - postgres-data:/var/lib/postgresql/data  # persist data!

  app:                               # app container
    image: leavemgmt:1.0
    depends_on:
      postgres:
        condition: service_healthy   # wait for postgres first!

volumes:
  postgres-data:                     # named volume

networks:
  leavemgmt-network:                 # shared network
```

### Key concepts learned

**Why Docker Compose over manual commands:**
| Manual docker run | Docker Compose |
|---|---|
| 5 separate commands | `docker compose up` |
| Manual network creation | Auto created |
| No startup order control | `depends_on` + healthcheck |
| Data lost on container delete | Named volume persists |
| IP addresses for hostnames | Service names work |
| Easy to forget steps | Everything in one file |

**Attached vs Detached mode:**
| Command | Terminal | Use for |
|---|---|---|
| `docker compose up` | Locked, shows logs | Debugging, learning |
| `docker compose up -d` | Free, runs in background | Normal usage |

**Named Volume — why it matters:**

# Step 1 — create network
docker network create leavemgmt-network

# Step 2 — start postgres
docker run -d \
  --name postgres-local \
  --network leavemgmt-network \
  -e POSTGRES_DB=leavemgmt \
  -e POSTGRES_USER=postgres \
  -e POSTGRES_PASSWORD=yourpassword \
  -p 5432:5432 \
  postgres:16-alpine

# Step 3 — wait and hope postgres is ready (manual guessing!)

# Step 4 — run app
docker run -p 8082:8082 \
  --network leavemgmt-network \
  -e DB_URL=jdbc:postgresql://postgres-local:5432/leavemgmt \
  -e DB_USER=postgres \
  -e DB_PASS=yourpassword \
  leavemgmt:1.0

# Step 5 — if app crashed because postgres wasn't ready, repeat step 4!

These 5 steps were replaced by 1 docker compose command.