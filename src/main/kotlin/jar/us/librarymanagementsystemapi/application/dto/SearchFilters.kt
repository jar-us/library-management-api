package jar.us.librarymanagementsystemapi.application.dto

data class SearchFilters(
    val genre: String? = null,
    val author: String? = null,
    val availableOnly: Boolean = false
)