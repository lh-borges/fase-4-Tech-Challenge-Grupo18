package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import org.mapstruct.Mapper;

import java.util.UUID;

@Mapper(componentModel = "spring")
public interface AvaliacaoMapper {

    default Avaliacao toDomain(AvaliacaoRequestDTO dto) {
        return toDomain(dto, null);
    }

    default Avaliacao toDomain(AvaliacaoRequestDTO dto, UUID alunoId) {
        if (dto == null) {
            return null;
        }
        return new Avaliacao(dto.descricao(), dto.nota(), dto.disciplinaId(), alunoId);
    }

    default AvaliacaoResponseDTO toResponse(Avaliacao avaliacao) {
        return toResponse(avaliacao, null);
    }

    static AvaliacaoResponseDTO toResponse(Avaliacao avaliacao, String disciplinaNome) {
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
