package com.springboot.online_store.dtos.category;

import com.springboot.online_store.entities.Category;

public record CategoryInfoDto(
        String name,
        Category parent
) {
}
