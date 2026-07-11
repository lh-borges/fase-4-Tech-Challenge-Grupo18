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
 */
@RestController
@RequestMapping("/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackGenerateUseCase feedbackGenerateUseCase;
    private final FeedbackFindAllUseCase feedbackFindAllUseCase;

    @PostMapping
    public ResponseEntity<FeedbackResponseDTO> generate(@Valid @RequestBody FeedbackRequestDTO request) {
        FeedbackResponseDTO response = feedbackGenerateUseCase.execute(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping
    public ResponseEntity<List<FeedbackResponseDTO>> findAll() {
        List<FeedbackResponseDTO> response = feedbackFindAllUseCase.execute();
        return ResponseEntity.ok(response);
    }
}
