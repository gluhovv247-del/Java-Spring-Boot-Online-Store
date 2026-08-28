package com.springboot.online_store.dtos.category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoryCreateDto(
        @NotBlank
        @Size(max = 50)
        String name,
        Long parentId
) {
}
