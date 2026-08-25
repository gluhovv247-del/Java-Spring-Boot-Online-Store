package com.springboot.online_store.dtos;

import com.springboot.online_store.entities.Category;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ProductInfoDto(
        Long id,
        String name,
        BigDecimal price,
        int quantity,
        String imageUrl,
        Category category,
        LocalDateTime createdTime,
        LocalDateTime updatedTime
) {
}
