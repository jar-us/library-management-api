package jar.us.librarymanagementsystemapi.infrastructure.configuration

import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.transaction.annotation.EnableTransactionManagement

@Configuration
@EnableJpaRepositories(basePackages = ["jar.us.librarymanagementsystemapi.infrastructure.persistence"])
@EnableTransactionManagement
class JpaConfiguration