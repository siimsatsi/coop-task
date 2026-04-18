# Coop Pank Praktika ülesanne

## Tech Stack

- **Backend**: Java 21, Spring Boot 3.4, Spring Data JPA, Liquibase
- **Database**: PostgreSQL 16
- **Frontend**: Vue 3, TypeScript, Vue Router
- **Docs**: SpringDoc OpenAPI (Swagger)
- **Containerisation**: Docker, Docker Compose

## Running the Application

Requires Docker and Docker Compose.

**First run or after code changes:**
```bash
docker-compose up --build
```

**Subsequent runs:**
```bash
docker-compose up
```

This starts three containers:
- `db`: PostgreSQL on port `5432`
- `app`: Spring Boot backend on port `8080`
- `frontend`: Nginx for the Vue app on port `3000`

Liquibase runs migrations automatically on startup.

Once running, open **http://localhost:3000** in your browser to access the application.

## Running the Frontend Locally

If you want hot reload while actively developing the frontend, you can run it outside Docker:

**Prerequisites:** Node.js 18+

```bash
cd frontend
npm install
npm run dev
```

The dev server starts on **http://localhost:5173**.

> **Note:** When running the frontend locally, the backend must still be running.
> Start it via Docker with `docker-compose up db app`.

## Usage

| Service  | URL                                    |
|----------|----------------------------------------|
| Frontend | http://localhost:3000                  |
| Swagger  | http://localhost:8080/swagger-ui.html  |
| Health   | http://localhost:8080/actuator/health  |

## API Overview

| Method | Endpoint                    | Description                        |
|--------|-----------------------------|------------------------------------|
| POST   | `/api/loans`                | Submit a new loan application      |
| GET    | `/api/loans`                | Get all applications in IN_REVIEW  |
| GET    | `/api/loans/{id}`           | Get a loan application by ID       |
| POST   | `/api/loans/{id}/approve`   | Approve a loan application         |
| POST   | `/api/loans/{id}/reject`    | Reject a loan application          |

## Loan Application Flow

1. Customer submits application. Status becomes `STARTED`
2. Age is validated from the social security number:
    - If age > 70 → automatically `REJECTED` with reason `CUSTOMER_TOO_OLD`
3. Annuity payment schedule is generated. Status moves to `IN_REVIEW`
4. Loan officer reviews and either approves (`APPROVED`) or rejects (`REJECTED`)

A customer can only have one active application at a time.

## Configuration

Key settings in `backend/src/main/resources/application.yaml`:

```yaml
app:
  loan:
    max-customer-age: 70
    min-amount: 5000
    min-term-months: 6
    max-term-months: 360
```

## Running Tests

Run the test directly

OR

```bash
cd backend
./gradlew test
```