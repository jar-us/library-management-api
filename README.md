# Library Management System API

A RESTful API for managing library operations built with Spring Boot and Kotlin.

## Overview

This application provides a backend API for managing library books, with plans to support user management and book borrowing transactions. Currently implements basic book management operations.

### Technology Stack
- **Framework**: Spring Boot 3.4.3
- **Language**: Kotlin 1.9.25
- **Database**: PostgreSQL (production), H2 (testing)
- **Build Tool**: Maven
- **Java Version**: 17

## Current Features

- Add new books to the library catalog
- Retrieve all books
- Get specific book details by ID
- Input validation and error handling
- ISBN uniqueness enforcement

## API Endpoints

### Books Management
- `POST /api/books` - Add a new book
- `GET /api/books` - Retrieve all books
- `GET /api/books/{id}` - Get book by ID

### Example Request
```bash
curl -X POST http://localhost:8080/api/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "The Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "publicationYear": 2008,
    "genre": "Programming",
    "totalCopies": 10,
    "availableCopies": 10
  }'
```

## Getting Started

### Prerequisites
- Java 17+
- Docker (for PostgreSQL)
- Maven (or use included wrapper)

### Database Setup

1. **Start PostgreSQL using Docker:**
   ```bash
   docker run -d \
     --name library-postgres \
     -e POSTGRES_DB=library-management-system \
     -e POSTGRES_USER=postgres \
     -e POSTGRES_PASSWORD=postgres \
     -p 5432:5432 \
     postgres:latest
   ```

2. **Verify PostgreSQL is running:**
   ```bash
   docker ps
   ```

### Running the Application

1. **Clone the repository:**
   ```bash
   git clone <repository-url>
   cd library-management-api
   ```

2. **Run the application:**
   ```bash
   ./mvnw spring-boot:run
   ```

3. **The application will be available at:**
   - API: http://localhost:8080/api/books
   - Health Check: http://localhost:8080/actuator/health (if actuator is enabled)

### Running Tests

```bash
./mvnw test
```

Tests use H2 in-memory database and don't require PostgreSQL to be running.

### Database Management

The application uses `spring.jpa.hibernate.ddl-auto=create-drop`, which means:
- Database schema is created automatically on startup
- All data is lost when the application stops
- Perfect for development and testing

## Project Structure

```
src/main/kotlin/jar/us/librarymanagementsystemapi/
├── LibraryManagementSystemApiApplication.kt    # Main application
├── BookController.kt                           # REST endpoints
├── domain/Book.kt                              # JPA entity
├── repository/BookRepository.kt                # Data access
├── service/BookService.kt                      # Business logic
├── schema/BookRequest.kt                       # DTOs and mapping
└── exception/GlobalExceptionHandler.kt         # Error handling
```

## Planned Features

Based on the OpenAPI specification, future implementations will include:
- User management (registration, profiles)
- Book borrowing and return system
- Transaction history tracking
- User borrowing limits
- Book availability management

## Development

### Common Commands
- **Build**: `./mvnw clean package`
- **Run tests**: `./mvnw test`
- **Run specific test**: `./mvnw test -Dtest=ClassName`

### Database Access
PostgreSQL can be accessed directly:
```bash
docker exec -it library-postgres psql -U postgres -d library-management-system
```

## Troubleshooting

### Common Issues

1. **Application fails to start with database connection error:**
   - Ensure PostgreSQL container is running: `docker ps`
   - Check container logs: `docker logs library-postgres`

2. **Port 5432 already in use:**
   - Stop existing PostgreSQL processes or use a different port
   - Modify the docker run command: `-p 5433:5432`
   - Update `application.properties` accordingly

3. **Tests failing:**
   - Tests should work independently of PostgreSQL
   - Check if H2 dependency is properly configured

### Stopping the Application
- Application: `Ctrl+C` in the terminal
- PostgreSQL container: `docker stop library-postgres`
- Remove container: `docker rm library-postgres`

## Contributing

1. Follow existing code style and patterns
2. Add tests for new features
3. Update this README for any setup changes
4. Ensure all tests pass before submitting