package jar.us.librarymanagementsystemapi.domain.model

@JvmInline
value class BookId(val value: Long) {
    init {
        require(value > 0) { "Book ID must be positive" }
    }
}