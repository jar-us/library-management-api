# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build and Development Commands

This is a Spring Boot application using Maven and Kotlin. Common commands:

- **Run application**: `./mvnw spring-boot:run`
- **Run tests**: `./mvnw test`
- **Run specific test**: `./mvnw test -Dtest=ClassName`
- **Build application**: `./mvnw clean package`
- **Run with specific profile**: `./mvnw spring-boot:run -Dspring-boot.run.profiles=test`

## Architecture Overview

### Technology Stack
- **Framework**: Spring Boot 3.4.3 with Kotlin 1.9.25
- **Database**: PostgreSQL (production), H2 (tests)
- **Build Tool**: Maven
- **Testing**: JUnit 5, Spring Boot Test, MockMvc
- **Validation**: Jakarta Validation API with Hibernate Validator

### Project Structure
```
src/main/kotlin/jar/us/librarymanagementsystemapi/
├── LibraryManagementSystemApiApplication.kt    # Main application entry point
├── BookController.kt                           # REST endpoints for books
├── domain/Book.kt                              # JPA entity
├── repository/BookRepository.kt                # Data access layer
├── service/BookService.kt                      # Business logic
├── schema/BookRequest.kt                       # DTOs and mapping functions
└── exception/GlobalExceptionHandler.kt         # Centralized error handling
```

### Database Configuration
- **Production**: PostgreSQL at `localhost:5432/library-management-system`
- **Tests**: H2 in-memory database
- **JPA**: Uses `create-drop` DDL strategy (recreates schema on startup)

### Current API Implementation
Currently implements basic book management:
- `POST /api/books` - Add new book
- `GET /api/books` - Retrieve all books  
- `GET /api/books/{id}` - Retrieve book by ID

### Testing Strategy
- **Base Test Class**: `AbstractBookControllerTest` - provides common setup with MockMvc, ObjectMapper, and BookRepository
- **Integration Tests**: Full Spring Boot context with test profile
- **Database**: H2 in-memory database, cleared before each test
- **Test Organization**: Tests are organized by feature in `src/test/kotlin/jar/us/librarymanagementsystemapi/book/`

### Key Design Patterns
- **DTO Pattern**: Separate `BookRequest`/`BookResponse` DTOs with extension functions for entity mapping
- **Service Layer**: Business logic isolated in `BookService`
- **Global Exception Handling**: Centralized error handling with `@RestControllerAdvice`
- **Validation**: Jakarta Validation annotations on both entity and DTO levels

### API Contract
The `library-api-contract-openapi.yml` file defines the complete API specification including endpoints for books, users, and transactions that are planned but not yet implemented.

### Development Notes
- The application uses Spring Boot's auto-configuration
- Database schema is auto-generated from JPA entities
- Error responses follow a consistent `ErrorResponse` format
- ISBN uniqueness is enforced at the service layer
- All validation errors are properly handled and formatted