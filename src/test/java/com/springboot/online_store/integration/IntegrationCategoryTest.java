package com.springboot.online_store.integration;

import com.springboot.online_store.dtos.category.CategoryCreateDto;
import com.springboot.online_store.dtos.category.CategoryInfoDto;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.repositories.CategoryRepository;
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


import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestRestTemplate
public class IntegrationCategoryTest {

    @Container
    @ServiceConnection
    static org.testcontainers.postgresql.PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:15.18-trixie");

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CategoryRepository categoryRepository;

    @AfterEach
    void cleanUp() {
        categoryRepository.deleteAllInBatch();
    }

    @Test
    void shouldCreateAndGetCategory(){
        var categoryDto = CategoryCreateDto.builder()
                .name("test_category")
                .build();

        ResponseEntity<CategoryInfoDto> response = restTemplate.postForEntity(
                "/categories",
                categoryDto,
                CategoryInfoDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(categoryDto.name());

        Long categoryId = response.getBody().id();
        ResponseEntity<CategoryInfoDto> getResponse = restTemplate.getForEntity(
                "/categories/" + categoryId,
                CategoryInfoDto.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().name()).isEqualTo(categoryDto.name());
    }

    @Test
    void shouldDeleteCategory(){
        var category = new Category("name", null);
        categoryRepository.save(category);

        ResponseEntity<String> response = restTemplate.exchange(
                "/categories/" + category.getId(),
                HttpMethod.DELETE,
                HttpEntity.EMPTY,
                String.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        ResponseEntity<CategoryInfoDto> getResponse = restTemplate.getForEntity(
                "/categories/" + category.getId(),
                CategoryInfoDto.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void shouldUpdateCategory(){
        var createCategory = new Category("first_name", null);
        categoryRepository.save(createCategory);

        var updateCategory = new Category("second_name", null);

        ResponseEntity<CategoryInfoDto> response = restTemplate.exchange(
                "/categories/" + createCategory.getId(),
                HttpMethod.PUT,
                new HttpEntity<>(updateCategory),
                CategoryInfoDto.class
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().id()).isNotNull();
        assertThat(response.getBody().name()).isEqualTo(updateCategory.getName());

        ResponseEntity<CategoryInfoDto> getResponse = restTemplate.getForEntity(
                "/categories/" + createCategory.getId(),
                CategoryInfoDto.class
        );

        assertThat(getResponse.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResponse.getBody()).isNotNull();
        assertThat(getResponse.getBody().name()).isEqualTo(updateCategory.getName());

    }

    @Test
    void shouldGetCategories(){
        var category = new Category("name", null);
        categoryRepository.save(category);

        ResponseEntity<List<CategoryInfoDto>> response = restTemplate.exchange(
                "/categories",
                HttpMethod.GET,
                HttpEntity.EMPTY,
                new ParameterizedTypeReference<List<CategoryInfoDto>>() {}
        );

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().getFirst().id()).isNotNull();
        assertThat(response.getBody().getFirst().name()).isEqualTo(category.getName());
    }
}
