package com.springboot.online_store.exceptions;

public class CategoryNotEmptyException extends RuntimeException{
    public CategoryNotEmptyException(String message){
        super(message);
    }
}
