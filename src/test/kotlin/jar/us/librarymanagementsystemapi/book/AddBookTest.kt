package jar.us.librarymanagementsystemapi.book

import jar.us.librarymanagementsystemapi.schema.BookRequest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

class AddBookTest : AbstractBookControllerTest() {


    @Test
    fun `should successfully add a new book`() {
        val bookRequest = BookRequest(
            title = "The Pragmatic Programmer",
            author = "Andrew Hunt",
            isbn = "9780135957059",
            publicationYear = 1999,
            genre = "Software Development",
            totalCopies = 15,
            availableCopies = 15
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest))
        )
            .andExpect(status().isCreated)
            .andExpect(jsonPath("$.title").value("The Pragmatic Programmer"))
            .andExpect(jsonPath("$.author").value("Andrew Hunt"))
            .andExpect(jsonPath("$.isbn").value("9780135957059"))
            .andExpect(jsonPath("$.totalCopies").value(15))
            .andExpect(jsonPath("$.availableCopies").value(15))
    }

    @Test
    fun `should return 400 when adding a book with duplicate ISBN`() {
        val bookRequest = BookRequest(
            title = "Test Book",
            author = "Test Author",
            isbn = "1234567890123",
            publicationYear = 2020,
            genre = "Test Genre",
            totalCopies = 10,
            availableCopies = 10
        )

        // Save the first book
        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest))
        )
            .andExpect(status().isCreated)

        // Try saving another book with the same ISBN
        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").value("ISBN '1234567890123' already exists"))
    }

    @Test
    fun `should return 400 when adding a book with missing required fields`() {
        val incompleteRequest = mapOf(
            "title" to "Incomplete Book" // Missing 'author', 'isbn', 'totalCopies', and 'availableCopies'
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").exists())
            .andExpect(jsonPath("$.error").value("Missing or null value for field 'author', which is required.")) // Sample validation message
    }

    @Test
    fun `should return 400 when adding a book with invalid data`() {
        val invalidRequest = BookRequest(
            title = "", // Invalid: empty title
            author = "Valid Author",
            isbn = "12345", // Invalid: ISBN too short
            publicationYear = 2023,
            genre = "Genre",
            totalCopies = -1, // Invalid: negative copies
            availableCopies = -5 // Invalid: negative copies
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.error").exists()) // Check if error message exists

            .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Title is required")))
            .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Available copies must be zero or positive")))
            .andExpect(jsonPath("$.error").value(org.hamcrest.Matchers.containsString("Total copies must be zero or positive")))
    }
}