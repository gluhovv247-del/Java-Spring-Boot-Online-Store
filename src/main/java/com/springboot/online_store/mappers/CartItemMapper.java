package com.springboot.online_store.mappers;

import com.springboot.online_store.dtos.cart.CartItemDto;
import com.springboot.online_store.entities.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CartItemMapper {
    @Mapping(source = "product.id", target = "productId")
    CartItemDto toCartItemDto(CartItem cartItem);
    @Mapping(source = "product.id", target = "productId")
    List<CartItemDto> toCartItemDtoList(List<CartItem> cartItem);
}
