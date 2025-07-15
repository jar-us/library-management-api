package jar.us.librarymanagementsystemapi.infrastructure.persistence

import jar.us.librarymanagementsystemapi.domain.model.Book
import jar.us.librarymanagementsystemapi.domain.repository.BookRepository
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface JpaBookRepository : JpaRepository<Book, Long> {
    fun existsByIsbn(isbn: String): Boolean
    fun findByIsbn(isbn: String): Book?
    fun findByTitleContainingIgnoreCase(title: String): List<Book>
    fun findByAuthorContainingIgnoreCase(author: String): List<Book>
    fun findByGenre(genre: String): List<Book>
    
    @Query("SELECT b FROM Book b WHERE b.availableCopies > 0")
    fun findAvailableBooks(): List<Book>
}

@Repository
class BookRepositoryImpl(
    private val jpaBookRepository: JpaBookRepository
) : BookRepository {

    override fun save(book: Book): Book = jpaBookRepository.save(book)

    override fun findById(id: Long): Book? = jpaBookRepository.findById(id).orElse(null)

    override fun findAll(): List<Book> = jpaBookRepository.findAll()

    override fun existsByIsbn(isbn: String): Boolean = jpaBookRepository.existsByIsbn(isbn)

    override fun deleteById(id: Long) = jpaBookRepository.deleteById(id)

    override fun findByIsbn(isbn: String): Book? = jpaBookRepository.findByIsbn(isbn)

    override fun findByTitleContainingIgnoreCase(title: String): List<Book> = 
        jpaBookRepository.findByTitleContainingIgnoreCase(title)

    override fun findByAuthorContainingIgnoreCase(author: String): List<Book> = 
        jpaBookRepository.findByAuthorContainingIgnoreCase(author)

    override fun findByGenre(genre: String): List<Book> = jpaBookRepository.findByGenre(genre)

    override fun deleteAll() = jpaBookRepository.deleteAll()

    override fun saveAll(books: List<Book>): List<Book> = jpaBookRepository.saveAll(books)
}