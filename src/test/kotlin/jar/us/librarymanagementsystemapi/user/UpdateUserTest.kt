package jar.us.librarymanagementsystemapi.user

import jar.us.librarymanagementsystemapi.application.dto.ChangePasswordRequestDto
import jar.us.librarymanagementsystemapi.application.dto.UpdateUserProfileRequestDto
import jar.us.librarymanagementsystemapi.application.dto.UpdateUserRequestDto
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import jar.us.librarymanagementsystemapi.domain.model.User
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import java.time.LocalDateTime

class UpdateUserTest : AbstractUserControllerTest() {

    @Test
    fun `should update user with valid data`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        val savedUser = userRepository.save(user)

        val updateRequest = UpdateUserRequestDto(
            username = "updateduser",
            email = "updated@example.com",
            firstName = "Updated",
            lastName = "User",
            phoneNumber = "9876543210",
            membershipStatus = MembershipStatus.ACTIVE
        )

        // When & Then
        mockMvc.perform(
            put("/api/users/${savedUser.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("updateduser"))
            .andExpect(jsonPath("$.email").value("updated@example.com"))
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.lastName").value("User"))
            .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
            .andExpect(jsonPath("$.membershipStatus").value("ACTIVE"))
    }

    @Test
    fun `should return 404 when updating non-existent user`() {
        // Given
        val updateRequest = UpdateUserRequestDto(
            username = "updateduser",
            email = "updated@example.com",
            firstName = "Updated",
            lastName = "User",
            membershipStatus = MembershipStatus.ACTIVE
        )

        // When & Then
        mockMvc.perform(
            put("/api/users/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("User with id 999 not found"))
    }

    @Test
    fun `should fail to update user with duplicate username`() {
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
            lastName = "Two"
        )
        val savedUser1 = userRepository.save(user1)
        userRepository.save(user2)

        val updateRequest = UpdateUserRequestDto(
            username = "user2",
            email = "user1@example.com",
            firstName = "User",
            lastName = "One",
            membershipStatus = MembershipStatus.ACTIVE
        )

        // When & Then
        mockMvc.perform(
            put("/api/users/${savedUser1.id}")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Username 'user2' already exists"))
    }

    @Test
    fun `should update user profile with partial data`() {
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

        val profileUpdate = UpdateUserProfileRequestDto(
            firstName = "Updated",
            phoneNumber = "9876543210"
        )

        // When & Then
        mockMvc.perform(
            patch("/api/users/${savedUser.id}/profile")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(profileUpdate))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.firstName").value("Updated"))
            .andExpect(jsonPath("$.lastName").value("User"))
            .andExpect(jsonPath("$.phoneNumber").value("9876543210"))
            .andExpect(jsonPath("$.email").value("test@example.com"))
    }

    @Test
    fun `should change user password with valid current password`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "oldpassword",
            firstName = "Test",
            lastName = "User"
        )
        val savedUser = userRepository.save(user)

        val passwordChange = ChangePasswordRequestDto(
            currentPassword = "oldpassword",
            newPassword = "newpassword123"
        )

        // When & Then
        mockMvc.perform(
            patch("/api/users/${savedUser.id}/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordChange))
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.username").value("testuser"))
    }

    @Test
    fun `should fail to change password with incorrect current password`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "correctpassword",
            firstName = "Test",
            lastName = "User"
        )
        val savedUser = userRepository.save(user)

        val passwordChange = ChangePasswordRequestDto(
            currentPassword = "wrongpassword",
            newPassword = "newpassword123"
        )

        // When & Then
        mockMvc.perform(
            patch("/api/users/${savedUser.id}/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordChange))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("Current password is incorrect"))
    }

    @Test
    fun `should suspend user`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            membershipStatus = MembershipStatus.ACTIVE
        )
        val savedUser = userRepository.save(user)

        // When & Then
        mockMvc.perform(patch("/api/users/${savedUser.id}/suspend"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.membershipStatus").value("SUSPENDED"))
            .andExpect(jsonPath("$.isActive").value(false))
            .andExpect(jsonPath("$.canBorrowBooks").value(false))
    }

    @Test
    fun `should activate user`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            membershipStatus = MembershipStatus.SUSPENDED
        )
        val savedUser = userRepository.save(user)

        // When & Then
        mockMvc.perform(patch("/api/users/${savedUser.id}/activate"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.membershipStatus").value("ACTIVE"))
            .andExpect(jsonPath("$.isActive").value(true))
            .andExpect(jsonPath("$.canBorrowBooks").value(true))
    }

    @Test
    fun `should delete user`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User"
        )
        val savedUser = userRepository.save(user)

        // When & Then
        mockMvc.perform(delete("/api/users/${savedUser.id}"))
            .andExpect(status().isNoContent)

        // Verify user is deleted
        val deletedUser = userRepository.findById(savedUser.id!!)
        assert(deletedUser == null)
    }

    @Test
    fun `should return 404 when deleting non-existent user`() {
        // When & Then
        mockMvc.perform(delete("/api/users/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("User with id 999 not found"))
    }

    @Test
    fun `should fail to change password with short new password`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "oldpassword",
            firstName = "Test",
            lastName = "User"
        )
        val savedUser = userRepository.save(user)

        val passwordChange = ChangePasswordRequestDto(
            currentPassword = "oldpassword",
            newPassword = "short"
        )

        // When & Then
        mockMvc.perform(
            patch("/api/users/${savedUser.id}/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(passwordChange))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.newPassword").value("New password must be at least 6 characters long"))
    }

    @Test
    fun `should fail to suspend already suspended user`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            membershipStatus = MembershipStatus.SUSPENDED
        )
        val savedUser = userRepository.save(user)

        // When & Then
        mockMvc.perform(patch("/api/users/${savedUser.id}/suspend"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("User 'testuser' is already suspended"))
    }

    @Test
    fun `should fail to activate already active user`() {
        // Given
        val user = User.create(
            username = "testuser",
            email = "test@example.com",
            password = "password123",
            firstName = "Test",
            lastName = "User",
            membershipStatus = MembershipStatus.ACTIVE
        )
        val savedUser = userRepository.save(user)

        // When & Then
        mockMvc.perform(patch("/api/users/${savedUser.id}/activate"))
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("User 'testuser' is already active"))
    }
}