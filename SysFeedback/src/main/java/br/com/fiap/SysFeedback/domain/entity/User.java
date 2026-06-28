package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;
import java.util.UUID;


@Getter
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime createdAt;


    public User(String name, String email, String password, Role role) {

        NothingNotNull(name, email, password, role);

        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }


    public User(UUID id, String name, String email, String password, Role role, LocalDateTime createdAt) {

        NothingNotNull(name, email, password, role);

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    private void NothingNotNull(String name, String email, String password, Role role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome não pode ser vazio");
        }
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email não pode ser vazio");
        }
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Senha não pode ser vazia");
        }
        if (role == null) {
            throw new IllegalArgumentException("Role não pode ser nula");
        }
    }

    public void setId(UUID id) {this.id = id;}
    public void setPassword(String password) {this.password = password;}
}
