package jar.us.librarymanagementsystemapi.application.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import java.time.LocalDateTime

data class CreateUserRequestDto(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Username can only contain letters, numbers, and underscores"
    )
    val username: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "Password is required")
    @field:Size(min = 6, message = "Password must be at least 6 characters long")
    val password: String,

    @field:NotBlank(message = "First name is required")
    @field:Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    val lastName: String,

    @field:Pattern(
        regexp = "^[+]?[0-9\\s\\-]{10,15}$",
        message = "Phone number must be 10-15 digits and may contain spaces, dashes, and plus sign"
    )
    val phoneNumber: String? = null,

    val membershipStatus: MembershipStatus = MembershipStatus.ACTIVE,

    val membershipExpiryDate: LocalDateTime? = null
)

data class UpdateUserRequestDto(
    @field:NotBlank(message = "Username is required")
    @field:Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z0-9_]+$",
        message = "Username can only contain letters, numbers, and underscores"
    )
    val username: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    val email: String,

    @field:NotBlank(message = "First name is required")
    @field:Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @field:Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    val lastName: String,

    @field:Pattern(
        regexp = "^[+]?[0-9\\s\\-]{10,15}$",
        message = "Phone number must be 10-15 digits and may contain spaces, dashes, and plus sign"
    )
    val phoneNumber: String? = null,

    @field:NotNull(message = "Membership status is required")
    val membershipStatus: MembershipStatus,

    val membershipExpiryDate: LocalDateTime? = null
)

data class UpdateUserProfileRequestDto(
    @field:Size(min = 1, max = 50, message = "First name must be between 1 and 50 characters")
    val firstName: String? = null,

    @field:Size(min = 1, max = 50, message = "Last name must be between 1 and 50 characters")
    val lastName: String? = null,

    @field:Pattern(
        regexp = "^[+]?[0-9\\s\\-]{10,15}$",
        message = "Phone number must be 10-15 digits and may contain spaces, dashes, and plus sign"
    )
    val phoneNumber: String? = null,

    @field:Email(message = "Email must be valid")
    val email: String? = null
)

data class ChangePasswordRequestDto(
    @field:NotBlank(message = "Current password is required")
    val currentPassword: String,

    @field:NotBlank(message = "New password is required")
    @field:Size(min = 6, message = "New password must be at least 6 characters long")
    val newPassword: String
)

data class LoginRequestDto(
    @field:NotBlank(message = "Username is required")
    val username: String,

    @field:NotBlank(message = "Password is required")
    val password: String
)