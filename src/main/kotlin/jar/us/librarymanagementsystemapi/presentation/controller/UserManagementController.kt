package jar.us.librarymanagementsystemapi.presentation.controller

import jakarta.validation.Valid
import jar.us.librarymanagementsystemapi.application.dto.*
import jar.us.librarymanagementsystemapi.application.mapper.UserMapper
import jar.us.librarymanagementsystemapi.application.service.UserManagementService
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import jar.us.librarymanagementsystemapi.presentation.exception.UserNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/users")
class UserManagementController(
    private val userManagementService: UserManagementService,
    private val userMapper: UserMapper
) {

    @PostMapping("/register")
    fun registerUser(@Valid @RequestBody request: CreateUserRequestDto): ResponseEntity<UserResponseDto> {
        val user = userMapper.toUser(request)
        val createdUser = userManagementService.registerUser(user)
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(userMapper.toUserResponseDto(createdUser))
    }

    @PostMapping("/login")
    fun authenticateUser(@Valid @RequestBody request: LoginRequestDto): ResponseEntity<LoginResponseDto> {
        val user = userManagementService.authenticateUser(request.username, request.password)
            ?: return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build()
        
        return ResponseEntity.ok(userMapper.toLoginResponseDto(user))
    }

    @GetMapping
    fun getAllUsers(): ResponseEntity<List<UserResponseDto>> {
        val users = userManagementService.findAllUsers()
        return ResponseEntity.ok(userMapper.toUserResponseDtoList(users))
    }

    @GetMapping("/{id}")
    fun getUserById(@PathVariable id: Long): ResponseEntity<UserResponseDto> {
        val user = userManagementService.findUserById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        return ResponseEntity.ok(userMapper.toUserResponseDto(user))
    }

    @GetMapping("/username/{username}")
    fun getUserByUsername(@PathVariable username: String): ResponseEntity<UserResponseDto> {
        val user = userManagementService.findUserByUsername(username)
            ?: throw UserNotFoundException("User with username '$username' not found")
        
        return ResponseEntity.ok(userMapper.toUserResponseDto(user))
    }

    @GetMapping("/email/{email}")
    fun getUserByEmail(@PathVariable email: String): ResponseEntity<UserResponseDto> {
        val user = userManagementService.findUserByEmail(email)
            ?: throw UserNotFoundException("User with email '$email' not found")
        
        return ResponseEntity.ok(userMapper.toUserResponseDto(user))
    }

    @PutMapping("/{id}")
    fun updateUser(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserRequestDto
    ): ResponseEntity<UserResponseDto> {
        val existingUser = userManagementService.findUserById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        val updatedUser = userMapper.toUser(request, existingUser)
        val result = userManagementService.updateUser(id, updatedUser)
        
        return ResponseEntity.ok(userMapper.toUserResponseDto(result))
    }

    @PatchMapping("/{id}/profile")
    fun updateUserProfile(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateUserProfileRequestDto
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userManagementService.updateUserProfile(
            id, 
            request.firstName, 
            request.lastName, 
            request.phoneNumber, 
            request.email
        )
        
        return ResponseEntity.ok(userMapper.toUserResponseDto(updatedUser))
    }

    @PatchMapping("/{id}/password")
    fun changePassword(
        @PathVariable id: Long,
        @Valid @RequestBody request: ChangePasswordRequestDto
    ): ResponseEntity<UserResponseDto> {
        val updatedUser = userManagementService.changePassword(
            id, 
            request.currentPassword, 
            request.newPassword
        )
        
        return ResponseEntity.ok(userMapper.toUserResponseDto(updatedUser))
    }

    @PatchMapping("/{id}/suspend")
    fun suspendUser(@PathVariable id: Long): ResponseEntity<UserResponseDto> {
        val suspendedUser = userManagementService.suspendUser(id)
        return ResponseEntity.ok(userMapper.toUserResponseDto(suspendedUser))
    }

    @PatchMapping("/{id}/activate")
    fun activateUser(@PathVariable id: Long): ResponseEntity<UserResponseDto> {
        val activatedUser = userManagementService.activateUser(id)
        return ResponseEntity.ok(userMapper.toUserResponseDto(activatedUser))
    }

    @DeleteMapping("/{id}")
    fun deleteUser(@PathVariable id: Long): ResponseEntity<Void> {
        userManagementService.deleteUser(id)
        return ResponseEntity.noContent().build()
    }

    @GetMapping("/search")
    fun searchUsers(@RequestParam name: String): ResponseEntity<UserSearchResultDto> {
        val users = userManagementService.searchUsersByName(name)
        return ResponseEntity.ok(userMapper.toUserSearchResultDto(users))
    }

    @GetMapping("/filter")
    fun filterUsers(@RequestParam membershipStatus: MembershipStatus): ResponseEntity<UserSearchResultDto> {
        val users = userManagementService.filterUsersByMembershipStatus(membershipStatus)
        return ResponseEntity.ok(userMapper.toUserSearchResultDto(users))
    }
}