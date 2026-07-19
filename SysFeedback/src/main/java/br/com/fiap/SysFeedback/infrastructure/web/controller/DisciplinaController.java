package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.dto.DisciplinaResponseDTO;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.usecase.DisciplinaFindUseCase;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Endpoint de disciplinas escopado pelo perfil do usuário autenticado.
 *
 * <ul>
 *     <li>ALUNO — disciplinas em que está matriculado (para escolher qual avaliar).</li>
 *     <li>PROFESSOR — disciplinas que leciona.</li>
 *     <li>ADMIN — todas as disciplinas.</li>
 * </ul>
 *
 * @author Danilo Fernando
 */
@RestController
@RequestMapping("/disciplinas")
@RequiredArgsConstructor
public class DisciplinaController {

    private final DisciplinaFindUseCase disciplinaFindUseCase;
    private final RepositoryUserPort repositoryUserPort;

    /**
     * Lista as disciplinas visíveis ao usuário autenticado conforme o perfil.
     *
     * @param  authentication  contexto de autenticação (fornece perfil e id)
     * @return resposta 200 com as disciplinas visíveis
     *
     * @author Danilo Fernando
     */
    @GetMapping
    public ResponseEntity<List<DisciplinaResponseDTO>> listar(Authentication authentication) {
        User usuario = repositoryUserPort.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedOperationException("Usuário autenticado não encontrado"));

        List<DisciplinaResponseDTO> disciplinas =
                disciplinaFindUseCase.execute(usuario.getRole(), usuario.getId());
        return ResponseEntity.ok(disciplinas);
    }
}
