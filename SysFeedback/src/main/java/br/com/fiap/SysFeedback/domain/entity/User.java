package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.domain.enums.Role;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;


/**
 * Entidade de domínio que representa um usuário do sistema.
 *
 * @author Thiago de Jesus
 */
@Getter
public class User {
    private UUID id;
    private String name;
    private String email;
    private String password;
    private Role role;
    private LocalDateTime createdAt;


    /**
     * Cria um novo usuário sem identificador, definindo a data de criação como o momento atual.
     *
     * @param  name      nome do usuário
     * @param  email     endereço de e-mail do usuário
     * @param  password  senha do usuário
     * @param  role      papel (perfil de acesso) do usuário
     *
     * @throws IllegalArgumentException  quando algum campo obrigatório é nulo ou vazio
     *
     * @author Thiago de Jesus
     */
    public User(String name, String email, String password, Role role) {

        NothingNotNull(name, email, password, role);

        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }


    /**
     * Reconstrói um usuário existente com todos os seus atributos.
     *
     * @param  id         identificador único do usuário
     * @param  name       nome do usuário
     * @param  email      endereço de e-mail do usuário
     * @param  password   senha do usuário
     * @param  role       papel (perfil de acesso) do usuário
     * @param  createdAt  data e hora de criação do usuário
     *
     * @throws IllegalArgumentException  quando algum campo obrigatório é nulo ou vazio
     *
     * @author Thiago de Jesus
     */
    public User(UUID id, String name, String email, String password, Role role, LocalDateTime createdAt) {

        NothingNotNull(name, email, password, role);

        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.role = role;
        this.createdAt = createdAt;
    }

    /**
     * Valida que os campos obrigatórios não são nulos nem vazios.
     *
     * @param  name      nome a ser validado
     * @param  email     e-mail a ser validado
     * @param  password  senha a ser validada
     * @param  role      papel a ser validado
     *
     * @throws IllegalArgumentException  quando algum campo obrigatório é nulo ou vazio
     *
     * @author Thiago de Jesus
     */
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

    /**
     * Define o identificador único do usuário.
     *
     * @param  id  identificador a ser atribuído
     *
     * @author Thiago de Jesus
     */
    public void setId(UUID id) {this.id = id;}

    /**
     * Define a senha do usuário.
     *
     * @param  password  senha a ser atribuída
     *
     * @author Thiago de Jesus
     */
    public void setPassword(String password) {this.password = password;}
}
