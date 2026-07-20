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

class AvaliacaoRequestDTOTest {

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
    void deveCriarAvaliacaoRequestDTOComCamposValidos() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTOValida();

        assertEquals(Fixture.DESCRICAO_AVALIACAO, dto.descricao());
        assertEquals(Fixture.NOTA_AVALIACAO, dto.nota());
        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void devePermitirNotaMinimaZero() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO("Descricao valida", 0);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void devePermitirNotaMaximaDez() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO("Descricao valida", 10);

        assertTrue(validator.validate(dto).isEmpty());
    }

    @Test
    void deveInvalidarDescricaoNula() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO(null, 8);

        Set<ConstraintViolation<AvaliacaoRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "descricao", "Descrição é obrigatória");
    }

    @Test
    void deveInvalidarDescricaoEmBranco() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO("   ", 8);

        Set<ConstraintViolation<AvaliacaoRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "descricao", "Descrição é obrigatória");
    }

    @Test
    void deveInvalidarDescricaoComMaisDeMilCaracteres() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO("a".repeat(1001), 8);

        Set<ConstraintViolation<AvaliacaoRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "descricao", "Descrição não pode passar de 1000 caracteres");
    }

    @Test
    void deveInvalidarNotaNula() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO("Descricao valida", null);

        Set<ConstraintViolation<AvaliacaoRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "nota", "Nota é obrigatória");
    }

    @Test
    void deveInvalidarNotaMenorQueZero() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO("Descricao valida", -1);

        Set<ConstraintViolation<AvaliacaoRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "nota", "Nota mínima é 0");
    }

    @Test
    void deveInvalidarNotaMaiorQueDez() {
        AvaliacaoRequestDTO dto = Fixture.avaliacaoRequestDTO("Descricao valida", 11);

        Set<ConstraintViolation<AvaliacaoRequestDTO>> violations = validator.validate(dto);

        assertViolation(violations, "nota", "Nota máxima é 10");
    }

    private void assertViolation(Set<ConstraintViolation<AvaliacaoRequestDTO>> violations,
                                 String field,
                                 String message) {
        assertEquals(1, violations.size());
        assertTrue(violations.stream().anyMatch(violation ->
                violation.getPropertyPath().toString().equals(field)
                        && violation.getMessage().equals(message)));
    }
}
