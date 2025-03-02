package jar.us.librarymanagementsystemapi

import jakarta.validation.Valid
import jar.us.librarymanagementsystemapi.schema.BookRequest
import jar.us.librarymanagementsystemapi.schema.BookResponse
import jar.us.librarymanagementsystemapi.schema.toEntity
import jar.us.librarymanagementsystemapi.schema.toResponse
import jar.us.librarymanagementsystemapi.service.BookService
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/books")
class BookController(private val bookService: BookService) {

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun addBook(@Valid @RequestBody bookRequest: BookRequest): BookResponse {
        val book = bookRequest.toEntity()
        val savedBook = bookService.addBook(book)
        return savedBook.toResponse()
    }

    // New retrieveAllBooks method
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    fun retrieveAllBooks(): List<BookResponse> {
        val books = bookService.retrieveAllBooks()
        return books.map { it.toResponse() }
    }

    // New method to retrieve a book by ID
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    fun retrieveBookById(@PathVariable id: Long): BookResponse {
        val book = bookService.retrieveBookById(id)
        return book.toResponse()
    }
}