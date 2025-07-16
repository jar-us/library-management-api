package jar.us.librarymanagementsystemapi.application.dto

import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import java.time.LocalDateTime

data class UserResponseDto(
    val id: Long,
    val username: String,
    val email: String,
    val firstName: String,
    val lastName: String,
    val fullName: String,
    val phoneNumber: String?,
    val membershipStatus: MembershipStatus,
    val registrationDate: LocalDateTime,
    val lastLoginDate: LocalDateTime?,
    val membershipExpiryDate: LocalDateTime?,
    val isActive: Boolean,
    val canBorrowBooks: Boolean,
    val isMembershipExpired: Boolean
)

data class LoginResponseDto(
    val user: UserResponseDto,
    val message: String = "Login successful"
)

data class UserSearchResultDto(
    val users: List<UserResponseDto>,
    val totalCount: Int
)