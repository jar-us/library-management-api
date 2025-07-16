package jar.us.librarymanagementsystemapi.domain.repository

import jar.us.librarymanagementsystemapi.domain.model.User
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus

interface UserRepository {
    fun save(user: User): User
    fun findById(id: Long): User?
    fun findAll(): List<User>
    fun findByUsername(username: String): User?
    fun findByEmail(email: String): User?
    fun existsByUsername(username: String): Boolean
    fun existsByEmail(email: String): Boolean
    fun existsById(id: Long): Boolean
    fun deleteById(id: Long)
    fun deleteAll()
    fun findByMembershipStatus(membershipStatus: MembershipStatus): List<User>
    fun findByFirstNameContainingIgnoreCase(firstName: String): List<User>
    fun findByLastNameContainingIgnoreCase(lastName: String): List<User>
    fun findByFullNameContainingIgnoreCase(fullName: String): List<User>
}