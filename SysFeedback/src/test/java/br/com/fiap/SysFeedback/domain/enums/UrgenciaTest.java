package br.com.fiap.SysFeedback.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class UrgenciaTest {

    @Test
    void deveRetornarAltaParaNotasDeZeroAteTres() {
        assertEquals(Urgencia.ALTA, Urgencia.fromNota(0));
        assertEquals(Urgencia.ALTA, Urgencia.fromNota(3));
    }

    @Test
    void deveRetornarMediaParaNotasDeQuatroAteSeis() {
        assertEquals(Urgencia.MEDIA, Urgencia.fromNota(4));
        assertEquals(Urgencia.MEDIA, Urgencia.fromNota(6));
    }

    @Test
    void deveRetornarBaixaParaNotasDeSeteAteDez() {
        assertEquals(Urgencia.BAIXA, Urgencia.fromNota(7));
        assertEquals(Urgencia.BAIXA, Urgencia.fromNota(10));
    }

    @Test
    void deveManterOrdemDosValoresDoEnum() {
        assertArrayEquals(
                new Urgencia[]{Urgencia.BAIXA, Urgencia.MEDIA, Urgencia.ALTA},
                Urgencia.values()
        );
    }
}
