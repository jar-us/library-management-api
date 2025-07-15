package jar.us.librarymanagementsystemapi.domain.repository

import jar.us.librarymanagementsystemapi.domain.model.Book

interface BookRepository {
    fun save(book: Book): Book
    fun findById(id: Long): Book?
    fun findAll(): List<Book>
    fun existsByIsbn(isbn: String): Boolean
    fun existsById(id: Long): Boolean
    fun deleteById(id: Long)
    fun findByIsbn(isbn: String): Book?
    fun findByTitleContainingIgnoreCase(title: String): List<Book>
    fun findByAuthorContainingIgnoreCase(author: String): List<Book>
    fun findByGenre(genre: String): List<Book>
    fun deleteAll()
    fun saveAll(books: List<Book>): List<Book>
}