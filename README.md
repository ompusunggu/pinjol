# Pinjol Loan Management System

Pinjol is a modular loan management system built with Spring Boot, designed to handle loan creation, payment scheduling, delinquency tracking, and payment processing. The system is organized as a multi-module Maven project for clear separation of concerns and scalability.

## Modules

- **pinjol-entity**: JPA entities for core domain models (`Loan`, `PaymentSchedule`).
- **pinjol-repository**: Spring Data JPA repositories for data access.
- **pinjol-command**: Business logic and service layer (e.g., loan creation, payment processing).
- **pinjol-web-model**: API request/response models.
- **pinjol-properties**: Configuration properties for the application.
- **pinjol-web**: REST API controllers.
- **pinjol-app**: Main application entry point and configuration.

## Features

- Create loans with configurable interest rate and term.
- Generate payment schedules and track payments.
- Check outstanding loan amounts and delinquency status.
- Make payments against scheduled installments.

## Technologies

- Java 21
- Spring Boot 3.5.x
- Spring Data JPA
- PostgreSQL (default configuration)
- Maven (multi-module)

## Getting Started

### Prerequisites
- Java 21+
- Maven 3.8+
- PostgreSQL (or update datasource in `application.properties`)

### Configuration
Edit `pinjol-app/src/main/resources/application.properties` as needed:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=postgres
spring.datasource.password=postgres
spring.jpa.hibernate.ddl-auto=create
pinjol.loan.interest-rate=0.1
pinjol.loan.term=50
pinjol.loan.delinquent-days-limit=-14
```

### Build & Run

```bash
mvn clean install
cd pinjol-app
mvn spring-boot:run
```

The application will start on [http://localhost:8080](http://localhost:8080).

## API Endpoints

All endpoints are prefixed with `/api/loans`.

### 1. Create Loan
- **POST** `/api/loans`
- **Request Body:**
  ```json
  { "amount": 1000.0 }
  ```
- **Response:** Loan object

### 2. Get Outstanding Loan
- **GET** `/api/loans/{loanId}/outstanding`
- **Response:**
  ```json
  {
    "loanId": "...",
    "outstandingPrincipal": 900.0,
    "totalAmount": 1000.0,
    "interestRate": 0.1,
    "term": 50,
    "status": "ACTIVE"
  }
  ```

### 3. Check Delinquent Status
- **GET** `/api/loans/{loanId}/delinquent`
- **Response:**
  ```json
  {
    "loanId": "...",
    "delinquent": true,
    "reason": "Delinquent payment found",
    "daysOverdue": 5
  }
  ```

### 4. Make Payment
- **POST** `/api/loans/{loanId}/payment`
- **Request Body:**
  ```json
  { "paymentAmount": 22.0 }
  ```
- **Response:** PaymentSchedule object

## Data Model Overview

### Loan
- `id`: String (UUID)
- `totalAmount`: BigDecimal
- `interestRate`: BigDecimal
- `term`: Integer
- `weeklyPaymentAmount`: BigDecimal
- `principal`: BigDecimal
- `status`: Enum (PENDING, APPROVED, ACTIVE, COMPLETED, etc.)
- `firstPaymentDate`, `createdAt`, `updatedAt`: Long (epoch ms)

### PaymentSchedule
- `id`: String (UUID)
- `loan`: Loan
- `paymentNumber`: Integer
- `dueDate`: Long (epoch ms)
- `principalAmount`, `interestAmount`, `totalAmount`: BigDecimal
- `status`: Enum (PENDING, PAID, OVERDUE)
- `paidDate`: Long (epoch ms)
