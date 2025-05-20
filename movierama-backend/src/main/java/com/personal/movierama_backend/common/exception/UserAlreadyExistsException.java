package com.personal.movierama_backend.common.exception;

public class UserAlreadyExistsException extends RuntimeException {

    public UserAlreadyExistsException(String message) {
        super(message);
    }

    public static UserAlreadyExistsException forUsername(String username) {
        return new UserAlreadyExistsException("Username '" + username + "' is already taken.");
    }

    public static UserAlreadyExistsException forEmail(String email) {
        return new UserAlreadyExistsException("Email '" + email + "' is already registered.");
    }
}
