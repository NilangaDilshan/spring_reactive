package com.reactivespring.moviesinfo.exception;

public class MovieInfoNotfoundException extends RuntimeException {
    private String message;

    public MovieInfoNotfoundException(String message) {
        super(message);
        this.message = message;
    }
}