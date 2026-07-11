package br.com.fiap.SysFeedback.domain.exception;

/**
 * Lançada quando um usuário autenticado tenta executar uma operação que não lhe
 * é permitida (ex.: alterar os dados de outro usuário). Mapeada para HTTP 403.
 */
public class UnauthorizedOperationException extends RuntimeException {

    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
