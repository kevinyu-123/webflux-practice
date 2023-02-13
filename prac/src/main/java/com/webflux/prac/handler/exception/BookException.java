package com.webflux.prac.handler.exception;

public class BookException extends RuntimeException{
    public BookException(String message) {
        super(message);
    }
}
