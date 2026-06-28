package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Role;

import java.util.UUID;

public record UserRequestDTO(
        String name,
        String email,
        String password,
        Role role
) {
}
