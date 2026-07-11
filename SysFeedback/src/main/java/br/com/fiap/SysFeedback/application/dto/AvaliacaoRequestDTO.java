package br.com.fiap.SysFeedback.application.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Payload de entrada de uma avaliação (POST /avaliacoes).
 * Segue a referência do desafio: descrição e nota (0 a 10).
 */
public record AvaliacaoRequestDTO(

        @NotBlank(message = "Descrição é obrigatória")
        @Size(max = 1000, message = "Descrição não pode passar de 1000 caracteres")
        String descricao,

        // Integer (não int): distingue "campo ausente" (null -> rejeitado pelo @NotNull)
        // de "nota 0" enviada de propósito.
        @NotNull(message = "Nota é obrigatória")
        @Min(value = 0, message = "Nota mínima é 0")
        @Max(value = 10, message = "Nota máxima é 10")
        Integer nota
) {
}
