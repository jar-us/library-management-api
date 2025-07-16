package jar.us.librarymanagementsystemapi.application.service.impl

import jar.us.librarymanagementsystemapi.application.service.UserManagementService
import jar.us.librarymanagementsystemapi.domain.exception.UserBusinessException
import jar.us.librarymanagementsystemapi.domain.model.User
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import jar.us.librarymanagementsystemapi.domain.repository.UserRepository
import jar.us.librarymanagementsystemapi.presentation.exception.UserNotFoundException
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Service
@Transactional
class UserManagementServiceImpl(
    private val userRepository: UserRepository,
    private val userValidationService: UserValidationService
) : UserManagementService {

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

    override fun authenticateUser(username: String, password: String): User? {
        val user = userRepository.findByUsername(username)
            ?: return null
        
        // In a real application, you would hash the password and compare hashes
        if (user.password != password) {
            return null
        }
        
        if (!user.canBorrowBooks()) {
            throw UserBusinessException("User account is not active or has expired")
        }
        
        // Update last login
        val updatedUser = user.updateLastLogin()
        return userRepository.save(updatedUser)
    }

    @Transactional(readOnly = true)
    override fun findUserById(id: Long): User? {
        return userRepository.findById(id)
    }

    @Transactional(readOnly = true)
    override fun findUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    @Transactional(readOnly = true)
    override fun findUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    @Transactional(readOnly = true)
    override fun findAllUsers(): List<User> {
        return userRepository.findAll()
    }

    override fun updateUser(id: Long, user: User): User {
        val existingUser = userRepository.findById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        // Check if username is being changed and if it's already taken
        if (user.username != existingUser.username && userRepository.existsByUsername(user.username)) {
            throw UserBusinessException("Username '${user.username}' already exists")
        }
        
        // Check if email is being changed and if it's already taken
        if (user.email != existingUser.email && userRepository.existsByEmail(user.email)) {
            throw UserBusinessException("Email '${user.email}' already exists")
        }
        
        val updatedUser = user.copy(
            id = existingUser.id,
            registrationDate = existingUser.registrationDate,
            lastLoginDate = existingUser.lastLoginDate
        )
        
        userValidationService.validateUserUpdate(updatedUser)
        return userRepository.save(updatedUser)
    }

    override fun deleteUser(id: Long): Boolean {
        if (!userRepository.existsById(id)) {
            throw UserNotFoundException("User with id $id not found")
        }
        
        userRepository.deleteById(id)
        return true
    }

    override fun suspendUser(id: Long): User {
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        val suspendedUser = user.suspendMembership()
        return userRepository.save(suspendedUser)
    }

    override fun activateUser(id: Long): User {
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        val activatedUser = user.activateMembership()
        return userRepository.save(activatedUser)
    }

    override fun updateUserProfile(id: Long, firstName: String?, lastName: String?, phoneNumber: String?, email: String?): User {
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        // Check if email is being changed and if it's already taken
        if (email != null && email != user.email && userRepository.existsByEmail(email)) {
            throw UserBusinessException("Email '$email' already exists")
        }
        
        val updatedUser = user.updateProfile(firstName, lastName, phoneNumber, email)
        return userRepository.save(updatedUser)
    }

    override fun changePassword(id: Long, oldPassword: String, newPassword: String): User {
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        // In a real application, you would hash passwords and compare hashes
        if (user.password != oldPassword) {
            throw UserBusinessException("Current password is incorrect")
        }
        
        if (newPassword.length < 6) {
            throw UserBusinessException("New password must be at least 6 characters long")
        }
        
        val updatedUser = user.copy(password = newPassword)
        return userRepository.save(updatedUser)
    }

    @Transactional(readOnly = true)
    override fun searchUsersByName(name: String): List<User> {
        return userRepository.findByFullNameContainingIgnoreCase(name)
    }

    @Transactional(readOnly = true)
    override fun filterUsersByMembershipStatus(membershipStatus: MembershipStatus): List<User> {
        return userRepository.findByMembershipStatus(membershipStatus)
    }

    override fun updateLastLogin(id: Long): User {
        val user = userRepository.findById(id)
            ?: throw UserNotFoundException("User with id $id not found")
        
        val updatedUser = user.updateLastLogin()
        return userRepository.save(updatedUser)
    }
}