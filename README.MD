# GymCRM - Gym Customer Relationship Management System

## Table of Contents

- [Overview](#overview)
- [Architecture](#architecture)
- [Technologies](#technologies)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Authentication](#authentication)
- [Database Schema](#database-schema)
- [Docker Setup](#docker-setup)
- [Swagger Documentation](#swagger-documentation)
- [Testing](#testing)

---

## Overview

GymCRM is a comprehensive gym management system built with a microservices architecture using Spring Boot. The system manages trainees, trainers, training sessions, and provides statistical analysis of training activities. It features robust authentication using JWT tokens, service discovery with Eureka, and API gateway routing.

**Key Features:**
- User authentication and management (Trainees & Trainers)
- Training session scheduling and management
- Training statistics and analytics
- RESTful API with comprehensive documentation
- Microservices architecture for scalability
- Docker containerization for easy deployment

---

## Architecture

GymCRM follows a **microservices architecture** pattern with the following components:

```
┌────────────────────────────────────────────────────────────┐
│                    Client Applications                     │
└────────────────────────────┬───────────────────────────────┘
                             │
           ┌─────────────────┴────────────────┐
           │                                  │
    ┌──────▼────────┐             ┌───────────▼────────┐
    │  API Gateway  │             │  Direct Access     │
    │  (Port 8080)  │             │  (for debugging)   │
    └──────┬────────┘             └────────────────────┘
           │
    ┌──────▼─────────────────────────────────────┐
    │     Service Discovery (Eureka)             │
    │     Registry Server (Port 8761)            │
    └────────────────────┬───────────────────────┘
                         │
         ┌───────────────┴───────────────────────────┐
         │                                           │
┌────────▼────────┐     ┌──────────┐      ┌──────────▼─────────┐
│   Main App      │     │ RabbitMQ │      │   Statistics Ap    │
│   (Port 8081)   │     │ (5672)   │      │   (Port 8082)      │
│   - Auth        │◄───►│          │◄────►│ - Statistics       │
│   - Trainees    │     │          │      │ - Analytics        │
│   - Trainers    │     │          │      │                    │
│   - Trainings   │     │          │      │                    │
└───┬─────────────┘     └──────────┘      └────────┬───────────┘
    │                                              │
    │       ┌───────────────────┬──────────────────┘
    │       │                   │
┌───▼───────▼───┐     ┌─────────▼────────┐
│  PostgreSQL   │     │    MongoDB       │
│  (Port 5432)  │     │  (Port 27017)    │
│   gym-db      │     │   gym_db         │
└───────────────┘     └──────────────────┘
```

### Microservices:

1. **API Gateway** - Entry point for all client requests
   - Routing to appropriate microservices
   - Request/response filtering
   - Load balancing

2. **Main App** - Core business logic
   - User management (Trainees & Trainers)
   - Training session management
   - Authentication services

3. **Statistics App** - Training analytics
   - Trainer statistics calculation
   - Time-based aggregation
   - Asynchronous processing

4. **Registry Server (Eureka)** - Service discovery
   - Service registration and discovery
   - Health monitoring

### Infrastructure Services:

- **PostgreSQL** - Relational database for main application data
- **MongoDB** - Document database for statistics
- **RabbitMQ** - Message broker for asynchronous communication

---

## Technologies

### Core Framework
- **Spring Boot** 4.0.6
- **Spring Cloud** 2025.1.1
- **Java** 21

### Spring Stack
- **Spring Security** - Authentication and authorization
- **Spring Data JPA** - ORM for PostgreSQL
- **Spring Data MongoDB** - MongoDB integration
- **Spring Cloud Gateway** - API gateway with routing
- **Spring Cloud Netflix Eureka** - Service discovery
- **Spring Boot AMQP** - RabbitMQ integration

### Authentication & Security
- **JJWT** (Java JWT) 0.13.0 - JWT token generation and validation
- **BCrypt** - Password encryption
- **Spring Security** - Authorization and access control

### API Documentation
- **SpringDoc OpenAPI** 3.0.2 - OpenAPI 3.0 specification
- **Swagger UI** - Interactive API documentation

### Database
- **PostgreSQL** 18 - Relational database
- **MongoDB** - NoSQL document database
- **Flyway** - Database migration (if configured)

### Additional Libraries
- **Lombok** - Code generation boilerplate reduction
- **MapStruct** 1.6.3 - DTO mapping
- **Resilience4j** - Circuit breaker pattern
- **Micrometer Prometheus** - Metrics and monitoring
- **Spring Boot Actuator** - Application monitoring

### Testing
- **JUnit 5** - Unit testing
- **Cucumber** - BDD testing
- **Spring Boot Test** - Integration testing
- **H2** - In-memory database for testing

### Build Tools
- **Maven** - Dependency management and build tool
- **Spring Boot Maven Plugin** - Build executable JAR files

---

## Getting Started

### Prerequisites

- Java 21 or higher
- Maven 3.6.0 or higher
- Docker and Docker Compose (for containerized deployment)
- PostgreSQL (optional, if running locally without Docker)
- MongoDB (optional, if running locally without Docker)
- RabbitMQ (optional, if running locally without Docker)

### Local Installation

#### 1. Clone the Repository

```bash
git clone <repository-url>
cd GymCRM
```

#### 2. Build the Project

```bash
mvn clean install
```

#### 3. Run PostgreSQL and MongoDB

Use Docker or install locally:

```bash
docker run -d --name postgres_gym \
  -e POSTGRES_USER=admin \
  -e POSTGRES_PASSWORD=admin \
  -e POSTGRES_DB=gym-db \
  -p 5432:5432 \
  postgres:18-alpine

docker run -d --name mongo_gym \
  -e MONGO_INITDB_ROOT_USERNAME=admin \
  -e MONGO_INITDB_ROOT_PASSWORD=admin \
  -e MONGO_INITDB_DATABASE=gym_db \
  -p 27017:27017 \
  mongo
```

#### 4. Run RabbitMQ

```bash
docker run -d --name rabbitmq_gym \
  -e RABBITMQ_DEFAULT_USER=admin \
  -e RABBITMQ_DEFAULT_PASS=admin \
  -p 5672:5672 \
  -p 15672:15672 \
  rabbitmq:management-alpine
```

#### 5. Start Services (in order)

```bash
# Terminal 1: Registry Server
cd registry-server
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Terminal 2: API Gateway
cd api-gateway
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Terminal 3: Main App
cd main-app
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"

# Terminal 4: Statistics App
cd statistics-app
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

---

## API Endpoints

### Base URLs

- **Local Development:** `http://localhost:8080/GymCRM/api`
- **Local Direct (Main App):** `http://localhost:8081/api`
- **Direct Docker:** `http://api-gateway:8080/GymCRM/api`

### Authentication Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/auth/tokens` | Get access and refresh tokens | Basic Auth |
| POST | `/auth/refresh-token` | Refresh access token | Bearer Token |
| POST | `/auth/logout` | Logout and revoke tokens | Bearer Token |
| PUT | `/auth/change-password` | Change user password | Bearer Token |


### Trainee Management Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/trainees` | Register new trainee | Public |
| GET | `/trainees/{username}` | Get trainee profile | Bearer Token |
| PUT | `/trainees/{username}` | Update trainee profile | Bearer Token |
| PUT | `/trainees/{username}/trainers` | Update trainer assignments | Bearer Token |
| PATCH | `/trainees/{username}` | Toggle trainee active status | Bearer Token |
| DELETE | `/trainees/{username}` | Delete trainee | Bearer Token |


### Trainer Management Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| POST | `/trainers` | Register new trainer | Public |
| GET | `/trainers/{username}` | Get trainer profile | Bearer Token |
| PUT | `/trainers/{username}` | Update trainer profile | Bearer Token |
| PUT | `/trainers/{username}/trainees` | Update trainee assignments | Bearer Token |
| PATCH | `/trainers/{username}` | Toggle trainer active status | Bearer Token |
| DELETE | `/trainers/{username}` | Delete trainer | Bearer Token |


### Training Management Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/trainings/trainee/{username}` | Get trainee trainings | Bearer Token |
| POST | `/trainings` | Create training session | Bearer Token |
| GET | `/trainings/{trainingId}` | Get training details | Bearer Token |


### Statistics Endpoints

| Method | Endpoint | Description | Auth |
|--------|----------|-------------|------|
| GET | `/statistics/{trainerUsername}` | Get trainer statistics | Public |

---

## Authentication

### JWT Token Structure

GymCRM uses JSON Web Tokens (JWT) for stateless authentication with the following components:

#### Header

```json
{
  "alg": "HS256",
  "typ": "JWT"
}
```

#### Payload (Claims)

```json
{
  "jti": "550e8400-e29b-41d4-a716-446655440000",
  "jtt": "ACCESS_TOKEN|REFRESH_TOKEN",
  "iss": "GymCRM",
  "sub": "username",
  "aud": "GymCRM-Users",
  "iat": 1694959200,
  "exp": 1694962800
}
```

**Claim Descriptions:**
- `jti` - JWT ID (unique identifier)
- `jtt` - JWT Token Type (ACCESS_TOKEN or REFRESH_TOKEN)
- `iss` - Issuer (GymCRM)
- `sub` - Subject (username)
- `aud` - Audience (GymCRM-Users)
- `iat` - Issued At (timestamp)
- `exp` - Expiration Time (timestamp)

#### Token Lifecycle

1. **Login:** User provides credentials via Basic Auth to `/auth/tokens`
2. **Token Generation:** Server generates both access and refresh tokens
3. **Access Token Use:** Client includes access token in Authorization header
4. **Token Refresh:** When access token expires, use refresh token to get new pair
5. **Logout:** Tokens are revoked and cannot be reused

### Implementation Details

**Signature Algorithm:** HMAC with SHA-256 (HS256)
- Minimum key size: 256 bits
- Key management: Server-side secret key

**Password Hashing:** BCrypt
- Default strength: 10 rounds
- Applied on registration and password changes

### Usage Examples

#### Basic Authentication (Login)

```bash
curl -X GET http://localhost:8081/api/auth/tokens \
  -H "Authorization: Basic $(echo -n 'username:password' | base64)" \
  -H "Content-Type: application/json"
```

#### Bearer Token Usage

```bash
curl -X GET http://localhost:8081/api/trainees/john.doe@gymcrm \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  -H "Content-Type: application/json"
```

#### Token Refresh

```bash
curl -X POST http://localhost:8081/api/auth/refresh-token \
  -H "Content-Type: application/json" \
  -d '{"refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."}'
```

### Public Endpoints

The following endpoints are accessible without authentication:

```
GET  /api/auth/tokens                    (Basic Auth required)
POST /api/trainees                       (Registration)
POST /api/trainers                       (Registration)
GET  /statistics/{trainerUsername}       (Public statistics)
GET  /swagger-ui.html                    (Swagger UI)
GET  /v3/api-docs                        (OpenAPI spec)
GET  /actuator/health                    (Health check)
```

### Security Features

- **Cross-Origin Resource Sharing (CORS)** - Enabled for development
- **SQL Injection Prevention** - Parameterized queries via JPA
- **Password Encoding** - BCrypt hashing
- **Token Revocation** - Tokens tracked in database
- **Account Locking** - After failed login attempts
- **Session Management** - Stateless JWT-based

---

## Database Schema

### PostgreSQL Schema (Main Application)

#### Overview

GymCRM uses PostgreSQL for relational data with the following entities and relationships:

```
┌─────────────────────────────┐
│         Users (Base)        │
│  - id (PK)                  │
│  - first_name               │
│  - last_name                │
│  - username (Unique)        │
│  - password (BCrypt)        │
│  - is_active (Boolean)      │
└─────────────────────────────┘
      ▲                ▲
      │                │
      │ (1:1)          │ (1:1)
      │                │
┌─────┴───────┐    ┌───┴────────────┐
│  Trainees   │    │    Trainers    │
│ - id (FK)   │    │ - id (FK)      │
│ - DOB       │    │ - spec_id (FK) │
│ - address   │    │                │
└──┬───────┬──┘    └───┬────┬───────┘
   │       │           │    └──────────┐
   │       │ (1:M)     │ (1:M)         │
   │       │           │               │
   │       │  ┌────────▼──────────┐    │
   │       │  │    Trainings      │    │
   │       │  │ - id (PK)         │    │
   │       └─►│ - trainee_id (FK) │    │
   │          │ - trainer_id(FK)  │    │
   │          │ - name            │    │
   │          │ - type_id (FK)    │    │
   │          │ - date            │    │
   │          │ - duration        │    │
   │          └────────┬──────────┘    │
   │ (M:M)             │ (M:1)         │
   │        ┌──────────▼─────────────┐ │
   │        │     TrainingTypes      │ │
   │        │ - id (PK)              │ │
   │        │ - training_type_name   │ │  
   │        └────────────────────────┘ │
┌──▼────────────────┐                  │
│ Trainees_Trainers │        (M:M)     │
│ - id (PK)         │◄─────────────────┘       
│ - trainee_id(FK)  │      
│ - trainer_id(FK)  │        
└───────────────────┘       
           
```
## Docker Setup

### Prerequisites

- Docker Engine 20.10+
- Docker Compose 2.0+

### Quick Start with Docker Compose

#### 1. Build and Start All Services

```bash
# Navigate to project root directory
cd GymCRM

# Build and start all services
docker-compose up -d --build

# View logs
docker-compose logs -f

# Stop all services
docker-compose down

# Clean up (remove volumes)
docker-compose down -v
```

#### 2. Access Services

| Service | URL | Port |
|---------|-----|------|
| API Gateway | http://localhost:8080/GymCRM | 8080 |
| Main App | http://localhost:8081 | 8081 |
| Statistics App | http://localhost:8082 | 8082 |
| Registry Server (Eureka) | http://localhost:8761 | 8761 |
| RabbitMQ Management | http://localhost:15672 | 15672 |
| PostgreSQL | localhost:5432 | 5432 |
| MongoDB | localhost:27017 | 27017 |
---

## Swagger Documentation

### Accessing Swagger UI

Swagger UI provides interactive API documentation and testing capabilities.

**URLs:**
- **Main App:** http://localhost:8081/swagger-ui.html
- **API Gateway:** http://localhost:8080/GymCRM/swagger-ui.html

**OpenAPI Specification:**
- **Main App:** http://localhost:8081/v3/api-docs
- **API Gateway:** http://localhost:8080/GymCRM/v3/api-docs

### Swagger Configuration

The application includes pre-configured Swagger support with:

```java
// Security schemes
bearerAuth     - JWT Bearer Token
basicAuth      - HTTP Basic Authentication

// API Documentation
Title:    "Gym CRM API"
Version:  "1.0"
```

### Security Schemes in Swagger

**Bearer Token (JWT):**
```
Authorization: Bearer <access_token>
```

**Basic Authentication:**
```
Authorization: Basic <base64_encoded_username:password>
```

### API Documentation Features

1. **Interactive Testing**
   - Click "Try It Out" button to test endpoints
   - Swagger automatically handles authentication headers
   - View request/response examples

2. **Schema Documentation**
   - Detailed request/response models
   - Field descriptions and constraints
   - Example values

3. **Endpoint Organization**
   - **Authentication Management** - Token and logout operations
   - **Trainees Management** - CRUD operations for trainees
   - **Trainers Management** - CRUD operations for trainers
   - **Trainings Management** - Training session management
   - **Statistics** - Trainer statistics and analytics

### Example: Testing Authentication Endpoint

1. Open Swagger UI at http://localhost:8081/swagger-ui.html
2. Find "Authentication Management" section
3. Click on "GET /api/auth/tokens"
4. Click "Try It Out"
5. In the Authorization field, enter: `Basic dXNlcm5hbWU6cGFzc3dvcmQ=`
6. Click "Execute"
7. View the response with access and refresh tokens
---

## Testing

### Testing Strategy

GymCRM implements a comprehensive testing approach covering:

1. **Unit Tests**
   - Individual service and utility methods
   - Business logic validation
   - Edge case handling

2. **Integration Tests**
   - Database interactions
   - JWT token generation and validation
   - Service-to-service communication

3. **Behavioral Tests (BDD)**
   - Cucumber feature scenarios
   - End-to-end workflow validation
   - User story verification

### Testing Technologies

- **JUnit 5** - Unit and integration test framework
- **Spring Boot Test** - Spring-specific testing utilities
- **Mockito** - Mocking and stubbing
- **Cucumber** - BDD test scenarios
- **H2** - In-memory database for tests
- **Spring Security Test** - Security layer testing

### Running Tests

#### Run All Tests

```bash
# From root directory
mvn test

# With coverage reports
mvn test jacoco:report
```

#### Run Specific Module Tests

```bash
# Main App tests
cd main-app
mvn test

# Statistics App tests
cd statistics-app
mvn test

# Cucumber BDD tests
cd cucumber
mvn test
```

#### Run Specific Test Class

```bash
mvn test -Dtest=JwtFactoryUtilTest
mvn test -Dtest=AuthenticationServiceImplTest
```

#### Run Specific Test Method

```bash
mvn test -Dtest=JwtFactoryUtilTest#testTokenGeneration
``