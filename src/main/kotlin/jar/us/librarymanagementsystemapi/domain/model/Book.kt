package jar.us.librarymanagementsystemapi.domain.model

import jakarta.persistence.*
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero
import jar.us.librarymanagementsystemapi.domain.exception.BookBusinessException

@Entity
@Table(name = "books")
data class Book(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @field:NotBlank(message = "Title is required")
    @Column(nullable = false)
    val title: String,

    @field:NotBlank(message = "Author is required")
    @Column(nullable = false)
    val author: String,

    @field:NotBlank(message = "ISBN is required")
    @Column(nullable = false, unique = true)
    val isbn: String,

    @Column(name = "publication_year")
    val publicationYear: Int? = null,

    val genre: String? = null,

    @field:NotNull(message = "Total copies is required")
    @field:PositiveOrZero(message = "Total copies must be zero or positive")
    @Column(nullable = false)
    val totalCopies: Int,

    @field:NotNull(message = "Available copies is required")
    @field:PositiveOrZero(message = "Available copies must be zero or positive")
    @Column(nullable = false)
    val availableCopies: Int,
) {
    init {
        validateAvailableCopies()
    }

    fun isAvailable(): Boolean = availableCopies > 0

    fun hasLimitedAvailability(): Boolean = availableCopies in 1..LIMITED_AVAILABILITY_THRESHOLD

    fun isOutOfStock(): Boolean = availableCopies == 0

    fun borrowCopy(): Book {
        if (isOutOfStock()) {
            throw BookBusinessException("Book '${title}' is out of stock")
        }
        return this.copy(availableCopies = availableCopies - 1)
    }

    fun returnCopy(): Book {
        if (availableCopies >= totalCopies) {
            throw BookBusinessException("Cannot return more copies than total available")
        }
        return this.copy(availableCopies = availableCopies + 1)
    }

    fun updateAvailability(newAvailableCopies: Int): Book {
        if (newAvailableCopies < 0) {
            throw BookBusinessException("Available copies cannot be negative")
        }
        if (newAvailableCopies > totalCopies) {
            throw BookBusinessException("Available copies cannot exceed total copies")
        }
        return this.copy(availableCopies = newAvailableCopies)
    }

    private fun validateAvailableCopies() {
        if (availableCopies > totalCopies) {
            throw BookBusinessException("Available copies ($availableCopies) cannot exceed total copies ($totalCopies)")
        }
    }

    companion object {
        private const val LIMITED_AVAILABILITY_THRESHOLD = 2
        
        fun create(
            title: String,
            author: String,
            isbn: String,
            publicationYear: Int? = null,
            genre: String? = null,
            totalCopies: Int,
            availableCopies: Int = totalCopies
        ): Book {
            // Validate ISBN format
            val cleanIsbn = isbn.replace("-", "").replace(" ", "")
            if (cleanIsbn.length !in listOf(10, 13)) {
                throw BookBusinessException("ISBN must be 10 or 13 characters long")
            }
            
            return Book(
                title = title.trim(),
                author = author.trim(),
                isbn = isbn,
                publicationYear = publicationYear,
                genre = genre?.trim(),
                totalCopies = totalCopies,
                availableCopies = availableCopies
            )
        }
    }
}