package br.com.fiap.sysfeedback.functions.relatorio;

import io.quarkus.mailer.MockMailbox;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Testa a formatação e o envio do relatório semanal, usando o {@link MockMailbox}.
 * A busca HTTP no backend é testada em integração (não aqui).
 */
@QuarkusTest
class RelatorioSemanalFunctionTest {

    @Inject
    RelatorioSemanalFunction function;

    @Inject
    MockMailbox mailbox;

    @BeforeEach
    void limparCaixa() {
        mailbox.clear();
    }

    private RelatorioSemanal relatorioExemplo() {
        return new RelatorioSemanal(
                "2026-07-12T00:00:00",
                "2026-07-19T00:00:00",
                5.5,
                4,
                Map.of("2026-07-15", 2L, "2026-07-17", 2L),
                Map.of("ALTA", 1L, "MEDIA", 2L, "BAIXA", 1L),
                List.of(
                        new RelatorioSemanal.Item("Aula confusa", "ALTA", "2026-07-15T10:00:00"),
                        new RelatorioSemanal.Item("Boa aula", "BAIXA", "2026-07-17T14:00:00")));
    }

    @Test
    void corpoContemMediaContagensEDetalhe() {
        String corpo = function.montarCorpo(relatorioExemplo());

        assertTrue(corpo.contains("5,50") || corpo.contains("5.50"), "deve conter a media");
        assertTrue(corpo.contains("ALTA"), "deve conter contagem por urgencia");
        assertTrue(corpo.contains("Aula confusa"), "deve listar a avaliacao");
        assertTrue(corpo.contains("Total"), "deve conter o total");
    }

    @Test
    void enviaEmailDoRelatorioParaAdmin() {
        function.enviar(relatorioExemplo());

        var enviados = mailbox.getMessagesSentTo("admin@fiap.com");
        assertEquals(1, enviados.size(), "deveria enviar um e-mail ao admin");
        assertTrue(enviados.get(0).getSubject().contains("Relatorio semanal"));
    }
}
