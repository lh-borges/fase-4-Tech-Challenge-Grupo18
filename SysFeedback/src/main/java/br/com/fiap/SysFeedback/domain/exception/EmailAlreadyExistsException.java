package br.com.fiap.SysFeedback.domain.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
