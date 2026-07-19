package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Role;

/**
 * Dados de entrada para atualização de um usuário existente.
 *
 * @param  name      novo nome do usuário
 * @param  email     novo endereço de e-mail do usuário
 * @param  password  nova senha em texto puro a ser codificada
 * @param  role      novo papel (perfil de acesso) do usuário
 *
 * @author Thiago de Jesus
 */
public record UserUpdateDTO(
        String name,
        String email,
        String password,
        Role role
) {
}
