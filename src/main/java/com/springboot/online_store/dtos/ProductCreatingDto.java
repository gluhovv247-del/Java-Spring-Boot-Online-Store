package com.springboot.online_store.dtos;

import com.springboot.online_store.entities.Category;

import java.math.BigDecimal;

public record ProductCreatingDto(
        String name,
        BigDecimal price,
        int quantity,
        String imageUrl,
        Category category
) {
}
