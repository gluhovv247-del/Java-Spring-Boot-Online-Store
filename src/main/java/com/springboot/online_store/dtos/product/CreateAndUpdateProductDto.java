package com.springboot.online_store.dtos.product;

import com.springboot.online_store.entities.Category;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record CreateAndUpdateProductDto(
        @NotBlank
        @Size(max = 100)
        String name,

        @NotNull
        @DecimalMin(value = "0.01")
        @Digits(integer = 8, fraction = 2)
        BigDecimal price,

        @NotNull
        @Min(1)
        @Max(5000)
        int quantity,

        String imageUrl,

        Long categoryId
) {
}
