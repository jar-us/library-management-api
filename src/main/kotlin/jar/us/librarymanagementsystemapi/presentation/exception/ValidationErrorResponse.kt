package jar.us.librarymanagementsystemapi.presentation.exception

import java.time.LocalDateTime

data class ValidationErrorResponse(
    val error: String,
    val timestamp: LocalDateTime,
    val status: Int,
    val fieldErrors: Map<String, String>
)