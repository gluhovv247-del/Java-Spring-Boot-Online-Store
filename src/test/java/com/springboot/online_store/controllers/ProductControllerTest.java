package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.product.CreateAndUpdateProductDto;
import com.springboot.online_store.dtos.product.ProductFilter;
import com.springboot.online_store.dtos.product.ProductInfoDto;
import com.springboot.online_store.dtos.product.ProductSearchFilter;
import com.springboot.online_store.entities.Category;
import com.springboot.online_store.services.ProductService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;
import java.math.BigDecimal;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {
    @MockitoBean
    private ProductService productService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class GetProduct {
        @Test
        void getProductShouldWork() throws Exception {
            var infoDto = builderInfoDto();

            Long id = 1L;
            when(productService.getProduct(id))
                    .thenReturn(infoDto);

            mockMvc.perform(get("/products/get/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(infoDto.name()))
                    .andExpect(jsonPath("$.price").value(infoDto.price()))
                    .andExpect(jsonPath("$.quantity").value(infoDto.quantity()))
                    .andExpect(jsonPath("$.imageUrl").value(infoDto.imageUrl()));

            verify(productService).getProduct(id);
        }

        @Test
        void getProductWithEntityNotFoundException() throws Exception {
            Long id = 1L;
            when(productService.getProduct(id))
                    .thenThrow(new EntityNotFoundException());

            mockMvc.perform(get("/products/get/{id}", id))
                    .andExpect(status().isNotFound());

            verify(productService).getProduct(id);
        }
    }

    @Nested
    class CreateProduct {
        @Test
        void createProductShouldWork() throws Exception {
            var createDto = builderProductDto();

            var infoDto = builderInfoDto();

            when(productService.createProduct(createDto))
                    .thenReturn(infoDto);

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.name").value(infoDto.name()))
                    .andExpect(jsonPath("$.price").value(infoDto.price()))
                    .andExpect(jsonPath("$.quantity").value(infoDto.quantity()))
                    .andExpect(jsonPath("$.imageUrl").value(infoDto.imageUrl()));

            verify(productService).createProduct(createDto);
        }
        @Test
        void createProductValidationCheck() throws Exception {
            var createDto = builderInvalidProductDto();

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).createProduct(createDto);
        }

        @Test
        void createProductShouldThrowEntityNotFoundException() throws Exception {
            var createDto = builderProductDto();

            when(productService.createProduct(createDto))
                    .thenThrow(new EntityNotFoundException());

            mockMvc.perform(post("/products")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isNotFound());
        }
    }

    @Nested
    class DeleteProduct {
        @Test
        void deleteProductShouldWork() throws Exception {
            Long id = 1L;

            mockMvc.perform(delete("/products/{id}", id))
                    .andExpect(status().isNoContent());

            verify(productService).deleteProduct(id);
        }

        @Test
        void deleteProductShouldThrowEntityNotFoundException() throws Exception {
            Long id = 1L;

            doThrow(new EntityNotFoundException())
                    .when(productService)
                            .deleteProduct(id);

            mockMvc.perform(delete("/products/{id}", id))
                    .andExpect(status().isNotFound());

            verify(productService).deleteProduct(id);
        }
    }

    @Nested
    class UpdateProduct {
        @Test
        void updateProductShouldWork() throws Exception {
            Long id = 1L;
            var createDto = builderProductDto();

            var infoDto = builderInfoDto();

            when(productService.updateProduct(id, createDto))
                    .thenReturn(infoDto);

            mockMvc.perform(put("/products/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.name").value(infoDto.name()))
                    .andExpect(jsonPath("$.price").value(infoDto.price()))
                    .andExpect(jsonPath("$.quantity").value(infoDto.quantity()))
                    .andExpect(jsonPath("$.imageUrl").value(infoDto.imageUrl()));

            verify(productService).updateProduct(id, createDto);
        }

        @Test
        void updateProductValidationCheck() throws Exception {
            Long id = 1L;
            var createDto = builderInvalidProductDto();

            mockMvc.perform(put("/products/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest());

            verify(productService, never()).updateProduct(id, createDto);
        }

        @Test
        void updateProductShouldThrowEntityNotFoundException() throws Exception {
            Long id = 1L;
            var createDto = builderProductDto();

            when(productService.updateProduct(id, createDto))
                    .thenThrow(new EntityNotFoundException());

            mockMvc.perform(put("/products/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isNotFound());

            verify(productService).updateProduct(id, createDto);
        }
    }

    @Nested
    class GetProductsCatalog {
        @Test
        void getProductsCatalogShouldWork() throws Exception {

            var infoDto = builderInfoDto();
            var infoDtoList = List.of(infoDto);

            when(productService.getCatalog(any(ProductFilter.class)))
                    .thenReturn(infoDtoList);

            mockMvc.perform(get("/products")
                            .param("pageSize", "10")
                            .param("pageNumber", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value(infoDtoList.getFirst().name()))
                    .andExpect(jsonPath("$[0].price").value(infoDtoList.getFirst().price()))
                    .andExpect(jsonPath("$[0].quantity").value(infoDtoList.getFirst().quantity()))
                    .andExpect(jsonPath("$[0].imageUrl").value(infoDtoList.getFirst().imageUrl()));
        }
    }

    @Nested
    class GetProductsBySearch {
        @Test
        void getProductsBySearch() throws Exception {

            var infoDto = builderInfoDto();
            var infoDtoList = List.of(infoDto);

            when(productService.searchByFilter(any(ProductSearchFilter.class)))
                    .thenReturn(infoDtoList);

            mockMvc.perform(get("/products/search")
                            .param("pageSize", "10")
                            .param("pageNumber", "0"))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].name").value(infoDtoList.getFirst().name()))
                    .andExpect(jsonPath("$[0].price").value(infoDtoList.getFirst().price()))
                    .andExpect(jsonPath("$[0].quantity").value(infoDtoList.getFirst().quantity()))
                    .andExpect(jsonPath("$[0].imageUrl").value(infoDtoList.getFirst().imageUrl()));
        }
    }

    private ProductInfoDto builderInfoDto(){
        return ProductInfoDto.builder()
                .id(1L)
                .name("test_product")
                .price(BigDecimal.valueOf(1000))
                .quantity(50)
                .imageUrl("fjddjdj")
                .build();
    }

    private CreateAndUpdateProductDto builderProductDto(){
        return CreateAndUpdateProductDto.builder()
                .name("test_product")
                .price(BigDecimal.valueOf(1000))
                .categoryId(1L)
                .quantity(50)
                .imageUrl("fjddjdj")
                .build();
    }

    private CreateAndUpdateProductDto builderInvalidProductDto(){
        return CreateAndUpdateProductDto.builder()
                .name("")
                .price(BigDecimal.valueOf(-100))
                .quantity(10000)
                .build();
    }
}

