package com.springboot.online_store.integration;

import com.springboot.online_store.dtos.product.CreateAndUpdateProductDto;
import com.springboot.online_store.dtos.product.ProductInfoDto;
import com.springboot.online_store.dtos.product.ProductSearchFilter;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.entities.Product;
import com.springboot.online_store.enums.ProductStatus;
import com.springboot.online_store.repositories.CategoryRepository;
import com.springboot.online_store.repositories.ProductRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.TestRestTemplate;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureTestRestTemplate;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.postgresql.PostgreSQLContainer;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class IntegrationProductTest {

    @Container
    @ServiceConnection
    static org.testcontainers.postgresql.PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:15.18-trixie");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @AfterEach
    void cleanUp() {
        productRepository.deleteAllInBatch();
        categoryRepository.deleteAllInBatch();
    }

    @Test
    void shouldCreateAndGetProduct(){
        var category = new Category("name", null);

        categoryRepository.save(category);
        var request = builderProductDto(category.getId());

        ResponseEntity<ProductInfoDto> response = restTemplate.postForEntity(
                "/products",
                request,
                ProductInfoDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(request.name());

        Long productId = response.getBody().id();
        ResponseEntity<ProductInfoDto> getResponse = restTemplate.getForEntity(
                "/products/" + productId,
                ProductInfoDto.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().name()).isEqualTo(request.name());
    }

    @Test
    void shouldDeleteProduct(){
        var category = new Category("name", null);
        categoryRepository.save(category);

        var product = new Product(
                "test_product",
                BigDecimal.valueOf(1000),
                50,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                category,
                ProductStatus.ACTIVE
        );
        productRepository.save(product);

        ResponseEntity<String> response = restTemplate.exchange(
                "/products/" + product.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
                );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<String> getResponse = restTemplate.getForEntity(
                "/products/" + product.getId(),
                String.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateProduct(){
        var category = new Category("name", null);
        categoryRepository.save(category);
        var product = new Product(
                "product",
                BigDecimal.valueOf(100),
                10,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                category,
                ProductStatus.ACTIVE
        );
        productRepository.save(product);

        var request = builderProductDto(category.getId());

        ResponseEntity<ProductInfoDto> response = restTemplate.exchange(
                "/products/" + product.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(request),
                ProductInfoDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(request.name());

        Long productId = product.getId();
        ResponseEntity<ProductInfoDto> getResponse = restTemplate.getForEntity(
                "/products/" + productId,
                ProductInfoDto.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().name()).isEqualTo(request.name());

    }

    @Test
    void shouldGetProductsCatalog(){
        var category = new Category("name", null);
        categoryRepository.save(category);

        var product = new Product(
                "product",
                BigDecimal.valueOf(100),
                10,
                null,
                LocalDateTime.now(),
                LocalDateTime.now(),
                category,
                ProductStatus.ACTIVE
        );
        productRepository.save(product);

        ResponseEntity<List<ProductInfoDto>> response = restTemplate.exchange(
                "/products",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<ProductInfoDto>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirst().id()).isNotNull();
        assertThat(response.getBody().getFirst().name()).isEqualTo(product.getName());
    }


    private CreateAndUpdateProductDto builderProductDto(Long categoryId){
        return CreateAndUpdateProductDto.builder()
                .name("test_product")
                .price(BigDecimal.valueOf(1000))
                .categoryId(categoryId)
                .quantity(50)
                .imageUrl("fjddjdj")
                .status(ProductStatus.ACTIVE)
                .build();
    }

}
