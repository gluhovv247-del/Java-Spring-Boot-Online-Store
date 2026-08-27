package com.springboot.online_store.exceptions;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleException(Exception ex){
        var responseDto = new ErrorResponseDto(
                ex.getClass(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.info("internal server exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseDto);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleNotFoundException(Exception ex){
        var responseDto = new ErrorResponseDto(
                ex.getClass(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.info("not found exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }

    @ExceptionHandler(
            exception = {
                    MethodArgumentNotValidException.class,
                    ConstraintViolationException.class,
                    HttpMessageNotReadableException.class
            }
    )
    public ResponseEntity<ErrorResponseDto> handleBadRequestException(Exception ex){
        var responseDto = new ErrorResponseDto(
                ex.getClass(),
                ex.getMessage(),
                LocalDateTime.now()
        );
        log.info("bad request exception: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(responseDto);
    }
}
