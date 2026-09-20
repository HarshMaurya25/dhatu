package com.project.dhatu;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = {
    "spring.datasource.url=jdbc:postgresql://localhost:5432/dhatu_db",
    "URL_DATABASE=jdbc:postgresql://localhost:5432/dhatu_db"
})
class DhatuApplicationTests {

	@Test
	void contextLoads() {
	}

}
