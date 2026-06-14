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

## Stage 6: Jenkins CI Pipeline

### Overview
Configured a Jenkins CI pipeline that automatically builds and deploys the application
whenever changes are merged to the `main` branch.

### Pipeline Stages
| Stage | Description |
|---|---|
| Checkout | Jenkins pulls the latest code from GitHub (`main` branch) |
| Build JAR | Compiles the Spring Boot app and packages it as a JAR using Maven Wrapper |
| Build Docker Image | Builds a Docker image from the JAR and tags it as `leavemgmt:1.0` |
| Deploy | Removes the old app container and starts a fresh one via Docker Compose |

### Files
- `Jenkinsfile` — declarative pipeline definition at the repo root
- `Dockerfile` — defines the Docker image build instructions
- `docker-compose.yml` — defines all three services (postgres, app, jenkins)

### How It Works
1. Developer creates a feature branch and makes changes
2. A Pull Request is opened and merged into `main`
3. Jenkins detects the change and triggers the pipeline automatically
4. Maven builds the JAR (`leavemgmt-0.0.1-SNAPSHOT.jar`)
5. Docker builds a new image using the JAR
6. Docker Compose stops the old app container and starts a fresh one with the new image
7. The updated application is live on port `8082`

### Key Decisions
- **`chmod +x mvnw` in Jenkinsfile** — ensures Maven Wrapper is executable after
  Jenkins clones the repo, since file permissions are not preserved by Git by default
- **`docker rm -f leavemgmt-app || true`** — safely removes any existing container
  before deploying, including containers not managed by Compose; `|| true` prevents
  pipeline failure if no container exists
- **`docker compose -p leavemgmt`** — explicitly sets the Compose project name so the
  network name is always `leavemgmt_leavemgmt-network` regardless of which directory
  Jenkins runs from; without this, Jenkins prefixes its workspace name to the network,
  isolating the app container from the postgres container
- **Separate build and deploy stages** — keeps concerns separated and provides a clear
  integration point for future enhancements like image scanning or pushing to a registry

### Issues Encountered & Fixes
| Issue | Cause | Fix |
|---|---|---|
| `mvnw` exit code 126 | Maven Wrapper lacked executable permission after git clone | Added `chmod +x mvnw` in Jenkinsfile; set permission permanently via `git update-index --chmod=+x mvnw` |
| Container name conflict on deploy | Old `leavemgmt-app` container not managed by Compose couldn't be recreated | Added `docker rm -f leavemgmt-app \|\| true` before `docker compose up` |
| `UnknownHostException: postgres` | Jenkins Compose ran from its workspace directory, creating a different network (`leavemgmt-pipeline_leavemgmt-network`) than the one postgres was on | Added `-p leavemgmt` to all `docker compose` commands to enforce a consistent project name and network |

### Running the Pipeline Manually
1. Open Jenkins at `http://localhost:8080`
2. Navigate to `leavemgmt-pipeline`
3. Click **Build Now**

### Verifying the Deployment
```bash
# Check the app container is running
docker ps | grep leavemgmt-app

# Hit the API
curl http://localhost:8082/api/employees
```

### Final Jenkinsfile
```groovy
pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Cloning repository...'
                checkout scm
            }
        }

        stage('Build JAR') {
            steps {
                echo 'Building JAR artifact...'
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Build Docker Image') {
            steps {
                echo 'Building Docker image...'
                sh 'docker build -t leavemgmt:1.0 .'
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying application...'
                sh 'docker rm -f leavemgmt-app || true'
                sh 'docker compose -p leavemgmt up -d --no-deps app'
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
```

## Stage 7: Deploy to AWS EC2

### Overview
Manually deployed the application to a cloud server on AWS EC2, making it accessible
over the internet. This stage simulates a real-world deployment to a remote server
and introduces multi-stage Docker builds to eliminate build tool dependencies on the
server.

### Infrastructure
| Component | Details |
|---|---|
| Cloud Provider | AWS |
| Instance Type | t2.micro (Free Tier) |
| OS | Ubuntu Server 22.04 LTS |
| Region | us-east-1 |
| Ports opened | 22 (SSH), 8082 (app) |

### What Changed
- Upgraded `Dockerfile` to a multi-stage build — the JAR is now built inside Docker,
  so the server needs nothing except Docker installed
- Deployed only the `postgres` and `app` services on EC2 (jenkins not needed on server)

### Multi-Stage Dockerfile
```dockerfile
# Stage 1 — Build
FROM eclipse-temurin:21-jdk-alpine AS builder
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN chmod +x mvnw && ./mvnw clean package -DskipTests

# Stage 2 — Run
FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
COPY --from=builder /app/target/leavemgmt-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]
```

**Why multi-stage:**
| | Single-stage | Multi-stage |
|---|---|---|
| Build tools needed on server | ✅ Java + Maven | ❌ Nothing |
| Final image contains build tools | ✅ Yes (bloated) | ❌ No (lean) |
| Builds anywhere with just Docker | ❌ No | ✅ Yes |

### How It Works

Developer pushes to GitHub

↓

SSH into EC2

↓

git pull origin main

↓

docker build -t leavemgmt:1.0 .

├── Stage 1: JDK image pulls, Maven builds JAR inside Docker

└── Stage 2: JAR copied into lean JRE image

↓

docker compose up -d postgres app

├── postgres container starts, healthcheck passes

└── app container starts, connects to postgres

↓

App live at http://<EC2-PUBLIC-IP>:8082

### EC2 Setup Steps
1. Launch Ubuntu 22.04 t2.micro instance
2. Create key pair (`leavemgmt-key.pem`) and download it
3. Configure Security Group — open ports 22 and 8082
4. Set key permissions and SSH in:
```bash
cp /path/to/leavemgmt-key.pem ~/.ssh/
chmod 400 ~/.ssh/leavemgmt-key.pem
ssh -i ~/.ssh/leavemgmt-key.pem ubuntu@<EC2-PUBLIC-IP>
```
5. Install Docker:
```bash
curl -fsSL https://get.docker.com -o get-docker.sh
sudo sh get-docker.sh
sudo usermod -aG docker ubuntu
newgrp docker
```
6. Clone repo and deploy:
```bash
git clone https://github.com/krsaeed/leave-mgmt.git
cd leave-mgmt
docker build -t leavemgmt:1.0 .
docker compose up -d postgres app
```

### Verifying the Deployment
```bash
# From your local machine
curl http://<EC2-PUBLIC-IP>:8082/api/employees

# On EC2 — check containers are running
docker ps

# On EC2 — check app logs
docker logs leavemgmt-app
```

### Key Learnings
- **chmod 400 on .pem file** — SSH refuses to use a private key that is readable
  by others; must be restricted to owner only
- **Copy .pem to WSL filesystem** — `chmod` does not work correctly on
  Windows-mounted drives (`/mnt/d/`); key must be copied to WSL's own filesystem
  (`~/.ssh/`) first
- **Multi-stage build** — EC2 only needs Docker; no Java or Maven installation
  required; the builder stage handles compilation entirely inside Docker
- **Selective Compose services** — `docker compose up -d postgres app` starts only
  the needed services; jenkins is omitted on the server

### What's Manual vs Automated
| Task | Stage 7 (now) | Future improvement |
|---|---|---|
| SSH into server | Manual | Jenkins does it automatically |
| git pull | Manual | Jenkins triggers on merge |
| docker build | Manual | Jenkins pipeline stage |
| docker compose up | Manual | Jenkins pipeline stage |

This manual process will be fully automated when Jenkins is extended to deploy
to EC2 via SSH in a future stage.

## Stage 8: SonarQube Code Quality

So the real constraint is always RAM, not disk.
This is why:

t2.micro (1GB RAM) → too small for either
t3.medium (4GB RAM) → comfortable for one of them
Your local WSL at 3.8GB → tight, especially with Jenkins + postgres + app already running

Practical conclusion for your project:
Both Stage 8 and 9 are best done on a persistent AWS Free Tier account where you can keep a t3.medium running without it resetting. The sandbox just isn't the right environment for them.