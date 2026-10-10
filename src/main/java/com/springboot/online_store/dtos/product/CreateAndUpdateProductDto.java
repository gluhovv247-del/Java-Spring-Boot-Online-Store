package com.springboot.online_store.dtos.product;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
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

        @NotNull
        Long categoryId
) {
}
