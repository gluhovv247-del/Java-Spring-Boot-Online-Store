package com.springboot.online_store.dtos.product;

import com.springboot.online_store.entities.Category;

import java.math.BigDecimal;

public record ProductSearchFilter(
        Integer pageSize,
        Integer pageNumber,
        String name,
        Category category,
        BigDecimal minPrice,
        BigDecimal maxPrice
) {
}
