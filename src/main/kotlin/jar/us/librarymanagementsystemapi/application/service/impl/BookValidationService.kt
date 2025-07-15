package jar.us.librarymanagementsystemapi.application.service.impl

import jar.us.librarymanagementsystemapi.domain.exception.BookBusinessException
import jar.us.librarymanagementsystemapi.domain.model.Book
import org.springframework.stereotype.Service

@Service
class BookValidationService {

    fun validateForCreation(book: Book) {
        validateBasicFields(book)
        validateBusinessRules(book)
    }

    fun validateForUpdate(book: Book, existingBook: Book) {
        validateBasicFields(book)
        validateBusinessRules(book)
        validateUpdateRules(book, existingBook)
    }

    private fun validateBasicFields(book: Book) {
        if (book.title.isBlank()) {
            throw BookBusinessException("Book title cannot be empty")
        }
        
        if (book.author.isBlank()) {
            throw BookBusinessException("Book author cannot be empty")
        }
        
        if (book.totalCopies <= 0) {
            throw BookBusinessException("Total copies must be greater than 0")
        }
        
        if (book.availableCopies < 0) {
            throw BookBusinessException("Available copies cannot be negative")
        }
    }

    private fun validateBusinessRules(book: Book) {
        if (book.availableCopies > book.totalCopies) {
            throw BookBusinessException("Available copies cannot exceed total copies")
        }
        
        book.publicationYear?.let { year ->
            val currentYear = java.time.Year.now().value
            if (year < 1000 || year > currentYear + 1) {
                throw BookBusinessException("Publication year must be between 1000 and ${currentYear + 1}")
            }
        }
    }

    private fun validateUpdateRules(book: Book, existingBook: Book) {
        // Business rule: Cannot reduce total copies below currently borrowed copies
        val borrowedCopies = existingBook.totalCopies - existingBook.availableCopies
        if (book.totalCopies < borrowedCopies) {
            throw BookBusinessException("Cannot reduce total copies below currently borrowed copies ($borrowedCopies)")
        }
    }
}