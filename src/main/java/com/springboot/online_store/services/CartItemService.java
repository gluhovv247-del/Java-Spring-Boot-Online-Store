package com.springboot.online_store.services;

import com.springboot.online_store.constants.BusinessConstants;
import com.springboot.online_store.dtos.cart.CartItemDto;
import com.springboot.online_store.dtos.cart.UpdateCartItemDto;
import com.springboot.online_store.entities.CartItem;
import com.springboot.online_store.exceptions.custom.InsufficientStockException;
import com.springboot.online_store.mappers.CartItemMapper;
import com.springboot.online_store.repositories.CartItemRepository;
import com.springboot.online_store.repositories.CartRepository;
import com.springboot.online_store.repositories.ProductRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class CartItemService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final CartItemMapper mapper;

    public CartItemService(CartItemRepository cartItemRepository,
                           ProductRepository productRepository,
                           CartRepository cartRepository,
                           CartItemMapper mapper
    ) {
        this.cartItemRepository = cartItemRepository;
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.mapper = mapper;
    }

    public CartItemDto createCartItem(CartItemDto cartItemDto) {
        var product = productRepository.findById(cartItemDto.productId())
                .orElseThrow(EntityNotFoundException::new);
        var cart = cartRepository.findById(1L)
                .orElseThrow(EntityNotFoundException::new);

        if(product.getQuantity() < cartItemDto.quantity()) {
            throw new InsufficientStockException("Not enough products in stock");
        }

        CartItem cartItem = new CartItem(
                cartItemDto.quantity(),
                cart,
                product
        );

        cartItemRepository.save(cartItem);
        return mapper.toCartItemDto(cartItem);
    }

    public List<CartItemDto> getCartItems(Integer pageSize, Integer pageNumber) {
        if(pageSize == null){
            pageSize = BusinessConstants.DEFAULT_PAGE_SIZE;
        }
        if(pageNumber == null){
            pageNumber = BusinessConstants.DEFAULT_PAGE_NUMBER;
        }

        var pageable = Pageable.ofSize(pageSize).withPage(pageNumber);
        var cartItems = cartItemRepository.findAll(pageable).stream().toList();
        return mapper.toCartItemDtoList(cartItems);
    }

    public void deleteCartItem(Long id) {
        var cartItem = cartItemRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        cartItemRepository.delete(cartItem);
    }

    @Transactional
    public CartItemDto updateCartItem(Long id, UpdateCartItemDto cartItemDto) {
        var cartItem = cartItemRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        var product = cartItem.getProduct();

        if(product.getQuantity() < cartItemDto.quantity()){
            throw new InsufficientStockException("Not enough products in stock");
        }

        cartItem.setQuantity(cartItemDto.quantity());

        return mapper.toCartItemDto(cartItem);
    }

    public CartItemDto getCartItemById(Long id) {
        var cartItem = cartItemRepository.findById(id)
                .orElseThrow(EntityNotFoundException::new);
        return mapper.toCartItemDto(cartItem);
    }
}
