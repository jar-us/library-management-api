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
}