package com.springboot.online_store.mappers;

import com.springboot.online_store.dtos.product.CreateAndUpdateProductDto;
import com.springboot.online_store.dtos.product.ProductInfoDto;
import com.springboot.online_store.entities.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.util.List;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    ProductInfoDto toProductInfoDto(Product product);
    List<ProductInfoDto> toProductInfoDtoList(List<Product> products);
}
