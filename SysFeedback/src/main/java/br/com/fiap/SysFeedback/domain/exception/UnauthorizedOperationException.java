package br.com.fiap.SysFeedback.domain.exception;

/**
 * Lançada quando um usuário autenticado tenta executar uma operação que não lhe
 * é permitida (ex.: alterar os dados de outro usuário). Mapeada para HTTP 403.
 *
 * @author luisbraserv
 */
public class UnauthorizedOperationException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem de erro informada.
     *
     * @param  message  descrição da operação não autorizada
     *
     * @author luisbraserv
     */
    public UnauthorizedOperationException(String message) {
        super(message);
    }
}
