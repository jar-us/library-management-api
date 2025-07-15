package jar.us.librarymanagementsystemapi.book

import jar.us.librarymanagementsystemapi.application.dto.CreateBookRequestDto
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import kotlin.test.Test

class AddBookTest : AbstractBookControllerTest() {


    @Test
    fun `should successfully add a new book`() {
        val bookRequest = CreateBookRequestDto(
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
        val bookRequest = CreateBookRequestDto(
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
            .andExpect(jsonPath("$.error").value("Book with ISBN '1234567890123' already exists"))
    }

    @Test
    fun `should return 400 when adding a book with missing required fields`() {
        val incompleteRequest = CreateBookRequestDto(
            title = "Incomplete Book",
            author = "", // Missing author
            isbn = "", // Missing ISBN
            publicationYear = 2023,
            totalCopies = 10,
            availableCopies = 10
        )

        mockMvc.perform(
            post("/api/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(incompleteRequest))
        )
            .andExpect(status().isBadRequest)
            .andExpect(jsonPath("$.fieldErrors.author").value("Author is required"))
            .andExpect(jsonPath("$.fieldErrors.isbn").value("ISBN is required"))
    }

    @Test
    fun `should return 400 when adding a book with invalid data`() {
        val invalidRequest = CreateBookRequestDto(
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
            .andExpect(jsonPath("$.fieldErrors.title").value("Title is required"))
            .andExpect(jsonPath("$.fieldErrors.isbn").value("ISBN must be between 10 and 17 characters"))
            .andExpect(jsonPath("$.fieldErrors.totalCopies").value("Total copies must be greater than 0"))
            .andExpect(jsonPath("$.fieldErrors.availableCopies").value("Available copies must be zero or positive"))
    }
}