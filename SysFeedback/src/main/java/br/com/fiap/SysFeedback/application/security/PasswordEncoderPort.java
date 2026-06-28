package br.com.fiap.SysFeedback.application.security;

public interface PasswordEncoderPort {

    String encode(String password);

    boolean matches(String rawPassword, String encodedPassword);

}
