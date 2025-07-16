package jar.us.librarymanagementsystemapi.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jar.us.librarymanagementsystemapi.domain.exception.UserBusinessException
import java.time.LocalDateTime

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank(message = "Username is required")
    @Column(nullable = false, unique = true)
    val username: String,

    @field:NotBlank(message = "Email is required")
    @field:Email(message = "Email must be valid")
    @Column(nullable = false, unique = true)
    val email: String,

    @field:NotBlank(message = "Password is required")
    @Column(nullable = false)
    val password: String,

    @field:NotBlank(message = "First name is required")
    @Column(nullable = false)
    val firstName: String,

    @field:NotBlank(message = "Last name is required")
    @Column(nullable = false)
    val lastName: String,

    val phoneNumber: String? = null,

    @field:NotNull(message = "Membership status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val membershipStatus: MembershipStatus = MembershipStatus.ACTIVE,

    @field:NotNull(message = "Registration date is required")
    @Column(nullable = false)
    val registrationDate: LocalDateTime = LocalDateTime.now(),

    val lastLoginDate: LocalDateTime? = null,

    val membershipExpiryDate: LocalDateTime? = null
) {
    init {
        validateUser()
    }

    val fullName: String
        get() = "$firstName $lastName"

    fun isActive(): Boolean = membershipStatus == MembershipStatus.ACTIVE

    fun isSuspended(): Boolean = membershipStatus == MembershipStatus.SUSPENDED

    fun isExpired(): Boolean = membershipStatus == MembershipStatus.EXPIRED

    fun canBorrowBooks(): Boolean = isActive() && !isMembershipExpired()

    fun isMembershipExpired(): Boolean = 
        membershipExpiryDate?.isBefore(LocalDateTime.now()) == true

    fun updateLastLogin(): User = this.copy(lastLoginDate = LocalDateTime.now())

    fun suspendMembership(): User {
        if (membershipStatus == MembershipStatus.SUSPENDED) {
            throw UserBusinessException("User '$username' is already suspended")
        }
        return this.copy(membershipStatus = MembershipStatus.SUSPENDED)
    }

    fun activateMembership(): User {
        if (membershipStatus == MembershipStatus.ACTIVE) {
            throw UserBusinessException("User '$username' is already active")
        }
        return this.copy(membershipStatus = MembershipStatus.ACTIVE)
    }

    fun updateProfile(
        firstName: String? = null,
        lastName: String? = null,
        phoneNumber: String? = null,
        email: String? = null
    ): User {
        return this.copy(
            firstName = firstName?.trim() ?: this.firstName,
            lastName = lastName?.trim() ?: this.lastName,
            phoneNumber = phoneNumber?.trim(),
            email = email?.trim() ?: this.email
        )
    }

    private fun validateUser() {
        if (username.isBlank()) {
            throw UserBusinessException("Username cannot be blank")
        }
        if (firstName.isBlank()) {
            throw UserBusinessException("First name cannot be blank")
        }
        if (lastName.isBlank()) {
            throw UserBusinessException("Last name cannot be blank")
        }
        if (password.length < MIN_PASSWORD_LENGTH) {
            throw UserBusinessException("Password must be at least $MIN_PASSWORD_LENGTH characters long")
        }
        // Only validate expiry date if this is a new user creation (no ID)
        if (id == null && membershipExpiryDate?.isBefore(registrationDate) == true) {
            throw UserBusinessException("Membership expiry date cannot be before registration date")
        }
    }

    companion object {
        private const val MIN_PASSWORD_LENGTH = 6

        fun create(
            username: String,
            email: String,
            password: String,
            firstName: String,
            lastName: String,
            phoneNumber: String? = null,
            membershipStatus: MembershipStatus = MembershipStatus.ACTIVE,
            membershipExpiryDate: LocalDateTime? = null
        ): User {
            // Validate email format
            val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
            if (!emailRegex.matches(email.trim())) {
                throw UserBusinessException("Invalid email format")
            }

            // Validate username format
            val usernameRegex = "^[a-zA-Z0-9_]{3,20}$".toRegex()
            if (!usernameRegex.matches(username.trim())) {
                throw UserBusinessException("Username must be 3-20 characters long and contain only letters, numbers, and underscores")
            }

            return User(
                username = username.trim(),
                email = email.trim().lowercase(),
                password = password, // In real app, this would be hashed
                firstName = firstName.trim(),
                lastName = lastName.trim(),
                phoneNumber = phoneNumber?.trim(),
                membershipStatus = membershipStatus,
                membershipExpiryDate = membershipExpiryDate
            )
        }
    }
}

enum class MembershipStatus {
    ACTIVE,
    SUSPENDED,
    EXPIRED
}