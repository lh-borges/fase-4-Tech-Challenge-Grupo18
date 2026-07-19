package br.com.fiap.SysFeedback.application.dto;

import java.util.UUID;

/**
 * Representação de uma disciplina retornada ao cliente.
 *
 * @param  id  identificador da disciplina
 * @param  nome  nome da disciplina
 * @param  codigo  código curto identificador
 *
 * @author Danilo Fernando
 */
public record DisciplinaResponseDTO(UUID id, String nome, String codigo) {
}
