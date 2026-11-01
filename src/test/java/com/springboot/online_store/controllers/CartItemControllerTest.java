package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.cart.CartItemDto;
import com.springboot.online_store.dtos.cart.UpdateCartItemDto;
import com.springboot.online_store.services.CartItemService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartItemController.class)
class CartItemControllerTest {

    @MockitoBean
    private CartItemService cartItemService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Nested
    class CreateCartItem {
        @Test
        void createCartItemShouldWork() throws Exception {
            var createDto = builderCartItemDto();
            var infoDto = builderCartItemDto();

            when(cartItemService.createCartItem(createDto))
                    .thenReturn(infoDto);

            mockMvc.perform(post("/carts")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.productId").value(infoDto.productId()))
                    .andExpect(jsonPath("$.quantity").value(infoDto.quantity()));

            verify(cartItemService).createCartItem(createDto);
        }

        @Test
        void createCartItemCheckValidation() throws Exception {
            var createDto = builderInvalidCartItemDto();

            mockMvc.perform(post("/carts")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(createDto)))
                    .andExpect(status().isBadRequest());

            verify(cartItemService, never()).createCartItem(createDto);
        }
    }

    @Nested
    class GetAllCartItems {
        @Test
        void getAllCartItems() throws Exception {
            Integer pageSize = 10;
            Integer pageNumber = 0;
            var infoDto = builderCartItemDto();
            var infoDtoList = List.of(infoDto);

            when(cartItemService.getCartItems(pageSize, pageNumber))
                    .thenReturn(infoDtoList);

            mockMvc.perform(get("/carts")
                    .param("pageSize", pageSize.toString())
                    .param("pageNumber", pageNumber.toString()))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$[0].productId").value(infoDtoList.getFirst().productId()))
                    .andExpect(jsonPath("$[0].quantity").value(infoDtoList.getFirst().quantity()));

            verify(cartItemService)
                    .getCartItems(pageSize, pageNumber);
        }
    }

    @Nested
    class GetCartItemById {
        @Test
        void getCartItemByIdShouldWork() throws Exception {
            var infoDto = builderCartItemDto();
            Long id = 1L;

            when(cartItemService.getCartItemById(id))
                    .thenReturn(infoDto);

            mockMvc.perform(get("/carts/{id}", id))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.productId").value(infoDto.productId()))
                    .andExpect(jsonPath("$.quantity").value(infoDto.quantity()));

            verify(cartItemService).getCartItemById(id);
        }
        @Test
        void getCartItemByIdShouldThrowEntityNotFoundException() throws Exception {
            Long id = 1L;

            when(cartItemService.getCartItemById(id))
                    .thenThrow(new EntityNotFoundException());

            mockMvc.perform(get("/carts/{id}", id))
                    .andExpect(status().isNotFound());

            verify(cartItemService).getCartItemById(id);
        }
    }

    @Nested
    class DeleteCartItem {
        @Test
        void deleteCartItemShouldWork() throws Exception {
            Long id = 1L;

            mockMvc.perform(delete("/carts/{id}", id))
                    .andExpect(status().isNoContent());

            verify(cartItemService).deleteCartItem(id);
        }

        @Test
        void deleteCartItemShouldThrowEntityNotFoundException() throws Exception {
            Long id = 1L;

            doThrow(new EntityNotFoundException())
                    .when(cartItemService).deleteCartItem(id);

            mockMvc.perform(delete("/carts/{id}", id))
                    .andExpect(status().isNotFound());

            verify(cartItemService).deleteCartItem(id);
        }
    }

    @Nested
    class UpdateCartItem {
        @Test
        void updateCartItemShouldWork() throws Exception {
            var updateDto = builderUpdateCartItemDto();
            var infoDto = builderCartItemDto();
            Long id = 1L;

            when(cartItemService.updateCartItem(id, updateDto))
                    .thenReturn(infoDto);

            mockMvc.perform(put("/carts/{id}", id)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.quantity").value(infoDto.quantity()));

            verify(cartItemService).updateCartItem(id, updateDto);
        }
        @Test
        void updateCartItemCheckValidation() throws Exception {
            var updateDto = UpdateCartItemDto.builder()
                    .quantity(100000)
                    .build();
            Long id = 1L;

            mockMvc.perform(put("/carts/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isBadRequest());

            verify(cartItemService, never()).updateCartItem(id, updateDto);
        }

        @Test
        void updateCartItemShouldThrowEntityNotFoundException() throws Exception {
            var updateDto = builderUpdateCartItemDto();
            Long id = 1L;

            when(cartItemService.updateCartItem(id, updateDto))
                    .thenThrow(new EntityNotFoundException());

            mockMvc.perform(put("/carts/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(updateDto)))
                    .andExpect(status().isNotFound());

            verify(cartItemService).updateCartItem(id, updateDto);
        }

    }

    private CartItemDto builderCartItemDto(){
        return CartItemDto.builder()
                .productId(1L)
                .quantity(500)
                .build();
    }

    private CartItemDto builderInvalidCartItemDto(){
        return CartItemDto.builder()
                .productId(null)
                .quantity(100000)
                .build();
    }

    private UpdateCartItemDto builderUpdateCartItemDto(){
        return UpdateCartItemDto.builder()
                .quantity(500)
                .build();
    }

}