package jar.us.librarymanagementsystemapi.application.service

import jar.us.librarymanagementsystemapi.domain.model.User
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus

interface UserManagementService {
    fun registerUser(user: User): User
    fun authenticateUser(username: String, password: String): User?
    fun findUserById(id: Long): User?
    fun findUserByUsername(username: String): User?
    fun findUserByEmail(email: String): User?
    fun findAllUsers(): List<User>
    fun updateUser(id: Long, user: User): User
    fun deleteUser(id: Long): Boolean
    fun suspendUser(id: Long): User
    fun activateUser(id: Long): User
    fun updateUserProfile(id: Long, firstName: String?, lastName: String?, phoneNumber: String?, email: String?): User
    fun changePassword(id: Long, oldPassword: String, newPassword: String): User
    fun searchUsersByName(name: String): List<User>
    fun filterUsersByMembershipStatus(membershipStatus: MembershipStatus): List<User>
    fun updateLastLogin(id: Long): User
}