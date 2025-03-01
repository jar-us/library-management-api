package jar.us.librarymanagementsystemapi.book

import jar.us.librarymanagementsystemapi.domain.Book
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.*
import kotlin.test.Test

class RetrieveBooksTest : AbstractBookControllerTest() {

    @Test
    fun `should return empty array when no books exist`() {
        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(content().json("[]"))
    }

    @Test
    fun `should return list of books when books exist`() {
        val book1 = Book(
            title = "Clean Code",
            author = "Robert C. Martin",
            isbn = "9780132350884",
            publicationYear = 2008,
            genre = "Software Development",
            totalCopies = 10,
            availableCopies = 8
        )
        val book2 = Book(
            title = "Effective Java",
            author = "Joshua Bloch",
            isbn = "9780134685991",
            publicationYear = 2017,
            genre = "Software Development",
            totalCopies = 5,
            availableCopies = 5
        )
        bookRepository.saveAll(listOf(book1, book2))

        mockMvc.perform(get("/api/books"))
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].title").value("Clean Code"))
            .andExpect(jsonPath("$[1].title").value("Effective Java"))
    }
}