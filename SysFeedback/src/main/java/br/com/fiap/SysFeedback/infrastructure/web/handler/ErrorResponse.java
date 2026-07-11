package br.com.fiap.SysFeedback.infrastructure.web.handler;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Corpo padrão de resposta de erro da API.
 *
 * @param timestamp momento do erro
 * @param status    código HTTP
 * @param error     descrição curta do status (ex.: "Bad Request")
 * @param message   mensagem legível do erro
 * @param path      rota que originou o erro
 * @param fields    erros por campo (apenas em falhas de validação; null caso contrário)
 */
public record ErrorResponse(
        LocalDateTime timestamp,
        int status,
        String error,
        String message,
        String path,
        Map<String, String> fields
) {
    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, null);
    }

    public static ErrorResponse ofValidation(int status, String error, String message,
                                             String path, Map<String, String> fields) {
        return new ErrorResponse(LocalDateTime.now(), status, error, message, path, fields);
    }
}
