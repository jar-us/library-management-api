package jar.us.librarymanagementsystemapi.presentation.exception

import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jar.us.librarymanagementsystemapi.domain.exception.BookBusinessException
import jar.us.librarymanagementsystemapi.domain.exception.UserBusinessException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import java.time.LocalDateTime

@RestControllerAdvice
class GlobalExceptionHandler {

    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)

    @ExceptionHandler(BookNotFoundException::class)
    fun handleBookNotFoundException(ex: BookNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("Book not found: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            error = ex.message ?: "Book not found",
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value()
        )
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(BookBusinessException::class)
    fun handleBookBusinessException(ex: BookBusinessException): ResponseEntity<ErrorResponse> {
        logger.warn("Business rule violation: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            error = ex.message ?: "Business rule violation",
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value()
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFoundException(ex: UserNotFoundException): ResponseEntity<ErrorResponse> {
        logger.info("User not found: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            error = ex.message ?: "User not found",
            timestamp = LocalDateTime.now(),
            status = HttpStatus.NOT_FOUND.value()
        )
        
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(UserBusinessException::class)
    fun handleUserBusinessException(ex: UserBusinessException): ResponseEntity<ErrorResponse> {
        logger.warn("User business rule violation: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            error = ex.message ?: "User business rule violation",
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value()
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(ex: MethodArgumentNotValidException): ResponseEntity<ValidationErrorResponse> {
        logger.info("Validation error: {}", ex.message)
        
        val fieldErrors = ex.bindingResult.fieldErrors.associate { error ->
            error.field to (error.defaultMessage ?: "Invalid value")
        }
        
        val errorResponse = ValidationErrorResponse(
            error = "Validation failed",
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value(),
            fieldErrors = fieldErrors
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleInvalidJsonException(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        logger.info("Invalid JSON request: {}", ex.message)
        
        val errorMessage = when (val cause = ex.cause) {
            is MismatchedInputException -> {
                "Invalid value for field '${cause.path.joinToString(".") { it.fieldName }}'"
            }
            else -> "Invalid JSON format"
        }
        
        val errorResponse = ErrorResponse(
            error = errorMessage,
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value()
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(IllegalArgumentException::class)
    fun handleIllegalArgumentException(ex: IllegalArgumentException): ResponseEntity<ErrorResponse> {
        logger.warn("Illegal argument: {}", ex.message)
        
        val errorResponse = ErrorResponse(
            error = ex.message ?: "Invalid argument",
            timestamp = LocalDateTime.now(),
            status = HttpStatus.BAD_REQUEST.value()
        )
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(Exception::class)
    fun handleGenericException(ex: Exception): ResponseEntity<ErrorResponse> {
        logger.error("Unexpected error occurred", ex)
        
        val errorResponse = ErrorResponse(
            error = "An unexpected error occurred",
            timestamp = LocalDateTime.now(),
            status = HttpStatus.INTERNAL_SERVER_ERROR.value()
        )
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse)
    }
}