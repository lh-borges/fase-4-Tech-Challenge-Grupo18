package br.com.fiap.SysFeedback.domain.exception;

/**
 * Exceção lançada quando se tenta cadastrar um e-mail já existente.
 *
 * @author Thiago de Jesus
 */
public class EmailAlreadyExistsException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem referente ao e-mail duplicado.
     *
     * @param  email  e-mail que já está cadastrado
     *
     * @author Thiago de Jesus
     */
    public EmailAlreadyExistsException(String email) {
        super("Email already exists: " + email);
    }
}
