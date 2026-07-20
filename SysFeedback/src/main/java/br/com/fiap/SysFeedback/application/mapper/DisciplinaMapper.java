package br.com.fiap.SysFeedback.application.mapper;

import br.com.fiap.SysFeedback.application.dto.DisciplinaResponseDTO;
import br.com.fiap.SysFeedback.domain.entity.Disciplina;
import org.mapstruct.Mapper;

/**
 * Converte a entidade de domínio {@link Disciplina} no seu DTO de resposta.
 */
@Mapper(componentModel = "spring")
public interface DisciplinaMapper {

    DisciplinaResponseDTO toResponse(Disciplina disciplina);
}
