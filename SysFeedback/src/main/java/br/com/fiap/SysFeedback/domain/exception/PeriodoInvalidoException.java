package br.com.fiap.SysFeedback.domain.exception;

/**
 * Lançada quando o período informado para geração de um feedback é inválido
 * (datas nulas ou fim anterior ao início). Mapeada para HTTP 400.
 */
public class PeriodoInvalidoException extends RuntimeException {

    public PeriodoInvalidoException(String message) {
        super(message);
    }
}
