package at.ac.tuwien.sepr.groupphase.backend;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;

@TestConfiguration
public class TestConfig {

    @Bean
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    public DatabaseCleaner databaseCleaner() {
        return new DatabaseCleaner();
    }
}
