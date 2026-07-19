package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // UserRequestDTO (cliente) -> User (dominio)
    default User toDomain(UserRequestDTO dto) {
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

    // User (dominio) -> UserResponseDTO (cliente)
    UserResponseDTO toResponse(User user);
}
