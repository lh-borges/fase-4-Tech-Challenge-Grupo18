package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.dto.UserUpdateDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class UserUpdateUseCase {

    private final RepositoryUserPort repositoryUserPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final UserMapper userMapper;

    public UserUpdateUseCase(
            RepositoryUserPort repositoryUserPort,
            PasswordEncoderPort passwordEncoderPort,
            UserMapper userMapper) {

        this.repositoryUserPort = repositoryUserPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.userMapper = userMapper;
    }

    public UserResponseDTO execute(UUID id, UserUpdateDTO userUpdateDTO) {
        User existingUser = repositoryUserPort.findById(id)
                .orElseThrow(() -> new UserNotFoundException(id));

        //--------------------
        //regra para não permitir que um usuário comum altere os dados de outro usuário
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !existingUser.getEmail().equals(emailLogado)) {
            throw new UnauthorizedOperationException("You don't have permission to change another user's data.");
        }

        // Só ADMIN pode alterar a role; usuário comum mantém a role atual
        // (evita auto-escalonamento de privilégio ao editar o próprio cadastro).
        Role role = isAdmin ? userUpdateDTO.role() : existingUser.getRole();

        User updatedUser = new User(
                id,
                userUpdateDTO.name(),
                userUpdateDTO.email(),
                passwordEncoderPort.encode(userUpdateDTO.password()),
                role,
                existingUser.getCreatedAt()
        );

        User savedUser = repositoryUserPort.save(updatedUser);
        return userMapper.toResponse(savedUser);
    }
}
