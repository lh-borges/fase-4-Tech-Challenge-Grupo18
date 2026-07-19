package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.User;


/**
 * Conversor entre DTOs de usuário e a entidade de domínio {@link User}.
 *
 * @author Thiago de Jesus
 */
public class UserMapper {

    /**
     * Converte um DTO de requisição em uma entidade de domínio.
     *
     * @param  dto  dados de entrada do usuário
     * @return entidade de domínio correspondente, ou {@code null} se o DTO for nulo
     *
     * @author Thiago de Jesus
     */
    public static User toDomain(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

        return new User(
                dto.name(),
                dto.email(),
                dto.password(),
                dto.role()
        );
    }

    /**
     * Converte uma entidade de domínio em um DTO de resposta.
     *
     * @param  user  entidade de domínio do usuário
     * @return DTO de resposta correspondente, ou {@code null} se o usuário for nulo
     *
     * @author Thiago de Jesus
     */
    public static UserResponseDTO toResponse(User user) {
        if (user == null) {
            return null;
        }
        return new UserResponseDTO(
                user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRole(),
                user.getCreatedAt()
        );
    }
}