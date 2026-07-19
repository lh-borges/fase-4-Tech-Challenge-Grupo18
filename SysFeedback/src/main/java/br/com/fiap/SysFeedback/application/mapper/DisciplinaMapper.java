package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.DisciplinaResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;

/**
 * Converte a entidade de domínio {@link Disciplina} no seu DTO de resposta.
 *
 * @author Danilo Fernando
 */
public class DisciplinaMapper {

    /**
     * Converte a disciplina de domínio no DTO de resposta.
     *
     * @param  disciplina  entidade de domínio
     * @return DTO de resposta, ou {@code null} se a entrada for nula
     *
     * @author Danilo Fernando
     */
    public static DisciplinaResponseDTO toResponse(Disciplina disciplina) {
        if (disciplina == null) {
            return null;
        }
        return new DisciplinaResponseDTO(disciplina.getId(), disciplina.getNome(), disciplina.getCodigo());
    }
}
