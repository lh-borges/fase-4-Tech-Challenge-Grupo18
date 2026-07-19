package br.com.fiap.SysFeedback.domain.exception;

/**
 * Lançada quando o período informado para geração de um feedback é inválido
 * (datas nulas ou fim anterior ao início). Mapeada para HTTP 400.
 *
 * @author luisbraserv
 */
public class PeriodoInvalidoException extends RuntimeException {

    /**
     * Cria a exceção com a mensagem de erro informada.
     *
     * @param  message  descrição do motivo da invalidez do período
     *
     * @author luisbraserv
     */
    public PeriodoInvalidoException(String message) {
        super(message);
    }
}
