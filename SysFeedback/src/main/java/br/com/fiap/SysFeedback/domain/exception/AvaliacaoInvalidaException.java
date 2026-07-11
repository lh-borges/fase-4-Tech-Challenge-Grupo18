package br.com.fiap.SysFeedback.domain.exception;

/**
 * Lançada quando os dados de uma avaliação são inválidos
 * (descrição vazia ou nota fora do intervalo permitido). Mapeada para HTTP 400.
 */
public class AvaliacaoInvalidaException extends RuntimeException {

    public AvaliacaoInvalidaException(String message) {
        super(message);
    }
}
