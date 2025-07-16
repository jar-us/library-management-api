package jar.us.librarymanagementsystemapi.user

import jar.us.librarymanagementsystemapi.application.dto.CreateUserRequestDto
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import jar.us.librarymanagementsystemapi.domain.model.User
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

class RegisterUserTest : AbstractUserControllerTest() {

    @Test
    fun `should register user with valid data`() {
        // Given
        val request = CreateUserRequestDto(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "1234567890"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"))
            .andExpect(jsonPath("$.fullName").value("Test User"))
            .andExpect(jsonPath("$.phoneNumber").value("1234567890"))
            .andExpect(jsonPath("$.membershipStatus").value("ACTIVE"))
            .andExpect(jsonPath("$.isActive").value(true))
            .andExpect(jsonPath("$.canBorrowBooks").value(true))
            .andExpect(jsonPath("$.isMembershipExpired").value(false))
            .andExpect(jsonPath("$.id").exists())
            .andExpect(jsonPath("$.registrationDate").exists())
    }

    @Test
    fun `should register user with minimum required fields`() {
        // Given
        val request = CreateUserRequestDto(
            username = "minuser",
            email = "min@example.com",
            password = "password123",
            firstName = "Min",
            lastName = "User"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.username").value("minuser"))
            .andExpect(jsonPath("$.email").value("min@example.com"))
            .andExpect(jsonPath("$.firstName").value("Min"))
            .andExpect(jsonPath("$.lastName").value("User"))
            .andExpect(jsonPath("$.phoneNumber").doesNotExist())
            .andExpect(jsonPath("$.membershipStatus").value("ACTIVE"))
    }

    @Test
    fun `should register user with custom membership status`() {
        // Given
        val request = CreateUserRequestDto(
            username = "suspendeduser",
            email = "suspended@example.com",
            password = "password123",
            firstName = "Suspended",
            lastName = "User",
            membershipStatus = MembershipStatus.SUSPENDED
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.membershipStatus").value("SUSPENDED"))
            .andExpect(jsonPath("$.isActive").value(false))
            .andExpect(jsonPath("$.canBorrowBooks").value(false))
    }

    @Test
    fun `should fail to register user with duplicate username`() {
        // Given
        val existingUser = User.create(
            username = "duplicateuser",
            email = "existing@example.com",
            password = "password123",
            firstName = "Existing",
            lastName = "User"
        )
        userRepository.save(existingUser)

        val request = CreateUserRequestDto(
            username = "duplicateuser",
            email = "new@example.com",
            password = "password123",
            firstName = "New",
            lastName = "User"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Username 'duplicateuser' already exists"))
    }

    @Test
    fun `should fail to register user with duplicate email`() {
        // Given
        val existingUser = User.create(
            username = "existinguser",
            email = "duplicate@example.com",
            password = "password123",
            firstName = "Existing",
            lastName = "User"
        )
        userRepository.save(existingUser)

        val request = CreateUserRequestDto(
            username = "newuser",
            email = "duplicate@example.com",
            password = "password123",
            firstName = "New",
            lastName = "User"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Email 'duplicate@example.com' already exists"))
    }

    @Test
    fun `should fail to register user with invalid email format`() {
        // Given
        val request = CreateUserRequestDto(
            username = "testuser",
            email = "invalid-email",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.email").value("Email must be valid"))
    }

    @Test
    fun `should fail to register user with short password`() {
        // Given
        val request = CreateUserRequestDto(
            username = "testuser",
            email = "test@example.com",
            password = "12345",
            firstName = "Test",
            lastName = "User"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.password").value("Password must be at least 6 characters long"))
    }

    @Test
    fun `should fail to register user with invalid username format`() {
        // Given
        val request = CreateUserRequestDto(
            username = "ab",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.username").value("Username must be between 3 and 20 characters"))
    }

    @Test
    fun `should fail to register user with blank required fields`() {
        // Given
        val request = CreateUserRequestDto(
            username = "",
            email = "",
            password = "",
            firstName = "",
            lastName = ""
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.username").exists())
            .andExpect(jsonPath("$.fieldErrors.email").exists())
            .andExpect(jsonPath("$.fieldErrors.password").exists())
            .andExpect(jsonPath("$.fieldErrors.firstName").exists())
            .andExpect(jsonPath("$.fieldErrors.lastName").exists())
    }

    @Test
    fun `should fail to register user with invalid phone number`() {
        // Given
        val request = CreateUserRequestDto(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "invalid-phone"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.phoneNumber").value("Phone number must be 10-15 digits and may contain spaces, dashes, and plus sign"))
    }
}