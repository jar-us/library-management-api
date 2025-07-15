package jar.us.librarymanagementsystemapi.domain.model

import jar.us.librarymanagementsystemapi.domain.exception.BookBusinessException

@JvmInline
value class ISBN(val value: String) {
    init {
        validateISBN(value)
    }

    private fun validateISBN(isbn: String) {
        val cleaned = isbn.replace("-", "").replace(" ", "")
        
        if (cleaned.length !in listOf(10, 13)) {
            throw BookBusinessException("ISBN must be 10 or 13 characters long")
        }
        
        if (!cleaned.all { it.isDigit() || (cleaned.length == 10 && it.uppercaseChar() == 'X') }) {
            throw BookBusinessException("ISBN contains invalid characters")
        }
    }

    fun isISBN10(): Boolean = value.replace("-", "").replace(" ", "").length == 10
    fun isISBN13(): Boolean = value.replace("-", "").replace(" ", "").length == 13

    override fun toString(): String = value
}