package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Role;

public record UserUpdateDTO(
        String name,
        String email,
        String password,
        Role role
) {
}
