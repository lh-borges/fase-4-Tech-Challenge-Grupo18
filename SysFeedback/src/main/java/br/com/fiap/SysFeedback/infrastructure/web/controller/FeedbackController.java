package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.dto.FeedbackRequestDTO;
import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.application.usecase.FeedbackFindAllUseCase;
import br.com.fiap.SysFeedback.application.usecase.FeedbackGenerateUseCase;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Endpoints de feedback consolidado (dono — roles PROFESSOR/ADMIN).
 *
 * <ul>
 *     <li>POST /feedback — gera o feedback do período [inicio, fim] com média e contagens.</li>
 *     <li>GET /feedback — lista os feedbacks já gerados.</li>
 * </ul>
 *
 * <p>As regras de autorização por role ficam centralizadas no SecurityConfig.</p>
 *
 * @author luisbraserv
 */
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackGenerateUseCase feedbackGenerateUseCase;
    private final FeedbackFindAllUseCase feedbackFindAllUseCase;

    /**
     * Gera o feedback consolidado do período informado.
     *
     * @param  request  período [inicio, fim] para o qual gerar o feedback
     * @return resposta 201 com o feedback gerado
     *
     * @author luisbraserv
     */
    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> generate(@Valid @RequestBody FeedbackRequestDTO request) {
        FeedbackResponseDTO response = feedbackGenerateUseCase.execute(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Lista todos os feedbacks já gerados.
     *
     * @return resposta 200 com a lista de feedbacks
     *
     * @author luisbraserv
     */
    @GetMapping
    public ResponseEntity<List<FeedbackResponseDTO>> findAll() {
        List<FeedbackResponseDTO> response = feedbackFindAllUseCase.execute();
        return ResponseEntity.ok(response);
    }
}
