package jar.us.librarymanagementsystemapi.application.mapper

import jar.us.librarymanagementsystemapi.application.dto.AvailabilityStatus
import jar.us.librarymanagementsystemapi.application.dto.BookResponseDto
import jar.us.librarymanagementsystemapi.application.dto.CreateBookRequestDto
import jar.us.librarymanagementsystemapi.application.dto.UpdateBookRequestDto
import jar.us.librarymanagementsystemapi.domain.model.Book
import org.springframework.stereotype.Component

@Component
class BookMapper {

    fun toEntity(dto: CreateBookRequestDto): Book {
        return Book.create(
            title = dto.title,
            author = dto.author,
            isbn = dto.isbn,
            publicationYear = dto.publicationYear,
            genre = dto.genre,
            totalCopies = dto.totalCopies,
            availableCopies = dto.availableCopies ?: dto.totalCopies
        )
    }

    fun toEntity(dto: UpdateBookRequestDto, id: Long): Book {
        return Book(
            id = id,
            title = dto.title,
            author = dto.author,
            isbn = dto.isbn,
            publicationYear = dto.publicationYear,
            genre = dto.genre,
            totalCopies = dto.totalCopies,
            availableCopies = dto.availableCopies
        )
    }

    fun toResponseDto(book: Book): BookResponseDto {
        return BookResponseDto(
            id = book.id ?: throw IllegalStateException("Book ID cannot be null for response"),
            title = book.title,
            author = book.author,
            isbn = book.isbn,
            publicationYear = book.publicationYear,
            genre = book.genre,
            totalCopies = book.totalCopies,
            availableCopies = book.availableCopies,
            availabilityStatus = determineAvailabilityStatus(book)
        )
    }

    fun toResponseDtoList(books: List<Book>): List<BookResponseDto> {
        return books.map { toResponseDto(it) }
    }

    private fun determineAvailabilityStatus(book: Book): AvailabilityStatus {
        return when {
            book.isOutOfStock() -> AvailabilityStatus.OUT_OF_STOCK
            book.hasLimitedAvailability() -> AvailabilityStatus.LIMITED
            else -> AvailabilityStatus.AVAILABLE
        }
    }
}