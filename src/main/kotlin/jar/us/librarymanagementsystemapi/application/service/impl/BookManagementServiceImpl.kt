package jar.us.librarymanagementsystemapi.application.service.impl

import jar.us.librarymanagementsystemapi.application.service.BookManagementService
import jar.us.librarymanagementsystemapi.domain.exception.BookBusinessException
import jar.us.librarymanagementsystemapi.domain.model.Book
import jar.us.librarymanagementsystemapi.domain.repository.BookRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class BookManagementServiceImpl(
    private val bookRepository: BookRepository,
    private val bookValidationService: BookValidationService
) : BookManagementService {

    override fun createBook(book: Book): Book {
        bookValidationService.validateForCreation(book)
        
        if (bookRepository.existsByIsbn(book.isbn)) {
            throw BookBusinessException("Book with ISBN '${book.isbn}' already exists")
        }
        
        return bookRepository.save(book)
    }

    @Transactional(readOnly = true)
    override fun findBookById(id: Long): Book? {
        return bookRepository.findById(id)
    }

    @Transactional(readOnly = true)
    override fun findAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

    override fun updateBook(id: Long, book: Book): Book {
        val existingBook = bookRepository.findById(id)
            ?: throw BookBusinessException("Book with ID '$id' not found")

        bookValidationService.validateForUpdate(book, existingBook)
        
        // Check if ISBN is being changed to one that already exists
        if (book.isbn != existingBook.isbn && bookRepository.existsByIsbn(book.isbn)) {
            throw BookBusinessException("Book with ISBN '${book.isbn}' already exists")
        }
        
        val updatedBook = book.copy(id = id)
        return bookRepository.save(updatedBook)
    }

    override fun deleteBook(id: Long): Boolean {
        return if (bookRepository.existsById(id)) {
            bookRepository.deleteById(id)
            true
        } else {
            false
        }
    }

    @Transactional(readOnly = true)
    override fun searchBooksByTitle(title: String): List<Book> {
        if (title.isBlank()) {
            throw BookBusinessException("Search title cannot be empty")
        }
        return bookRepository.findByTitleContainingIgnoreCase(title.trim())
    }

    @Transactional(readOnly = true)
    override fun searchBooksByAuthor(author: String): List<Book> {
        if (author.isBlank()) {
            throw BookBusinessException("Search author cannot be empty")
        }
        return bookRepository.findByAuthorContainingIgnoreCase(author.trim())
    }

    @Transactional(readOnly = true)
    override fun filterBooksByGenre(genre: String): List<Book> {
        if (genre.isBlank()) {
            throw BookBusinessException("Genre filter cannot be empty")
        }
        return bookRepository.findByGenre(genre.trim())
    }
}