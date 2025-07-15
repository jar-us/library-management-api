package jar.us.librarymanagementsystemapi.presentation.controller

import jakarta.validation.Valid
import jar.us.librarymanagementsystemapi.application.dto.*
import jar.us.librarymanagementsystemapi.application.mapper.BookMapper
import jar.us.librarymanagementsystemapi.application.service.BookManagementService
import jar.us.librarymanagementsystemapi.presentation.exception.BookNotFoundException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/books")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080"])
class BookManagementController(
    private val bookManagementService: BookManagementService,
    private val bookMapper: BookMapper
) {

    @PostMapping
    fun createBook(@Valid @RequestBody request: CreateBookRequestDto): ResponseEntity<BookResponseDto> {
        val book = bookMapper.toEntity(request)
        val createdBook = bookManagementService.createBook(book)
        val response = bookMapper.toResponseDto(createdBook)
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response)
    }

    @GetMapping("/{id}")
    fun getBookById(@PathVariable id: Long): ResponseEntity<BookResponseDto> {
        val book = bookManagementService.findBookById(id)
            ?: throw BookNotFoundException("Book with ID $id not found")
        
        val response = bookMapper.toResponseDto(book)
        return ResponseEntity.ok(response)
    }

    @GetMapping
    fun getAllBooks(): ResponseEntity<List<BookResponseDto>> {
        val books = bookManagementService.findAllBooks()
        val response = bookMapper.toResponseDtoList(books)
        
        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    fun updateBook(
        @PathVariable id: Long,
        @Valid @RequestBody request: UpdateBookRequestDto
    ): ResponseEntity<BookResponseDto> {
        val book = bookMapper.toEntity(request, id)
        val updatedBook = bookManagementService.updateBook(id, book)
        val response = bookMapper.toResponseDto(updatedBook)
        
        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    fun deleteBook(@PathVariable id: Long): ResponseEntity<Void> {
        val deleted = bookManagementService.deleteBook(id)
        
        return if (deleted) {
            ResponseEntity.noContent().build()
        } else {
            ResponseEntity.notFound().build()
        }
    }
}

@RestController
@RequestMapping("/api/v1/books/search")
@CrossOrigin(origins = ["http://localhost:3000", "http://localhost:8080"])
class BookSearchController(
    private val bookManagementService: BookManagementService,
    private val bookMapper: BookMapper
) {

    @GetMapping("/by-title")
    fun searchBooksByTitle(
        @RequestParam title: String
    ): ResponseEntity<BookSearchResultDto> {
        val books = bookManagementService.searchBooksByTitle(title)
        val response = BookSearchResultDto(
            books = bookMapper.toResponseDtoList(books),
            totalCount = books.size,
            searchTerm = title
        )
        
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-author")
    fun searchBooksByAuthor(
        @RequestParam author: String
    ): ResponseEntity<BookSearchResultDto> {
        val books = bookManagementService.searchBooksByAuthor(author)
        val response = BookSearchResultDto(
            books = bookMapper.toResponseDtoList(books),
            totalCount = books.size,
            searchTerm = author
        )
        
        return ResponseEntity.ok(response)
    }

    @GetMapping("/by-genre")
    fun filterBooksByGenre(
        @RequestParam genre: String
    ): ResponseEntity<BookSearchResultDto> {
        val books = bookManagementService.filterBooksByGenre(genre)
        val response = BookSearchResultDto(
            books = bookMapper.toResponseDtoList(books),
            totalCount = books.size,
            filters = SearchFilters(genre = genre)
        )
        
        return ResponseEntity.ok(response)
    }
}