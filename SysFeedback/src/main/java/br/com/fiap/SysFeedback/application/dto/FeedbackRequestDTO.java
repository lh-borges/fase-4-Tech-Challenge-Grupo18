package br.com.fiap.SysFeedback.application.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Payload de entrada para geração de um feedback (POST /feedback).
 * O dono informa o período [inicio, fim] a ser consolidado.
 *
 * @param  inicio  início do período a consolidar (obrigatório)
 * @param  fim  fim do período a consolidar (obrigatório)
 *
 * @author luisbraserv
 */
public record FeedbackRequestDTO(

        @NotNull(message = "Data de início é obrigatória")
        LocalDateTime inicio,

        @NotNull(message = "Data de fim é obrigatória")
        LocalDateTime fim
) {
}
