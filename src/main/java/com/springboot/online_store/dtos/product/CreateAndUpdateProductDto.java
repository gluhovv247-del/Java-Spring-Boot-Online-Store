package com.springboot.online_store.dtos.product;

import com.springboot.online_store.enums.ProductStatus;
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
        @Min(0)
        @Max(10000)
        int quantity,

        String imageUrl,

        @NotNull
        Long categoryId,

        @NotNull
        ProductStatus status
) {
}
