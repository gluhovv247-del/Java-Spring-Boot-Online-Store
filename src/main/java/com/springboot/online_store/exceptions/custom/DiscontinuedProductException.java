package com.springboot.online_store.exceptions.custom;

public class DiscontinuedProductException extends RuntimeException {
    public DiscontinuedProductException(String message) {
        super(message);
    }
}
