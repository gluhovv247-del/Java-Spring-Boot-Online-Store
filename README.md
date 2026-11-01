# Online Store — Backend

A RESTful backend application for an online store built with Java and Spring Boot.

The project is designed to implement core e-commerce functionality while following common backend development practices, including layered architecture, data validation, database migrations, and automated testing.

## Tech Stack

- **Java 25**
- **Spring Boot**
- **Spring Data JPA**
- **Hibernate**
- **PostgreSQL**
- **Flyway** — database migrations
- **Bean Validation** — request validation
- **Lombok**
- **MapStruct**
- **JUnit 5**
- **Mockito**
- **AssertJ**
- **Testcontainers**
- **Maven**

## Implemented Features

### Product Management

Implemented the core CRUD operations for products:

- Create new products.
- Retrieve a product by ID.
- Retrieve the product catalog.
- Update existing products.
- Delete products.
- Manage product availability and status.

### Product Catalog

Implemented flexible product retrieval with:

- **Filtering** by product attributes.
- **Pagination** to efficiently retrieve large product collections.
- **Sorting** by supported product fields and directions.
- Combined filtering, pagination, and sorting.

Spring Data JPA Specifications are used to build dynamic database queries based on request parameters.

### Data Validation

- Request validation using Jakarta Bean Validation.
- Constraints on product fields.
- Database-level constraints to maintain data integrity.
- Centralized handling of application exceptions.

### Database

- PostgreSQL as the primary relational database.
- Entity mapping using Hibernate and JPA.
- Database schema management through Flyway migrations.
- Defined primary keys, foreign keys, unique constraints, and check constraints.

### Testing

Implemented automated tests covering the main application functionality:

- Unit tests for service-layer business logic.
- Repository and database integration tests.
- REST controller tests.
- Request validation tests.
- Exception handling tests.
- Product filtering, pagination, and sorting scenarios.

Testcontainers is used to run integration tests against a real PostgreSQL database in an isolated Docker container.

## Project Architecture

The application follows a layered architecture:

- **Controller** — handles HTTP requests and responses.
- **Service** — contains business logic.
- **Repository** — handles database operations.
- **Entity** — represents database entities.
- **DTO** — defines API request and response models.
- **Mapper** — handles entity-to-DTO conversion.

## Getting Started

### Prerequisites

- JDK 25
- Maven
- Docker
- PostgreSQL (if running without Docker)

### Running the Application

```bash
git clone <repository-url>
cd online-store
```

Configure the database connection in `application.yml` or environment variables.

Run the application:

```bash
./mvnw spring-boot:run
```

### Running Tests

```bash
./mvnw test
```

## Project Status

The product management module is implemented, including CRUD operations, dynamic filtering, pagination, sorting, database migrations, and automated testing.

Further e-commerce functionality is under development.