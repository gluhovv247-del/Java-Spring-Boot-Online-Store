package com.springboot.online_store.dtos.user;

import com.springboot.online_store.enums.UserRole;

import java.time.LocalDate;

public record UserInfo(
        String firstName,
        String lastName,
        String email,
        String phoneNumber,
        UserRole role,
        LocalDate birthDate
) {
}
