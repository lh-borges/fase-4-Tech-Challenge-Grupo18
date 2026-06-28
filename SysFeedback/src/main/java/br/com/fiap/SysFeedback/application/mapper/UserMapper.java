package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.User;


public class UserMapper {

    // UserRequestDTO (cliente) → User (domínio)
    public static User toDomain(UserRequestDTO dto) {
        if (dto == null) {
            return null;
        }

/*      debug para entender a parte da senha

        System.out.println("DEBUG - name: " + dto.name());
        System.out.println("DEBUG - email: " + dto.email());
        System.out.println("DEBUG - password: " + dto.password());
        System.out.println("DEBUG - role: " + dto.role());
*/
        return new User(
                dto.name(),
                dto.email(),
                dto.password(),
                dto.role()
        );
    }

    // User (domínio) → UserResponseDTO (cliente)
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