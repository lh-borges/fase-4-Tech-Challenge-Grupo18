package br.com.fiap.SysFeedback.domain.exception;

import java.util.UUID;

/**
 * Exceção lançada quando um usuário não é encontrado.
 *
 * @author Thiago de Jesus
 */
public class UserNotFoundException extends RuntimeException {


    /**
     * Cria a exceção referente a um usuário não encontrado pelo e-mail.
     *
     * @param  email  e-mail que não corresponde a nenhum usuário
     *
     * @author Thiago de Jesus
     */
    public UserNotFoundException(String email) {
        super("User not found with email: " + email);
    }

    /**
     * Cria a exceção referente a um usuário não encontrado pelo identificador.
     *
     * @param  id  identificador que não corresponde a nenhum usuário
     *
     * @author Thiago de Jesus
     */
    public UserNotFoundException(UUID id) {
        super("User not found with id: " + id);
    }

}
