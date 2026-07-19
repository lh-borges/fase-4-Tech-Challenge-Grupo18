package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;

/**
 * Converte a entidade de domínio de avaliação no DTO de resposta.
 *
 * @author luisbraserv
 */
public class AvaliacaoMapper {

    /**
     * Converte a entidade de domínio no DTO de resposta, incluindo o nome da disciplina.
     *
     * @param  avaliacao  entidade de domínio
     * @param  disciplinaNome  nome da disciplina avaliada (pode ser nulo se não resolvido)
     * @return DTO de resposta, ou {@code null} se a avaliação for nula
     *
     * @author luisbraserv
     */
    public static AvaliacaoResponseDTO toResponse(Avaliacao avaliacao, String disciplinaNome) {
        if (avaliacao == null) {
            return null;
        }
        return new AvaliacaoResponseDTO(
                avaliacao.getId(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                avaliacao.getUrgencia(),
                avaliacao.getDataEnvio(),
                avaliacao.getDisciplinaId(),
                disciplinaNome
        );
    }
}
