package ch.glauserillnau.serviceauftrag;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/serviceauftrag",
    "spring.jpa.hibernate.ddl-auto=validate"
})
class ServiceauftragApplicationTests {

    @Test
    void contextLoads() {
    }
}
