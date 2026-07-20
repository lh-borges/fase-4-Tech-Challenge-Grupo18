package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Role;

/**
 * Dados de entrada para criação de um usuário.
 *
 * @param  name      nome do usuário
 * @param  email     endereço de e-mail do usuário
 * @param  password  senha em texto puro a ser codificada
 * @param  role      papel (perfil de acesso) do usuário
 *
 * @author Thiago de Jesus
 */
public record UserRequestDTO(
        String name,
        String email,
        String password,
        Role role
) {
}
