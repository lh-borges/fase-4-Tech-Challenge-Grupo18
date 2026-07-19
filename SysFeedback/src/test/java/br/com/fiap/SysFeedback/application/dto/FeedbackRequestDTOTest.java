package br.com.fiap.SysFeedback.application.dto;

import br.com.fiap.SysFeedback.fixture.Fixture;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FeedbackRequestDTOTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void setUpValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidator() {
        validatorFactory.close();
    }

    @Test
    void deveCriarFeedbackRequestDTOComPeriodoValido() {
        FeedbackRequestDTO dto = Fixture.feedbackRequestDTOValido();

        assertEquals(Fixture.PERIODO_INICIO, dto.inicio());
        assertEquals(Fixture.PERIODO_FIM, dto.fim());
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void deveInvalidarInicioNulo() {
        FeedbackRequestDTO dto = Fixture.feedbackRequestDTO(null, Fixture.PERIODO_FIM);

        Set<ConstraintViolation<FeedbackRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "inicio", "Data de início é obrigatória");
    }

    @Test
    void deveInvalidarFimNulo() {
        FeedbackRequestDTO dto = Fixture.feedbackRequestDTO(Fixture.PERIODO_INICIO, null);

        Set<ConstraintViolation<FeedbackRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "fim", "Data de fim é obrigatória");
    }

    @Test
    void deveInvalidarInicioEFimNulos() {
        FeedbackRequestDTO dto = Fixture.feedbackRequestDTO(null, null);

        Set<ConstraintViolation<FeedbackRequestDTO>> violations = validator.validate(dto);

        assertEquals(2, violations.size());
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("inicio")
                        && violation.getMessage().equals("Data de início é obrigatória")));
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals("fim")
                        && violation.getMessage().equals("Data de fim é obrigatória")));
    }

    @Test
    void devePermitirFimAnteriorAoInicioPorNaoSerRegraDoDTO() {
        FeedbackRequestDTO dto = Fixture.feedbackRequestDTO(
                Fixture.PERIODO_FIM,
                Fixture.PERIODO_INICIO
        );

        assertTrue(validator.validate(dto).isEmpty());
    }

    private void assertViolation(Set<ConstraintViolation<FeedbackRequestDTO>> violations,
                                 String field,
                                 String message) {
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals(field)
                        && violation.getMessage().equals(message)));
    }
}
