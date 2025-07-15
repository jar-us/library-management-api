package jar.us.librarymanagementsystemapi.infrastructure.configuration

import jar.us.librarymanagementsystemapi.domain.repository.BookRepository
import jar.us.librarymanagementsystemapi.infrastructure.persistence.BookRepositoryImpl
import jar.us.librarymanagementsystemapi.infrastructure.persistence.JpaBookRepository
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class ApplicationConfiguration {

    @Bean
    fun bookRepository(jpaBookRepository: JpaBookRepository): BookRepository {
        return BookRepositoryImpl(jpaBookRepository)
    }
}