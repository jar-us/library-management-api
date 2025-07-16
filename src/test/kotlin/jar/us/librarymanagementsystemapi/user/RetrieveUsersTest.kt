package jar.us.librarymanagementsystemapi.user

import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import jar.us.librarymanagementsystemapi.domain.model.User
import org.junit.jupiter.api.Test
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

class RetrieveUsersTest : AbstractUserControllerTest() {

    @Test
    fun `should retrieve all users when users exist`() {
        // Given
        val user1 = User.create(
            username = "user1",
            email = "user1@example.com",
            password = "password123",
            firstName = "User",
            lastName = "One"
        )
        val user2 = User.create(
            username = "user2",
            email = "user2@example.com",
            password = "password123",
            firstName = "User",
            lastName = "Two",
            membershipStatus = MembershipStatus.SUSPENDED
        )
        
        userRepository.save(user1)
        userRepository.save(user2)

        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].username").value("user1"))
            .andExpect(jsonPath("$[0].email").value("user1@example.com"))
            .andExpect(jsonPath("$[0].firstName").value("User"))
            .andExpect(jsonPath("$[0].lastName").value("One"))
            .andExpect(jsonPath("$[0].membershipStatus").value("ACTIVE"))
            .andExpect(jsonPath("$[1].username").value("user2"))
            .andExpect(jsonPath("$[1].membershipStatus").value("SUSPENDED"))
    }

    @Test
    fun `should retrieve empty list when no users exist`() {
        // When & Then
        mockMvc.perform(get("/api/users"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(0))
    }

    @Test
    fun `should retrieve user by id when user exists`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            phoneNumber = "1234567890"
        )
        val savedUser = userRepository.save(user)

        // When & Then
        mockMvc.perform(get("/api/users/${savedUser.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.id").value(savedUser.id))
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
    }

    @Test
    fun `should return 404 when retrieving user by non-existent id`() {
        // When & Then
        mockMvc.perform(get("/api/users/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("User with id 999 not found"))
    }

    @Test
    fun `should retrieve user by username when user exists`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        userRepository.save(user)

        // When & Then
        mockMvc.perform(get("/api/users/username/testuser"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"))
    }

    @Test
    fun `should return 404 when retrieving user by non-existent username`() {
        // When & Then
        mockMvc.perform(get("/api/users/username/nonexistent"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("User with username 'nonexistent' not found"))
    }

    @Test
    fun `should retrieve user by email when user exists`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        userRepository.save(user)

        // When & Then
        mockMvc.perform(get("/api/users/email/test@example.com"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("testuser"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
            .andExpect(jsonPath("$.firstName").value("Test"))
            .andExpect(jsonPath("$.lastName").value("User"))
    }

    @Test
    fun `should return 404 when retrieving user by non-existent email`() {
        // When & Then
        mockMvc.perform(get("/api/users/email/nonexistent@example.com"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("User with email 'nonexistent@example.com' not found"))
    }

    @Test
    fun `should search users by name`() {
        // Given
        val user1 = User.create(
            username = "john_doe",
            email = "john@example.com",
            password = "password123",
            firstName = "John",
            lastName = "Doe"
        )
        val user2 = User.create(
            username = "jane_smith",
            email = "jane@example.com",
            password = "password123",
            firstName = "Jane",
            lastName = "Smith"
        )
        val user3 = User.create(
            username = "john_smith",
            email = "johnsmith@example.com",
            password = "password123",
            firstName = "John",
            lastName = "Smith"
        )
        
        userRepository.save(user1)
        userRepository.save(user2)
        userRepository.save(user3)

        // When & Then
        mockMvc.perform(get("/api/users/search?name=John"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.users.length()").value(2))
            .andExpect(jsonPath("$.totalCount").value(2))
            .andExpect(jsonPath("$.users[0].firstName").value("John"))
            .andExpect(jsonPath("$.users[1].firstName").value("John"))
    }

    @Test
    fun `should return empty search results when no users match name`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        userRepository.save(user)

        // When & Then
        mockMvc.perform(get("/api/users/search?name=Nonexistent"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.users.length()").value(0))
            .andExpect(jsonPath("$.totalCount").value(0))
    }

    @Test
    fun `should filter users by membership status`() {
        // Given
        val activeUser = User.create(
            username = "activeuser",
            email = "active@example.com",
            password = "password123",
            firstName = "Active",
            lastName = "User",
            membershipStatus = MembershipStatus.ACTIVE
        )
        val suspendedUser = User.create(
            username = "suspendeduser",
            email = "suspended@example.com",
            password = "password123",
            firstName = "Suspended",
            lastName = "User",
            membershipStatus = MembershipStatus.SUSPENDED
        )
        val expiredUser = User.create(
            username = "expireduser",
            email = "expired@example.com",
            password = "password123",
            firstName = "Expired",
            lastName = "User",
            membershipStatus = MembershipStatus.EXPIRED
        )
        
        userRepository.save(activeUser)
        userRepository.save(suspendedUser)
        userRepository.save(expiredUser)

        // When & Then
        mockMvc.perform(get("/api/users/filter?membershipStatus=ACTIVE"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.users.length()").value(1))
            .andExpect(jsonPath("$.totalCount").value(1))
            .andExpect(jsonPath("$.users[0].membershipStatus").value("ACTIVE"))
            .andExpect(jsonPath("$.users[0].firstName").value("Active"))
    }

    @Test
    fun `should return empty filter results when no users match status`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            membershipStatus = MembershipStatus.ACTIVE
        )
        userRepository.save(user)

        // When & Then
        mockMvc.perform(get("/api/users/filter?membershipStatus=SUSPENDED"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.users.length()").value(0))
            .andExpect(jsonPath("$.totalCount").value(0))
    }
}