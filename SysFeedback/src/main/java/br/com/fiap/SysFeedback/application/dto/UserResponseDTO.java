package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Role;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Dados de saída que representam um usuário retornado ao cliente.
 *
 * @param  id         identificador único do usuário
 * @param  name       nome do usuário
 * @param  email      endereço de e-mail do usuário
 * @param  role       papel (perfil de acesso) do usuário
 * @param  createdAt  data e hora de criação do usuário
 *
 * @author Thiago de Jesus
 */
public record UserResponseDTO(
        UUID id,
        String name,
        String email,
        Role role,
        LocalDateTime createdAt
) {
}
