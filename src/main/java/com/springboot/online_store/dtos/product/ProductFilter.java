package com.springboot.online_store.dtos.product;

import com.springboot.online_store.entities.Category;

public record ProductFilter(
        Integer pageSize,
        Integer pageNumber,
        Category category
) {
}
