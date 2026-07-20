package br.com.fiap.SysFeedback.infrastructure.web.handler;

import br.com.fiap.SysFeedback.domain.exception.AvaliacaoInvalidaException;
import br.com.fiap.SysFeedback.domain.exception.EmailAlreadyExistsException;
import br.com.fiap.SysFeedback.domain.exception.PeriodoInvalidoException;
import br.com.fiap.SysFeedback.domain.exception.UnauthorizedOperationException;
import br.com.fiap.SysFeedback.domain.exception.UserNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

/**
 * Tratamento centralizado de exceções para todos os controllers da API.
 * Traduz exceções de domínio, de validação e de framework em respostas HTTP
 * consistentes ({@link ErrorResponse}), evitando vazamento de stack traces e
 * HTTP 500 genérico para erros que são, na verdade, do cliente.
 *
 * @author luisbraserv
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata recurso de domínio não encontrado, retornando 404.
     *
     * @param  ex  exceção de usuário não encontrado
     * @param  request  requisição que originou o erro
     * @return resposta 404 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 404 - recurso de domínio não encontrado
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex,
                                                        HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    /**
     * Trata rota inexistente, retornando 404 em vez de cair no fallback 500.
     *
     * @param  ex  exceção de recurso/rota não encontrado
     * @param  request  requisição que originou o erro
     * @return resposta 404 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 404 - rota inexistente (evita virar 500 no catch-all)
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorResponse> handleNoResource(NoResourceFoundException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.NOT_FOUND, "Recurso não encontrado", request);
    }

    /**
     * Trata conflito de domínio (recurso já existe), retornando 409.
     *
     * @param  ex  exceção de e-mail já cadastrado
     * @param  request  requisição que originou o erro
     * @return resposta 409 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 409 - conflito de domínio (recurso já existe)
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse> handleConflict(EmailAlreadyExistsException ex,
                                                        HttpServletRequest request) {
        return build(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    /**
     * Trata violação de integridade no banco, retornando 409.
     *
     * @param  ex  exceção de violação de integridade de dados
     * @param  request  requisição que originou o erro
     * @return resposta 409 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 409 - violação de integridade no banco (ex.: corrida na unicidade de e-mail)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                            HttpServletRequest request) {
        log.warn("Violação de integridade em {}: {}", request.getRequestURI(), ex.getMessage());
        return build(HttpStatus.CONFLICT,
                "Registro duplicado ou violação de restrição do banco", request);
    }

    /**
     * Trata operação não permitida ao usuário autenticado, retornando 403.
     *
     * @param  ex  exceção de operação não autorizada ou acesso negado
     * @param  request  requisição que originou o erro
     * @return resposta 403 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 403 - operação não permitida ao usuário autenticado
    @ExceptionHandler({UnauthorizedOperationException.class, AccessDeniedException.class})
    public ResponseEntity<ErrorResponse> handleForbidden(RuntimeException ex,
                                                         HttpServletRequest request) {
        return build(HttpStatus.FORBIDDEN, ex.getMessage(), request);
    }

    /**
     * Trata dados de domínio inválidos, retornando 400.
     *
     * @param  ex  exceção de validação de domínio ou argumento ilegal
     * @param  request  requisição que originou o erro
     * @return resposta 400 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 400 - dados de domínio inválidos
    @ExceptionHandler({
            AvaliacaoInvalidaException.class,
            PeriodoInvalidoException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponse> handleBadRequest(RuntimeException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    /**
     * Trata corpo de requisição ausente ou mal formatado, retornando 400.
     *
     * @param  ex  exceção de mensagem HTTP não legível
     * @param  request  requisição que originou o erro
     * @return resposta 400 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 400 - corpo ausente/mal formatado (JSON inválido, data em formato errado)
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Corpo da requisição ausente ou mal formatado", request);
    }

    /**
     * Trata parâmetro de rota/query com tipo incompatível, retornando 400.
     *
     * @param  ex  exceção de incompatibilidade de tipo de argumento
     * @param  request  requisição que originou o erro
     * @return resposta 400 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 400 - parâmetro de rota/query com tipo incompatível (ex.: id não-UUID)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        return build(HttpStatus.BAD_REQUEST,
                "Parâmetro '" + ex.getName() + "' com valor inválido", request);
    }

    /**
     * Trata método HTTP não suportado na rota, retornando 405.
     *
     * @param  ex  exceção de método HTTP não suportado
     * @param  request  requisição que originou o erro
     * @return resposta 405 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 405 - método HTTP não suportado na rota
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErrorResponse> handleMethodNotSupported(HttpRequestMethodNotSupportedException ex,
                                                                 HttpServletRequest request) {
        return build(HttpStatus.METHOD_NOT_ALLOWED,
                "Método HTTP não suportado para esta rota", request);
    }

    /**
     * Trata falha de validação de payload ({@code @Valid}), retornando 400 com os erros por campo.
     *
     * @param  ex  exceção de validação de argumentos do método
     * @param  request  requisição que originou o erro
     * @return resposta 400 com o corpo de erro detalhado por campo
     *
     * @author luisbraserv
     */
    // 400 - falha de validação de payload (@Valid)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        Map<String, String> fields = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                fields.put(error.getField(), error.getDefaultMessage()));

        ErrorResponse body = ErrorResponse.ofValidation(
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Erro de validação nos campos enviados",
                request.getRequestURI(),
                fields
        );
        return ResponseEntity.badRequest().body(body);
    }

    /**
     * Trata qualquer erro não previsto, registrando o log e retornando 500.
     *
     * @param  ex  exceção não mapeada pelos demais handlers
     * @param  request  requisição que originou o erro
     * @return resposta 500 com o corpo de erro padrão
     *
     * @author luisbraserv
     */
    // 500 - fallback para qualquer erro não previsto (logado para diagnóstico)
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex,
                                                       HttpServletRequest request) {
        log.error("Erro não tratado em {}: {}", request.getRequestURI(), ex.getMessage(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR,
                "Erro interno inesperado", request);
    }

    /**
     * Monta a resposta HTTP de erro padronizada a partir do status e da mensagem.
     *
     * @param  status  status HTTP a retornar
     * @param  message  mensagem legível do erro
     * @param  request  requisição que originou o erro, usada para obter a rota
     * @return resposta com o corpo {@link ErrorResponse} e o status informado
     *
     * @author luisbraserv
     */
    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message,
                                                HttpServletRequest request) {
        ErrorResponse body = ErrorResponse.of(
                status.value(),
                status.getReasonPhrase(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}
