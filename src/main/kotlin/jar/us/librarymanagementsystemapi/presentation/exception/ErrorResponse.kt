package jar.us.librarymanagementsystemapi.presentation.exception

import java.time.LocalDateTime

data class ErrorResponse(
    val error: String,
    val timestamp: LocalDateTime,
    val status: Int
)