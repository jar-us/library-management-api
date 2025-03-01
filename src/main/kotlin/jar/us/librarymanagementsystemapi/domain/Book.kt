package jar.us.librarymanagementsystemapi.domain

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PositiveOrZero

@Entity
data class Book(

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

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