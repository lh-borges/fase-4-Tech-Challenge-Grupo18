package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;

public class AvaliacaoMapper {

    // AvaliacaoRequestDTO (cliente) → Avaliacao (domínio)
    public static Avaliacao toDomain(AvaliacaoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Avaliacao(dto.descricao(), dto.nota());
    }

    // Avaliacao (domínio) → AvaliacaoResponseDTO (cliente)
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
