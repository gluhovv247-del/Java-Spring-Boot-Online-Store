package com.springboot.online_store.dtos;

import com.springboot.online_store.entities.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductMapper {
    public ProductInfoDto toProductInfoDto(Product product){
        return new ProductInfoDto(
                product.getId(),
                product.getName(),
                product.getPrice(),
                product.getQuantity(),
                product.getImageUrl(),
                product.getCategory(),
                product.getCreatedTime(),
                product.getUpdatedTime()
        );
    }
    public List<ProductInfoDto> toListOfProductInfoDto(List<Product> products){
        return products.stream()
                .map(product -> new ProductInfoDto(
                        product.getId(),
                        product.getName(),
                        product.getPrice(),
                        product.getQuantity(),
                        product.getImageUrl(),
                        product.getCategory(),
                        product.getCreatedTime(),
                        product.getUpdatedTime()
                )).toList();
    }
}
