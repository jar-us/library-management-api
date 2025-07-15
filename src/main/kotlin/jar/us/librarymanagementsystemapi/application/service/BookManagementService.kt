package jar.us.librarymanagementsystemapi.application.service

import jar.us.librarymanagementsystemapi.domain.model.Book

interface BookManagementService {
    fun createBook(book: Book): Book
    fun findBookById(id: Long): Book?
    fun findAllBooks(): List<Book>
    fun updateBook(id: Long, book: Book): Book
    fun deleteBook(id: Long): Boolean
    fun searchBooksByTitle(title: String): List<Book>
    fun searchBooksByAuthor(author: String): List<Book>
    fun filterBooksByGenre(genre: String): List<Book>
}