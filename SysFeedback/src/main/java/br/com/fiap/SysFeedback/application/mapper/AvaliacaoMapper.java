package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;

/**
 * Converte entre o DTO de avaliação e a entidade de domínio.
 *
 * @author luisbraserv
 */
public class AvaliacaoMapper {

    /**
     * Converte o payload de requisição na entidade de domínio.
     *
     * @param  dto  payload recebido do cliente
     * @return avaliação de domínio, ou {@code null} se o dto for nulo
     *
     * @author luisbraserv
     */
    public static Avaliacao toDomain(AvaliacaoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Avaliacao(dto.descricao(), dto.nota());
    }

    /**
     * Converte a entidade de domínio no DTO de resposta.
     *
     * @param  avaliacao  entidade de domínio
     * @return DTO de resposta, ou {@code null} se a avaliação for nula
     *
     * @author luisbraserv
     */
    public static AvaliacaoResponseDTO toResponse(Avaliacao avaliacao) {
        if (avaliacao == null) {
            return null;
        }
        return new AvaliacaoResponseDTO(
                avaliacao.getId(),
                avaliacao.getDescricao(),
                avaliacao.getNota(),
                avaliacao.getUrgencia(),
                avaliacao.getDataEnvio()
        );
    }
}
