package jar.us.librarymanagementsystemapi.exception

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.fasterxml.jackson.module.kotlin.MissingKotlinParameterException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.server.ResponseStatusException

@RestControllerAdvice
class GlobalExceptionHandler {

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ErrorResponse> {
        val errorMessage = ex.bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
        val errorResponse = ErrorResponse(error = errorMessage)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }

    // Handle ResponseStatusException (e.g., duplicate ISBN)
    @ExceptionHandler(ResponseStatusException::class)
    fun handleResponseStatusException(ex: ResponseStatusException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(error = ex.reason ?: "An error occurred")
        return ResponseEntity(errorResponse, ex.statusCode)
    }

    // Generic error handler (fallback)
    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse("Unexpected error: ${ex.message}")
        return ResponseEntity(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR)
    }

    // Handle missing required fields (e.g., missing non-nullable fields)
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJsonException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val errorMessage = if (ex.cause is MismatchedInputException) {
            val cause = ex.cause as MismatchedInputException
            "Missing or null value for field '${cause.path.joinToString(".") { it.fieldName }}', which is required."
        } else {
            "Malformed JSON request. Error: ${ex.message}"
        }
        val errorResponse = ErrorResponse(error = errorMessage)
        return ResponseEntity(errorResponse, HttpStatus.BAD_REQUEST)
    }
}

data class ErrorResponse(
    val error: String,
)