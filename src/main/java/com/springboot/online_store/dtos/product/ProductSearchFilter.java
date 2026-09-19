package com.springboot.online_store.dtos.product;

import com.springboot.online_store.entities.Category;
import jakarta.validation.constraints.Min;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record ProductSearchFilter(
        @Min(1)
        Integer pageSize,
        @Min(0)
        Integer pageNumber,
        String name,
        Category category,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
