package com.springboot.online_store.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserLogin(
        @Email
        String email,
        @NotBlank
        String password
) {
}
