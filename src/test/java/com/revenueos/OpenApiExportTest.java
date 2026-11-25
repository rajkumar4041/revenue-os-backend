package com.revenueos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

/**
 * Exports the generated OpenAPI document to a file so the frontend code
 * generator (Kubb) can consume it. Runs on H2, so it needs no real database.
 * Override the target with -Dopenapi.output=/path/to/openapi.json.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@ActiveProfiles("test")
class OpenApiExportTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void exportOpenApiSpec() throws Exception {
        String spec = restTemplate.getForObject("/v3/api-docs", String.class);
        assertThat(spec).contains("\"openapi\"");

        Path output = Path.of(System.getProperty("openapi.output", "target/openapi.json"));
        if (output.getParent() != null) {
            Files.createDirectories(output.getParent());
        }
        Files.writeString(output, spec);
    }
}
