package jar.us.librarymanagementsystemapi.infrastructure.persistence

import jar.us.librarymanagementsystemapi.domain.model.User
import jar.us.librarymanagementsystemapi.domain.model.MembershipStatus
import jar.us.librarymanagementsystemapi.domain.repository.UserRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaUserRepository : JpaRepository<User, Long>, UserRepository {
    
    override fun findByUsername(username: String): User?
    
    override fun findByEmail(email: String): User?
    
    override fun existsByUsername(username: String): Boolean
    
    override fun existsByEmail(email: String): Boolean
    
    override fun findByMembershipStatus(membershipStatus: MembershipStatus): List<User>
    
    override fun findByFirstNameContainingIgnoreCase(firstName: String): List<User>
    
    override fun findByLastNameContainingIgnoreCase(lastName: String): List<User>
    
    @Query("SELECT u FROM User u WHERE CONCAT(u.firstName, ' ', u.lastName) LIKE %:fullName%")
    override fun findByFullNameContainingIgnoreCase(@Param("fullName") fullName: String): List<User>
}