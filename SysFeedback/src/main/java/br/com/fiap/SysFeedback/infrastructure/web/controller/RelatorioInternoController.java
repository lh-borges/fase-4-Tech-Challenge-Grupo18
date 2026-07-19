package br.com.fiap.SysFeedback.infrastructure.web.controller;

import br.com.fiap.SysFeedback.application.dto.RelatorioSemanalDTO;
import br.com.fiap.SysFeedback.application.usecase.RelatorioSemanalGenerateUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * Endpoint interno (máquina-a-máquina) para geração do relatório semanal.
 *
 * <p>Diferente do {@code /feedback} (protegido por JWT de PROFESSOR/ADMIN), esta
 * rota é consumida pela Cloud Function {@code relatorio-semanal} e é protegida por
 * uma <strong>API key</strong> no header {@code X-Internal-Api-Key}
 * (ver {@code InternalApiKeyFilter}). Assim a função não precisa de credencial de
 * usuário.</p>
 *
 * <p>Sem parâmetros, considera os últimos 7 dias. Para testes/reprocessamento,
 * aceita {@code inicio} e {@code fim} (ISO-8601).</p>
 */
@RestController
@RequestMapping("/internal/relatorio")
@RequiredArgsConstructor
public class RelatorioInternoController {

    private final RelatorioSemanalGenerateUseCase relatorioSemanalGenerateUseCase;

    @PostMapping("/semanal")
    public ResponseEntity<RelatorioSemanalDTO> gerarSemanal(
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {

        LocalDateTime fimEfetivo = (fim != null) ? fim : LocalDateTime.now();
        LocalDateTime inicioEfetivo = (inicio != null) ? inicio : fimEfetivo.minusDays(7);

        return ResponseEntity.ok(relatorioSemanalGenerateUseCase.execute(inicioEfetivo, fimEfetivo));
    }
}
