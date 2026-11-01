package com.springboot.online_store.dtos.cart;

import jakarta.validation.constraints.*;
import lombok.Builder;

@Builder
public record CartItemDto(
        @NotNull
        Long productId,
        @NotNull
        @Min(1)
        @Max(5000)
        int quantity
) {
}
