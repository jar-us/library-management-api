package jar.us.librarymanagementsystemapi.schema

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jar.us.librarymanagementsystemapi.domain.Book

data class BookRequest(
    @field:NotBlank(message = "Title is required")
    val title: String,

    @field:NotBlank(message = "Author is required")
    val author: String,

    @field:NotBlank(message = "ISBN is required")
    val isbn: String,

    val publicationYear: Int? = null,
    val genre: String? = null,

    @field:NotNull(message = "Total copies is required")
    @field:PositiveOrZero(message = "Total copies must be zero or positive")
    val totalCopies: Int,

    @field:NotNull(message = "Available copies is required")
    @field:PositiveOrZero(message = "Available copies must be zero or positive")
    val availableCopies: Int,
)


fun BookRequest.toEntity(): Book {
    return Book(
        title = this.title,
        author = this.author,
        isbn = this.isbn,
        publicationYear = this.publicationYear,
        genre = this.genre,
        totalCopies = this.totalCopies,
        availableCopies = this.availableCopies
    )
}


data class BookResponse(
    val id: Long,
    val title: String,
    val author: String,
    val isbn: String,
    val publicationYear: Int?,
    val genre: String?,
    val totalCopies: Int,
    val availableCopies: Int,
)


fun Book.toResponse(): BookResponse {
    return BookResponse(
        id = this.id ?: throw IllegalStateException("Book ID cannot be null"),
        title = this.title,
        author = this.author,
        isbn = this.isbn,
        publicationYear = this.publicationYear,
        genre = this.genre,
        totalCopies = this.totalCopies,
        availableCopies = this.availableCopies
    )
}