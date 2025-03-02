package jar.us.librarymanagementsystemapi.book

import jar.us.librarymanagementsystemapi.domain.Book
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

class RetrieveBookByIdTest : AbstractBookControllerTest() {

    @Test
    fun `should return book details when book exists`() {
        val book = Book(
            title = "Domain-Driven Design",
            author = "Eric Evans",
            isbn = "9780321125217",
            publicationYear = 2003,
            genre = "Software Development",
            totalCopies = 12,
            availableCopies = 10
        )
        val savedBook = bookRepository.save(book)

        mockMvc.perform(get("/api/books/${savedBook.id}"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.title").value("Domain-Driven Design"))
            .andExpect(jsonPath("$.author").value("Eric Evans"))
            .andExpect(jsonPath("$.isbn").value("9780321125217"))
            .andExpect(jsonPath("$.totalCopies").value(12))
            .andExpect(jsonPath("$.availableCopies").value(10))
    }

    @Test
    fun `should return 404 when book does not exist`() {
        mockMvc.perform(get("/api/books/999"))
            .andExpect(status().isNotFound)
            .andExpect(jsonPath("$.error").value("Book with ID 999 not found"))
    }
}