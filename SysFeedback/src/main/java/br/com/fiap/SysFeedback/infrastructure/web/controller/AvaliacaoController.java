package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoCreateUseCase;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoFindUseCase;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Endpoints de avaliação, agora vinculados à disciplina avaliada.
 *
 * <ul>
 *     <li>POST /avaliacoes — o ALUNO envia uma avaliação de uma disciplina em que está matriculado.</li>
 *     <li>GET /avaliacoes — PROFESSOR vê apenas as das disciplinas que leciona; ADMIN vê todas.
 *         Aceita {@code ?disciplinaId=} para ver uma disciplina separadamente.</li>
 * </ul>
 *
 * <p>O usuário autenticado é resolvido pelo e-mail presente no token JWT.</p>
 *
 * @author luisbraserv
 */
@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoCreateUseCase avaliacaoCreateUseCase;
    private final AvaliacaoFindUseCase avaliacaoFindUseCase;
    private final RepositoryUserPort repositoryUserPort;

    /**
     * Registra a avaliação do aluno autenticado para a disciplina informada.
     *
     * @param  request  dados da avaliação (descrição, nota, disciplina)
     * @param  authentication  contexto de autenticação (fornece o e-mail do aluno)
     * @return resposta 201 com a avaliação criada
     *
     * @author Danilo Fernando
     */
    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> create(@Valid @RequestBody AvaliacaoRequestDTO request,
                                                       Authentication authentication) {
        User aluno = usuarioAutenticado(authentication);
        AvaliacaoResponseDTO response = avaliacaoCreateUseCase.execute(request, aluno.getId());
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Lista as avaliações visíveis ao usuário conforme o perfil, opcionalmente
     * filtradas por disciplina.
     *
     * @param  disciplinaId  disciplina a filtrar (opcional)
     * @param  authentication  contexto de autenticação (fornece perfil e id)
     * @return resposta 200 com as avaliações visíveis
     *
     * @author Danilo Fernando
     */
    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> findAll(
            @RequestParam(required = false) UUID disciplinaId,
            Authentication authentication) {

        User usuario = usuarioAutenticado(authentication);
        List<AvaliacaoResponseDTO> response =
                avaliacaoFindUseCase.execute(usuario.getRole(), usuario.getId(), disciplinaId);
        return ResponseEntity.ok(response);
    }

    /**
     * Resolve o usuário autenticado a partir do e-mail contido no token.
     *
     * @param  authentication  contexto de autenticação atual
     * @return usuário autenticado
     *
     * @throws UnauthorizedOperationException  quando o usuário do token não é encontrado
     *
     * @author Danilo Fernando
     */
    private User usuarioAutenticado(Authentication authentication) {
        return repositoryUserPort.findByEmail(authentication.getName())
                .orElseThrow(() -> new UnauthorizedOperationException("Usuário autenticado não encontrado"));
    }
}
