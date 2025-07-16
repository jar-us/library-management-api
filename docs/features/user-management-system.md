# User Management System Implementation

## Overview

The User Management System provides comprehensive user registration, authentication, and profile management capabilities for the Library Management API. This system serves as the foundation for the borrowing system, enabling user identification, authorization, and membership management.

## Architecture

The implementation follows a clean architecture pattern with clear separation of concerns:

```
┌─────────────────────────────────────────────────────────────────┐
│                    Presentation Layer                           │
├─────────────────────────────────────────────────────────────────┤
│                     Application Layer                           │
├─────────────────────────────────────────────────────────────────┤
│                       Domain Layer                              │
├─────────────────────────────────────────────────────────────────┤
│                    Infrastructure Layer                         │
└─────────────────────────────────────────────────────────────────┘
```

## Domain Layer

### User Entity (`User.kt`)

The core domain entity representing a user in the system:

```kotlin
@Entity
@Table(name = "users")
data class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    val username: String,
    val email: String,
    val password: String,
    val firstName: String,
    val lastName: String,
    val phoneNumber: String? = null,
    val membershipStatus: MembershipStatus = MembershipStatus.ACTIVE,
    val registrationDate: LocalDateTime = LocalDateTime.now(),
    val lastLoginDate: LocalDateTime? = null,
    val membershipExpiryDate: LocalDateTime? = null
)
```

**Key Features:**
- **Immutable Design**: Uses data class with val properties
- **Validation**: Built-in validation in init block and helper methods
- **Business Logic**: Methods for membership management, profile updates
- **Factory Method**: Static `create()` method for safe object creation

**Business Methods:**
- `isActive()`: Check if user has active membership
- `canBorrowBooks()`: Determine if user can borrow books
- `updateLastLogin()`: Update last login timestamp
- `suspendMembership()`: Suspend user account
- `activateMembership()`: Activate user account
- `updateProfile()`: Update user profile information

### MembershipStatus Enum

```kotlin
enum class MembershipStatus {
    ACTIVE,     // User can access all features
    SUSPENDED,  // User access is temporarily restricted
    EXPIRED     // User membership has expired
}
```

### User Business Exception

```kotlin
class UserBusinessException(message: String, cause: Throwable? = null) 
    : RuntimeException(message, cause)
```

## Repository Layer

### UserRepository Interface (`UserRepository.kt`)

Defines the contract for user data access:

```kotlin
interface UserRepository {
    fun save(user: User): User
    fun findById(id: Long): User?
    fun findAll(): List<User>
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun existsById(id: Long): Boolean
    fun deleteById(id: Long)
    fun deleteAll()
    fun findByMembershipStatus(membershipStatus: MembershipStatus): List<User>
    fun findByFirstNameContainingIgnoreCase(firstName: String): List<User>
    fun findByLastNameContainingIgnoreCase(lastName: String): List<User>
    fun findByFullNameContainingIgnoreCase(fullName: String): List<User>
}
```

### JPA Implementation (`JpaUserRepository.kt`)

Spring Data JPA repository implementation:

```kotlin
@Repository
interface JpaUserRepository : JpaRepository<User, Long>, UserRepository {
    override fun findByUsername(username: String): User?
    override fun findByEmail(email: String): User?
    override fun existsByUsername(username: String): Boolean
    override fun existsByEmail(email: String): Boolean
    override fun findByMembershipStatus(membershipStatus: MembershipStatus): List<User>
    override fun findByFirstNameContainingIgnoreCase(firstName: String): List<User>
    override fun findByLastNameContainingIgnoreCase(lastName: String): List<User>
    
    @Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %:fullName%")
    override fun findByFullNameContainingIgnoreCase(@Param("fullName") fullName: String): List<User>
}
```

## Application Layer

### UserManagementService Interface

```kotlin
interface UserManagementService {
    fun registerUser(user: User): User
    fun authenticateUser(username: String, password: String): User?
    fun findUserById(id: Long): User?
    fun findUserByUsername(username: String): User?
    fun findUserByEmail(email: String): User?
    fun findAllUsers(): List<User>
    fun updateUser(id: Long, user: User): User
    fun deleteUser(id: Long): Boolean
    fun suspendUser(id: Long): User
    fun activateUser(id: Long): User
    fun updateUserProfile(id: Long, firstName: String?, lastName: String?, phoneNumber: String?, email: String?): User
    fun changePassword(id: Long, oldPassword: String, newPassword: String): User
    fun searchUsersByName(name: String): List<User>
    fun filterUsersByMembershipStatus(membershipStatus: MembershipStatus): List<User>
    fun updateLastLogin(id: Long): User
}
```

### UserManagementServiceImpl

**Key Features:**
- **Transactional Operations**: Uses `@Transactional` for data consistency
- **Validation**: Integrates with `UserValidationService`
- **Business Logic**: Implements complex business rules
- **Error Handling**: Throws appropriate exceptions for different scenarios

**Key Methods:**

1. **User Registration**:
   ```kotlin
   override fun registerUser(user: User): User {
       userValidationService.validateNewUser(user)
       
       if (userRepository.existsByUsername(user.username)) {
           throw UserBusinessException("Username '${user.username}' already exists")
       }
       
       if (userRepository.existsByEmail(user.email)) {
           throw UserBusinessException("Email '${user.email}' already exists")
       }
       
       return userRepository.save(user)
   }
   ```

2. **User Authentication**:
   ```kotlin
   override fun authenticateUser(username: String, password: String): User? {
       val user = userRepository.findByUsername(username) ?: return null
       
       if (user.password != password) return null
       
       if (!user.canBorrowBooks()) {
           throw UserBusinessException("User account is not active or has expired")
       }
       
       val updatedUser = user.updateLastLogin()
       return userRepository.save(updatedUser)
   }
   ```

### UserValidationService

Handles validation logic for user operations:

```kotlin
@Service
class UserValidationService {
    fun validateNewUser(user: User)
    fun validateUserUpdate(user: User)
    private fun validateUserCommon(user: User)
}
```

**Validation Rules:**
- Username: 3-20 characters, alphanumeric and underscore only
- Email: Valid email format
- Password: Minimum 6 characters
- Phone: 10-15 digits (optional)
- Names: Non-blank, proper length

## DTOs (Data Transfer Objects)

### Request DTOs

1. **CreateUserRequestDto**: For user registration
2. **UpdateUserRequestDto**: For complete user updates
3. **UpdateUserProfileRequestDto**: For partial profile updates
4. **ChangePasswordRequestDto**: For password changes
5. **LoginRequestDto**: For authentication

### Response DTOs

1. **UserResponseDto**: Complete user information
2. **LoginResponseDto**: Authentication response
3. **UserSearchResultDto**: Search results wrapper

### UserMapper

Handles conversion between domain objects and DTOs:

```kotlin
@Component
class UserMapper {
    fun toUser(dto: CreateUserRequestDto): User
    fun toUser(dto: UpdateUserRequestDto, existingUser: User): User
    fun toUserResponseDto(user: User): UserResponseDto
    fun toUserResponseDtoList(users: List<User>): List<UserResponseDto>
    fun toLoginResponseDto(user: User): LoginResponseDto
    fun toUserSearchResultDto(users: List<User>): UserSearchResultDto
}
```

## Presentation Layer

### UserManagementController

REST controller providing HTTP endpoints:

```kotlin
@RestController
@RequestMapping("/api/users")
class UserManagementController(
    private val userManagementService: UserManagementService,
    private val userMapper: UserMapper
)
```

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/users/register` | Register new user |
| POST | `/api/users/login` | Authenticate user |
| GET | `/api/users` | Get all users |
| GET | `/api/users/{id}` | Get user by ID |
| GET | `/api/users/username/{username}` | Get user by username |
| GET | `/api/users/email/{email}` | Get user by email |
| PUT | `/api/users/{id}` | Update user |
| PATCH | `/api/users/{id}/profile` | Update user profile |
| PATCH | `/api/users/{id}/password` | Change password |
| PATCH | `/api/users/{id}/suspend` | Suspend user |
| PATCH | `/api/users/{id}/activate` | Activate user |
| DELETE | `/api/users/{id}` | Delete user |
| GET | `/api/users/search?name={name}` | Search users by name |
| GET | `/api/users/filter?membershipStatus={status}` | Filter by membership status |

### Request/Response Examples

**User Registration:**
```json
POST /api/users/register
{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123",
    "firstName": "John",
    "lastName": "Doe",
    "phoneNumber": "1234567890",
    "membershipStatus": "ACTIVE"
}
```

**User Authentication:**
```json
POST /api/users/login
{
    "username": "johndoe",
    "password": "password123"
}
```

**Response:**
```json
{
    "user": {
        "id": 1,
        "username": "johndoe",
        "email": "john@example.com",
        "firstName": "John",
        "lastName": "Doe",
        "fullName": "John Doe",
        "phoneNumber": "1234567890",
        "membershipStatus": "ACTIVE",
        "registrationDate": "2024-01-01T10:00:00",
        "lastLoginDate": "2024-01-01T10:00:00",
        "membershipExpiryDate": null,
        "isActive": true,
        "canBorrowBooks": true,
        "isMembershipExpired": false
    },
    "message": "Login successful"
}
```

## Exception Handling

### Global Exception Handler

The system integrates with the existing `GlobalExceptionHandler`:

```kotlin
@ExceptionHandler(UserNotFoundException::class)
fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorResponse>

@ExceptionHandler(UserBusinessException::class)
fun handleUserBusinessException(ex: UserBusinessException): ResponseEntity<ErrorResponse>
```

### Error Response Format

```json
{
    "error": "Username 'johndoe' already exists",
    "timestamp": "2024-01-01T10:00:00",
    "status": 400
}
```

## Web UI Integration

### Updated index.html

The web UI includes a new "Users" section with:

1. **User Registration Form**
   - Username, email, password validation
   - Phone number (optional)
   - Membership status selection

2. **User Login Form**
   - Username and password authentication
   - Success/error messaging

3. **User Search**
   - Search by ID, username, or name
   - Real-time search results

4. **User Management**
   - View all users
   - Filter by membership status
   - Suspend/activate users
   - User profile display

### JavaScript Functions

- `registerUser()`: Handle user registration
- `authenticateUser()`: Handle user login
- `searchUserById()`: Search user by ID
- `searchUserByUsername()`: Search user by username
- `searchUsersByName()`: Search users by name
- `loadAllUsers()`: Load and display all users
- `filterUsersByStatus()`: Filter users by membership status
- `suspendUser()`: Suspend user account
- `activateUser()`: Activate user account

## Database Schema

### Users Table

```sql
CREATE TABLE users (
    id BIGINT PRIMARY KEY GENERATED BY DEFAULT AS IDENTITY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    first_name VARCHAR(255) NOT NULL,
    last_name VARCHAR(255) NOT NULL,
    phone_number VARCHAR(255),
    membership_status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE',
    registration_date TIMESTAMP NOT NULL,
    last_login_date TIMESTAMP,
    membership_expiry_date TIMESTAMP
);
```

### Indexes

- Primary key on `id`
- Unique constraint on `username`
- Unique constraint on `email`
- Index on `membership_status` for filtering

## Testing Strategy

### Test Structure

```
src/test/kotlin/jar/us/librarymanagementsystemapi/user/
├── AbstractUserControllerTest.kt       # Base test class
├── RegisterUserTest.kt                 # User registration tests
├── AuthenticateUserTest.kt             # Authentication tests
├── RetrieveUsersTest.kt                # User retrieval tests
└── UpdateUserTest.kt                   # User update tests
```

### Test Coverage

**RegisterUserTest (10 tests):**
- Valid user registration
- Minimum required fields
- Custom membership status
- Duplicate username/email validation
- Invalid email format
- Short password validation
- Invalid username format
- Blank required fields
- Invalid phone number

**AuthenticateUserTest (8 tests):**
- Valid credentials
- Invalid username/password
- Suspended user authentication
- Expired user authentication
- Blank credentials validation
- Last login update verification

**RetrieveUsersTest (12 tests):**
- Get all users
- Get user by ID/username/email
- Search users by name
- Filter by membership status
- Handle non-existent users
- Empty result scenarios

**UpdateUserTest (13 tests):**
- Update user with valid data
- Update user profile
- Change password
- Suspend/activate user
- Delete user
- Validation error scenarios
- Duplicate username/email handling

### Test Configuration

- **Database**: H2 in-memory database for isolation
- **Framework**: JUnit 5 with Spring Boot Test
- **MockMvc**: For HTTP endpoint testing
- **Test Profile**: Uses `application-test.properties`

## Security Considerations

### Current Implementation

- **Password Storage**: Plain text (suitable for demo/development)
- **Authentication**: Username/password based
- **Authorization**: Role-based through membership status
- **Validation**: Multi-layer input validation

### Production Recommendations

1. **Password Security**: Implement bcrypt hashing
2. **JWT Tokens**: Add token-based authentication
3. **Rate Limiting**: Implement login attempt limits
4. **HTTPS**: Enforce secure connections
5. **Input Sanitization**: Additional XSS protection

## Configuration

### Application Properties

```properties
# Database configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/library-management-system
spring.datasource.username=username
spring.datasource.password=password

# JPA configuration
spring.jpa.hibernate.ddl-auto=create-drop
spring.jpa.show-sql=true
```

### Test Configuration

```properties
# Test database
spring.datasource.url=jdbc:h2:mem:testdb
spring.datasource.driver-class-name=org.h2.Driver
spring.jpa.hibernate.ddl-auto=create-drop
```

## Future Enhancements

### Planned Features

1. **Password Reset**: Email-based password recovery
2. **Email Verification**: Account activation via email
3. **User Roles**: Admin, librarian, member roles
4. **Profile Pictures**: User avatar support
5. **Activity Logging**: User action audit trail
6. **OAuth Integration**: Social login support
7. **Multi-factor Authentication**: Enhanced security
8. **User Preferences**: Customizable user settings

### Integration Points

1. **Borrowing System**: User identification for book loans
2. **Notification System**: User contact for overdue books
3. **Reporting System**: User activity analytics
4. **Search System**: User-specific search history

## Troubleshooting

### Common Issues

1. **Database Connection**: Check PostgreSQL service
2. **Test Failures**: Clear H2 database between tests
3. **Validation Errors**: Check DTO field annotations
4. **Authentication Issues**: Verify user status and credentials

### Debug Tips

1. Enable SQL logging: `spring.jpa.show-sql=true`
2. Check application logs for exceptions
3. Use Spring Boot Actuator for health checks
4. Verify database schema matches entity definitions

## Conclusion

The User Management System provides a solid foundation for the Library Management API with comprehensive user handling, authentication, and profile management. The clean architecture ensures maintainability, while extensive testing guarantees reliability. The system is ready for integration with the borrowing system and future enhancements.