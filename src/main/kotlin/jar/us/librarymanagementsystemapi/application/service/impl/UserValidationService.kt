package jar.us.librarymanagementsystemapi.application.service.impl

import jar.us.librarymanagementsystemapi.domain.exception.UserBusinessException
import jar.us.librarymanagementsystemapi.domain.model.User
import org.springframework.stereotype.Service

@Service
class UserValidationService {

    fun validateNewUser(user: User) {
        validateUserCommon(user)
        
        if (user.id != null) {
            throw UserBusinessException("New user should not have an ID")
        }
    }

    fun validateUserUpdate(user: User) {
        validateUserCommon(user)
        
        if (user.id == null) {
            throw UserBusinessException("User update requires an ID")
        }
    }

    private fun validateUserCommon(user: User) {
        if (user.username.isBlank()) {
            throw UserBusinessException("Username is required")
        }
        
        if (user.email.isBlank()) {
            throw UserBusinessException("Email is required")
        }
        
        if (user.firstName.isBlank()) {
            throw UserBusinessException("First name is required")
        }
        
        if (user.lastName.isBlank()) {
            throw UserBusinessException("Last name is required")
        }
        
        if (user.password.isBlank()) {
            throw UserBusinessException("Password is required")
        }
        
        // Validate email format
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
        if (!emailRegex.matches(user.email)) {
            throw UserBusinessException("Invalid email format")
        }
        
        // Validate username format
        val usernameRegex = "^[a-zA-Z0-9_]{3,20}$".toRegex()
        if (!usernameRegex.matches(user.username)) {
            throw UserBusinessException("Username must be 3-20 characters long and contain only letters, numbers, and underscores")
        }
        
        // Validate password strength
        if (user.password.length < 6) {
            throw UserBusinessException("Password must be at least 6 characters long")
        }
        
        // Validate phone number format if provided
        user.phoneNumber?.let { phone ->
            if (phone.isNotBlank()) {
                val phoneRegex = "^[+]?[0-9]{10,15}$".toRegex()
                if (!phoneRegex.matches(phone.replace("-", "").replace(" ", ""))) {
                    throw UserBusinessException("Invalid phone number format")
                }
            }
        }
    }
}