package com.springboot.online_store.controllers;

import com.springboot.online_store.dtos.cart.CartItemDto;
import com.springboot.online_store.dtos.cart.UpdateCartItemDto;
import com.springboot.online_store.services.CartItemService;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/carts")
public class CartItemController {
    private final CartItemService cartService;

    public CartItemController(CartItemService cartService) {
        this.cartService = cartService;
    }

    @PostMapping
    public ResponseEntity<CartItemDto> createCartItem(@Valid @RequestBody CartItemDto cartItemDto){
        log.info("create cart item = {}", cartItemDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cartService.createCartItem(cartItemDto));
    }

    @GetMapping
    public ResponseEntity<List<CartItemDto>> getAllCartItems(
            @RequestParam(required = false) Integer pageSize,
            @RequestParam(required = false) Integer pageNumber
    ){
        log.info("get all cart Items");
        return ResponseEntity.ok(cartService.getCartItems(pageSize, pageNumber));
    }

    @GetMapping("{id}")
    public ResponseEntity<CartItemDto> getCartItemById(@PathVariable("id") Long id){
        log.info("get cart item by id = {}", id);
        return ResponseEntity.ok(cartService.getCartItemById(id));
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> deleteCartItem(@PathVariable("id") Long id){
        log.info("delete cart item by id = {}", id);
        cartService.deleteCartItem(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("{id}")
    public ResponseEntity<CartItemDto> updateCartItem(
            @PathVariable("id") Long id,
            @Valid @RequestBody UpdateCartItemDto cartItemDto
    ){
        log.info("update cart item by id = {}", id);
        return ResponseEntity.ok(cartService.updateCartItem(id, cartItemDto));
    }
}
