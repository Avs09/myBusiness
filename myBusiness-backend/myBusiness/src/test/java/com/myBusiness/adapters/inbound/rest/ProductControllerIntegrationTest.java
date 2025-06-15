package com.myBusiness.adapters.inbound.rest;

import com.myBusiness.application.dto.ProductInputDto;
import com.myBusiness.application.dto.ProductOutputDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;                                // <— import correcto
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;          // <— import correcto
import org.springframework.boot.test.web.client.TestRestTemplate;                         // <— import correcto
import org.springframework.http.*;

import java.math.BigDecimal;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.config.location=classpath:application-test.properties"
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class ProductControllerIntegrationTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @BeforeEach
    void setupData() {
        // Crea Category y Unit necesarios (asume que has expuesto endpoints para ellos)
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        restTemplate.postForEntity(
            "/api/categories",
            new HttpEntity<>(Map.of("name", "Electronics"), headers),
            Void.class
        );
        restTemplate.postForEntity(
            "/api/units",
            new HttpEntity<>(Map.of("name", "Piece"), headers),
            Void.class
        );
    }

    @Test
    void createAndGetProduct_fullFlow_shouldReturnCreatedProduct() {
        ProductInputDto input = ProductInputDto.builder()
            .name("ProdX")
            .thresholdMin(5)
            .thresholdMax(50)
            .price(new BigDecimal("199.99"))
            .categoryId(1L)
            .unitId(1L)
            .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<ProductInputDto> request = new HttpEntity<>(input, headers);

        ResponseEntity<ProductOutputDto> createResponse = restTemplate
            .postForEntity("/api/products", request, ProductOutputDto.class);
        assertThat(createResponse.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        ProductOutputDto created = createResponse.getBody();
        assertThat(created).isNotNull();
        assertThat(created.getId()).isPositive();
        assertThat(created.getName()).isEqualTo("ProdX");

        ResponseEntity<ProductOutputDto> getResponse = restTemplate
            .getForEntity("/api/products/" + created.getId(), ProductOutputDto.class);
        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        ProductOutputDto fetched = getResponse.getBody();
        assertThat(fetched).isEqualToComparingFieldByField(created);
    }
}
