package com.springboot.online_store.dtos.category;

import com.springboot.online_store.entities.Category;
import lombok.Builder;

@Builder
public record CategoryInfoDto(
        String name,
        Category parent
) {
}
