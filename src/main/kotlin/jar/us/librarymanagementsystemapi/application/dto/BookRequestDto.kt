package jar.us.librarymanagementsystemapi.application.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Positive
import jakarta.validation.constraints.PositiveOrZero
import jakarta.validation.constraints.Size

data class CreateBookRequestDto(
    @field:NotBlank(message = "Title is required")
    @field:Size(min = 1, max = 255, message = "Title must be between 1 and 255 characters")
    val title: String,

    @field:NotBlank(message = "Author is required")
    @field:Size(min = 1, max = 255, message = "Author name must be between 1 and 255 characters")
    val author: String,

    @field:NotBlank(message = "ISBN is required")
    @field:Size(min = 10, max = 17, message = "ISBN must be between 10 and 17 characters")
    val isbn: String,

    val publicationYear: Int? = null,

    @field:Size(max = 100, message = "Genre must not exceed 100 characters")
    val genre: String? = null,

    @field:NotNull(message = "Total copies is required")
    @field:Positive(message = "Total copies must be greater than 0")
    val totalCopies: Int,

    @field:PositiveOrZero(message = "Available copies must be zero or positive")
    val availableCopies: Int? = null
)