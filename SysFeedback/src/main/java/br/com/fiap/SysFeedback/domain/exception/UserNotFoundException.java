package br.com.fiap.SysFeedback.domain.exception;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {


    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }

}
