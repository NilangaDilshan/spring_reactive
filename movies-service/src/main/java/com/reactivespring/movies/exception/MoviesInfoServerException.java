package com.reactivespring.movies.exception;

public class MoviesInfoServerException extends RuntimeException {
    private String message;

    public MoviesInfoServerException(String message) {
        super(message);
        this.message = message;
    }
}