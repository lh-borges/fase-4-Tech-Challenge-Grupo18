package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AvaliacaoMapper {

    // AvaliacaoRequestDTO (cliente) -> Avaliacao (dominio)
    default Avaliacao toDomain(AvaliacaoRequestDTO dto) {
        if (dto == null) {
            return null;
        }
        return new Avaliacao(dto.descricao(), dto.nota());
    }

    // Avaliacao (dominio) -> AvaliacaoResponseDTO (cliente)
    AvaliacaoResponseDTO toResponse(Avaliacao avaliacao);
}
