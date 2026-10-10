package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.category.CategoryCreateDto;
import com.springboot.online_store.dtos.category.CategoryInfoDto;
import com.springboot.online_store.services.CategoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {
    @MockitoBean
    private CategoryService categoryService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateCategory {
        @Test
        void createCategoryShouldWork() throws Exception {
            var createDto = builderCategoryCreateDto();
            var infoDto = CategoryInfoDto.builder()
                    .name("name")
                    .build();

            when(categoryService.createCategory(createDto))
                    .thenReturn(infoDto);

            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(infoDto.name()));

            verify(categoryService)
                    .createCategory(createDto);
        }

        @Test
        void createCategoryCheckValidation() throws Exception {
            var createDto = CategoryCreateDto.builder()
                    .name("")
                    .build();

            mockMvc.perform(post("/categories")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest());

            verify(categoryService, never())
                    .createCategory(createDto);
        }

    }

    @Nested
    class GetCategories {
        @Test
        void getCategoriesShouldWork() throws Exception {
            Integer pageSize = 10;
            Integer pageNumber = 0;
            var infoDto = CategoryInfoDto.builder()
                    .name("name")
                    .build();
            var infoDtoList = List.of(infoDto);

            when(categoryService.getCategories(pageSize, pageNumber))
                    .thenReturn(infoDtoList);

            mockMvc.perform(get("/categories")
                            .param("pageSize", pageSize.toString())
                            .param("pageNumber", pageNumber.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value(infoDtoList.getFirst().name()));

            verify(categoryService)
                    .getCategories(pageSize, pageNumber);
        }
    }

    @Nested
    class UpdateCategory {
        @Test
        void updateCategoryShouldWork() throws Exception {
            var createDto = builderCategoryCreateDto();
            Long id = 1L;
            var infoDto = CategoryInfoDto.builder()
                    .name("name")
                    .build();

            when(categoryService.updateCategory(createDto, id))
                    .thenReturn(infoDto);

            mockMvc.perform(put("/categories/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(infoDto.name()));

            verify(categoryService)
                    .updateCategory(createDto, id);
        }

        @Test
        void updateCategoryCheckValidation() throws Exception {
            var createDto = CategoryCreateDto.builder()
                    .name("")
                    .build();
            Long id = 1L;

            mockMvc.perform(put("/categories/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest());

            verify(categoryService, never())
                    .updateCategory(createDto, id);
        }

        @Test
        void updateCategoryThrowEntityNotFoundException() throws Exception {
            var createDto = CategoryCreateDto.builder()
                    .name("name")
                    .build();
            Long id = 1L;

            when(categoryService.updateCategory(createDto, id))
                    .thenThrow(new EntityNotFoundException());

            mockMvc.perform(put("/categories/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isNotFound());

            verify(categoryService)
                    .updateCategory(createDto, id);
        }
    }

    @Nested
    class DeleteEmptyCategory {
        @Test
        void deleteEmptyCategoryShouldWork() throws Exception {
            Long id = 1L;

            mockMvc.perform(delete("/categories/{id}", id))
                    .andExpect(status().isNoContent());

            verify(categoryService).deleteEmptyCategory(id);
        }

        @Test
        void deleteEmptyCategoryShouldThrowEntityNotFoundException() throws Exception {
            Long id = 1L;

            doThrow(new EntityNotFoundException())
                    .when(categoryService).deleteEmptyCategory(id);

            mockMvc.perform(delete("/categories/{id}", id))
                    .andExpect(status().isNotFound());

            verify(categoryService).deleteEmptyCategory(id);
        }
    }

    @Nested
    class deleteCategoryWithProducts {
        @Test
        void deleteCategoryWithProductsShouldWork() throws Exception {
            Long id = 1L;

            mockMvc.perform(delete("/categories/{id}/withProducts", id))
                    .andExpect(status().isNoContent());

            verify(categoryService).deleteCategoryWithProducts(id);
        }
        @Test
        void deleteCategoryWithProductsShouldThrowEntityNotFoundException() throws Exception {
            Long id = 1L;

            doThrow(new EntityNotFoundException())
                    .when(categoryService).deleteCategoryWithProducts(id);

            mockMvc.perform(delete("/categories/{id}/withProducts", id))
                    .andExpect(status().isNotFound());

            verify(categoryService).deleteCategoryWithProducts(id);
        }
    }

    private CategoryCreateDto builderCategoryCreateDto() {
        return CategoryCreateDto.builder()
                .name("name")
                .parentId(1L)
                .build();
    }

}