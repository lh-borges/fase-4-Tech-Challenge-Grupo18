package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Representação de uma avaliação retornada ao cliente, incluindo a urgência
 * derivada da nota, a data de envio e a disciplina avaliada.
 *
 * @param  id  identificador da avaliação
 * @param  descricao  texto livre da avaliação
 * @param  nota  nota atribuída (0 a 10)
 * @param  urgencia  urgência derivada da nota
 * @param  dataEnvio  data e hora do envio
 * @param  disciplinaId  identificador da disciplina avaliada
 * @param  disciplinaNome  nome da disciplina avaliada
 *
 * @author luisbraserv
 */
public record AvaliacaoResponseDTO(
        UUID id,
        String descricao,
        int nota,
        Urgencia urgencia,
        LocalDateTime dataEnvio,
        UUID disciplinaId,
        String disciplinaNome
) {
}
