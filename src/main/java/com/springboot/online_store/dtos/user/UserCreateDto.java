package com.springboot.online_store.dtos.user;

import com.springboot.online_store.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record UserCreateDto(
        @NotBlank
        @Size(max = 50)
        String firstName,
        @NotBlank
        @Size(max = 50)
        String lastName,
        @NotBlank
        @Email
        String email,
        @NotBlank
        String password,
        @NotBlank
        String phoneNumber,
        @NotNull
        UserRole role,
        LocalDate birthDate
) {
}
