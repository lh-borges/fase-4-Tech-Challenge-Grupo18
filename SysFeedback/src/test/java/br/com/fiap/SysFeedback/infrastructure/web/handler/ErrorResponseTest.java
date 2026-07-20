package br.com.fiap.SysFeedback.infrastructure.web.handler;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ErrorResponseTest {

    @Test
    void deveCriarErrorResponseComConstrutorDoRecord() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 7, 19, 20, 30);
        Map<String, String> fields = Map.of("email", "Email é obrigatório");

        ErrorResponse response = new ErrorResponse(
                timestamp,
                400,
                "Bad Request",
                "Erro de validação nos campos enviados",
                "/users",
                fields
        );

        assertEquals(timestamp, response.timestamp());
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Erro de validação nos campos enviados", response.message());
        assertEquals("/users", response.path());
        assertEquals(fields, response.fields());
    }

    @Test
    void deveCriarErrorResponseSemCamposDeValidacao() {
        LocalDateTime antes = LocalDateTime.now();

        ErrorResponse response = ErrorResponse.of(
                404,
                "Not Found",
                "Usuário não encontrado",
                "/users/email/teste@email.com"
        );

        LocalDateTime depois = LocalDateTime.now();

        assertFalse(response.timestamp().isBefore(antes));
        assertFalse(response.timestamp().isAfter(depois));
        assertEquals(404, response.status());
        assertEquals("Not Found", response.error());
        assertEquals("Usuário não encontrado", response.message());
        assertEquals("/users/email/teste@email.com", response.path());
        assertNull(response.fields());
    }

    @Test
    void deveCriarErrorResponseComCamposDeValidacao() {
        Map<String, String> fields = Map.of(
                "descricao", "Descrição é obrigatória",
                "nota", "Nota máxima é 10"
        );
        LocalDateTime antes = LocalDateTime.now();

        ErrorResponse response = ErrorResponse.ofValidation(
                400,
                "Bad Request",
                "Erro de validação nos campos enviados",
                "/avaliacoes",
                fields
        );

        LocalDateTime depois = LocalDateTime.now();

        assertFalse(response.timestamp().isBefore(antes));
        assertFalse(response.timestamp().isAfter(depois));
        assertEquals(400, response.status());
        assertEquals("Bad Request", response.error());
        assertEquals("Erro de validação nos campos enviados", response.message());
        assertEquals("/avaliacoes", response.path());
        assertEquals(fields, response.fields());
        assertEquals("Descrição é obrigatória", response.fields().get("descricao"));
        assertEquals("Nota máxima é 10", response.fields().get("nota"));
    }

    @Test
    void deveManterIgualdadeDoRecordQuandoTodosOsCamposForemIguais() {
        LocalDateTime timestamp = LocalDateTime.of(2026, 7, 19, 21, 0);

        ErrorResponse primeiro = new ErrorResponse(
                timestamp,
                500,
                "Internal Server Error",
                "Erro interno inesperado",
                "/test-error/generic",
                null
        );
        ErrorResponse segundo = new ErrorResponse(
                timestamp,
                500,
                "Internal Server Error",
                "Erro interno inesperado",
                "/test-error/generic",
                null
        );

        assertEquals(primeiro, segundo);
        assertEquals(primeiro.hashCode(), segundo.hashCode());
        assertTrue(primeiro.toString().contains("Internal Server Error"));
    }
}
