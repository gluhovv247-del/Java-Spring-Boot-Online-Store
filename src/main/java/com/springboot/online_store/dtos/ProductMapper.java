package com.springboot.online_store.dtos;

import com.springboot.online_store.entities.Product;
import org.springframework.stereotype.Component;

@Component
public class ProductMapper {
    public ProductInfoDto productInfoMapping(Product product){
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
}
