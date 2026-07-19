package br.com.fiap.SysFeedback.domain.exception;

/**
 * Lançada quando os dados de uma avaliação são inválidos
 * (descrição vazia ou nota fora do intervalo permitido). Mapeada para HTTP 400.
 *
 * @author luisbraserv
 */
public class AvaliacaoInvalidaException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem de erro informada.
     *
     * @param  message  descrição do motivo da invalidez
     *
     * @author luisbraserv
     */
    public AvaliacaoInvalidaException(String message) {
        super(message);
    }
}
