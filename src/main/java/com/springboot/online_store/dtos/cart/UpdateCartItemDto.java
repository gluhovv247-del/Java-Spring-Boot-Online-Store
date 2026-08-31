package com.springboot.online_store.dtos.cart;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateCartItemDto(
        @NotNull
        @Min(1)
        @Max(5000)
        int quantity
) {
}
