package com.gomin_jungdok.gdgoc;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@TestPropertySource(properties = {
    "spring.datasource.url=${DB_URL}",
    "spring.datasource.username=${DB_USERNAME}",
    "spring.datasource.password=${DB_PASSWORD}",
    "spring.cloud.gcp.storage.bucket=${GCS_NAME}",
    "spring.cloud.gcp.storage.credentials.location=${GCS_PATH}"
})

@SpringBootTest
class GdgocApplicationTests {

	@Test
	void contextLoads() {
	}

}
