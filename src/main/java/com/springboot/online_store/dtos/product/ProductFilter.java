package com.springboot.online_store.dtos.product;

import com.springboot.online_store.entities.Category;
import jakarta.validation.constraints.Min;
import lombok.Builder;

@Builder
public record ProductFilter(
        @Min(1)
        Integer pageSize,
        @Min(0)
        Integer pageNumber,
        Category category
) {
}
