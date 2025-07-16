package jar.us.librarymanagementsystemapi.user

import jar.us.librarymanagementsystemapi.application.dto.LoginRequestDto
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import jar.us.librarymanagementsystemapi.domain.model.User
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

class AuthenticateUserTest : AbstractUserControllerTest() {

    @Test
    fun `should authenticate user with valid credentials`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        userRepository.save(user)

        val loginRequest = LoginRequestDto(
            username = "testuser",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.user.username").value("testuser"))
            .andExpect(jsonPath("$.user.email").value("test@example.com"))
            .andExpect(jsonPath("$.user.firstName").value("Test"))
            .andExpect(jsonPath("$.user.lastName").value("User"))
            .andExpect(jsonPath("$.user.isActive").value(true))
            .andExpect(jsonPath("$.user.canBorrowBooks").value(true))
            .andExpect(jsonPath("$.user.lastLoginDate").exists())
            .andExpect(jsonPath("$.message").value("Login successful"))
    }

    @Test
    fun `should fail to authenticate user with invalid username`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        userRepository.save(user)

        val loginRequest = LoginRequestDto(
            username = "nonexistent",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should fail to authenticate user with invalid password`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        userRepository.save(user)

        val loginRequest = LoginRequestDto(
            username = "testuser",
            password = "wrongpassword"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isUnauthorized)
    }

    @Test
    fun `should fail to authenticate suspended user`() {
        // Given
        val user = User.create(
            username = "suspendeduser",
            email = "suspended@example.com",
            password = "password123",
            firstName = "Suspended",
            lastName = "User",
            membershipStatus = MembershipStatus.SUSPENDED
        )
        userRepository.save(user)

        val loginRequest = LoginRequestDto(
            username = "suspendeduser",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("User account is not active or has expired"))
    }

    @Test
    fun `should fail to authenticate expired user`() {
        // Given - Create a user with membership that expires in the past
        // For testing, we'll use a user with EXPIRED status instead of dealing with date validation
        val user = User.create(
            username = "expireduser",
            email = "expired@example.com",
            password = "password123",
            firstName = "Expired",
            lastName = "User",
            membershipStatus = MembershipStatus.EXPIRED
        )
        userRepository.save(user)

        val loginRequest = LoginRequestDto(
            username = "expireduser",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("User account is not active or has expired"))
    }

    @Test
    fun `should fail to authenticate with blank username`() {
        // Given
        val loginRequest = LoginRequestDto(
            username = "",
            password = "password123"
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.username").value("Username is required"))
    }

    @Test
    fun `should fail to authenticate with blank password`() {
        // Given
        val loginRequest = LoginRequestDto(
            username = "testuser",
            password = ""
        )

        // When & Then
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.password").value("Password is required"))
    }

    @Test
    fun `should update last login date after successful authentication`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        val savedUser = userRepository.save(user)

        val loginRequest = LoginRequestDto(
            username = "testuser",
            password = "password123"
        )

        // When
        mockMvc.perform(
            post("/api/users/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest))
        )
            .andExpect(status().isOk)

        // Then
        val updatedUser = userRepository.findById(savedUser.id!!)
        assert(updatedUser?.lastLoginDate != null)
        assert(updatedUser?.lastLoginDate?.isAfter(savedUser.registrationDate) == true)
    }
}