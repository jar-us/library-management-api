package jar.us.librarymanagementsystemapi.repository

import jar.us.librarymanagementsystemapi.domain.Book
import org.springframework.data.jpa.repository.JpaRepository

interface BookRepository : JpaRepository<Book, Long> {
    fun existsByIsbn(isbn: String): Boolean
}