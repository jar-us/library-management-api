package jar.us.librarymanagementsystemapi.application.mapper

import jar.us.librarymanagementsystemapi.application.dto.*
import jar.us.librarymanagementsystemapi.domain.model.User
import org.springframework.stereotype.Component

@Component
class UserMapper {

    fun toUser(dto: CreateUserRequestDto): User {
        return User.create(
            username = dto.username,
            email = dto.email,
            password = dto.password,
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            membershipStatus = dto.membershipStatus,
            membershipExpiryDate = dto.membershipExpiryDate
        )
    }

    fun toUser(dto: UpdateUserRequestDto, existingUser: User): User {
        return existingUser.copy(
            username = dto.username,
            email = dto.email,
            firstName = dto.firstName,
            lastName = dto.lastName,
            phoneNumber = dto.phoneNumber,
            membershipStatus = dto.membershipStatus,
            membershipExpiryDate = dto.membershipExpiryDate
        )
    }

    fun toUserResponseDto(user: User): UserResponseDto {
        return UserResponseDto(
            id = user.id!!,
            username = user.username,
            email = user.email,
            firstName = user.firstName,
            lastName = user.lastName,
            fullName = user.fullName,
            phoneNumber = user.phoneNumber,
            membershipStatus = user.membershipStatus,
            registrationDate = user.registrationDate,
            lastLoginDate = user.lastLoginDate,
            membershipExpiryDate = user.membershipExpiryDate,
            isActive = user.isActive(),
            canBorrowBooks = user.canBorrowBooks(),
            isMembershipExpired = user.isMembershipExpired()
        )
    }

    fun toUserResponseDtoList(users: List<User>): List<UserResponseDto> {
        return users.map { toUserResponseDto(it) }
    }

    fun toLoginResponseDto(user: User): LoginResponseDto {
        return LoginResponseDto(
            user = toUserResponseDto(user),
            message = "Login successful"
        )
    }

    fun toUserSearchResultDto(users: List<User>): UserSearchResultDto {
        return UserSearchResultDto(
            users = toUserResponseDtoList(users),
            totalCount = users.size
        )
    }
}