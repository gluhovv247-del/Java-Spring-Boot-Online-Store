package com.springboot.online_store.exceptions;

import java.time.LocalDateTime;

public record ErrorResponseDto (
        Object ex,
        String message,
        LocalDateTime errorTime
){
}
