package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.dto.UserUpdateDTO;
import br.com.fiap.SysFeedback.application.mapper.UserMapper;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

public class UserUpdateUseCase {

    private final RepositoryUserPort repositoryUserPort;
    private final PasswordEncoderPort passwordEncoderPort;

    public UserUpdateUseCase(
            RepositoryUserPort repositoryUserPort,
            PasswordEncoderPort passwordEncoderPort) {

        this.repositoryUserPort = repositoryUserPort;
        this.passwordEncoderPort = passwordEncoderPort;
    }

    public UserResponseDTO execute(UUID id, UserUpdateDTO userUpdateDTO) {
        User existingUser = repositoryUserPort.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found"));

        //--------------------
        //regra para não permitir que um usuário comum altere os dados de outro usuário
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String emailLogado = authentication.getName();

        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !existingUser.getEmail().equals(emailLogado)) {
            throw new RuntimeException("You don't have permission to change another user's data.");
        }else {
            //--------------------
            User updatedUser = new User(
                    id,
                    userUpdateDTO.name(),
                    userUpdateDTO.email(),
                    passwordEncoderPort.encode(userUpdateDTO.password()),
                    userUpdateDTO.role(),
                    existingUser.getCreatedAt()
            );

            User savedUser = repositoryUserPort.save(updatedUser);
            return UserMapper.toResponse(savedUser);
        }
    }
}