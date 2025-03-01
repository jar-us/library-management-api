package jar.us.librarymanagementsystemapi.book


import com.fasterxml.jackson.databind.ObjectMapper
import jar.us.librarymanagementsystemapi.repository.BookRepository
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.servlet.MockMvc


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
abstract class AbstractBookControllerTest {
    @Autowired
    protected lateinit var mockMvc: MockMvc

    @Autowired
    protected lateinit var objectMapper: ObjectMapper

    @Autowired
    protected lateinit var bookRepository: BookRepository

    @BeforeEach
    fun setup() {
        // Clear the book repository before each test to ensure clean database state
        bookRepository.deleteAll()
    }
}