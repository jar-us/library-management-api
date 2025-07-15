package jar.us.librarymanagementsystemapi.application.dto

data class BookSearchResultDto(
    val books: List<BookResponseDto>,
    val totalCount: Int,
    val searchTerm: String? = null,
    val filters: SearchFilters? = null
)