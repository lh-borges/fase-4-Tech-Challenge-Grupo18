package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoCreateUseCase;
import br.com.fiap.SysFeedback.application.usecase.AvaliacaoFindAllUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de avaliação.
 *
 * <ul>
 *     <li>POST /avaliacoes — usuário (role ALUNO) envia uma avaliação.</li>
 *     <li>GET /avaliacoes — dono (roles PROFESSOR/ADMIN) lê as avaliações.</li>
 * </ul>
 *
 * <p>As regras de autorização por role ficam centralizadas no SecurityConfig.</p>
 *
 * @author luisbraserv
 */
@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

    private final AvaliacaoCreateUseCase avaliacaoCreateUseCase;
    private final AvaliacaoFindAllUseCase avaliacaoFindAllUseCase;

    /**
     * Cria uma avaliação a partir do payload enviado.
     *
     * @param  request  dados da avaliação a criar
     * @return resposta 201 com a avaliação criada
     *
     * @author luisbraserv
     */
    @PostMapping
    public ResponseEntity<AvaliacaoResponseDTO> create(@Valid @RequestBody AvaliacaoRequestDTO request) {
        AvaliacaoResponseDTO response = avaliacaoCreateUseCase.execute(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Lista todas as avaliações cadastradas.
     *
     * @return resposta 200 com a lista de avaliações
     *
     * @author luisbraserv
     */
    @GetMapping
    public ResponseEntity<List<AvaliacaoResponseDTO>> findAll() {
        List<AvaliacaoResponseDTO> response = avaliacaoFindAllUseCase.execute();
        return ResponseEntity.ok(response);
    }
}
