package jar.us.librarymanagementsystemapi.application.dto

import com.fasterxml.jackson.annotation.JsonInclude

@JsonInclude(JsonInclude.Include.NON_NULL)
data class BookResponseDto(
    val id: Long,
    val title: String,
    val author: String,
    val isbn: String,
    val publicationYear: Int? = null,
    val genre: String? = null,
    val totalCopies: Int,
    val availableCopies: Int,
    val availabilityStatus: AvailabilityStatus
)