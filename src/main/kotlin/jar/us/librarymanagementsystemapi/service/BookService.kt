package jar.us.librarymanagementsystemapi.service


import jar.us.librarymanagementsystemapi.domain.Book
import jar.us.librarymanagementsystemapi.repository.BookRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.web.server.ResponseStatusException

@Service
class BookService(private val bookRepository: BookRepository) {

    fun addBook(book: Book): Book {
        if (bookRepository.existsByIsbn(book.isbn)) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "ISBN '${book.isbn}' already exists")
        }
        return bookRepository.save(book)
    }

    fun retrieveAllBooks(): List<Book> {
        return bookRepository.findAll()
    }

}