package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;
import br.com.fiap.SysFeedback.SysFeedbackApplication;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = SysFeedbackApplication.class)
@EntityScan(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.repository")
class AvaliacaoJpaRepositoryTest {

    @Autowired
    private AvaliacaoJpaRepository repository;

    @Test
    void deveSalvarEListarAvaliacao() {
        AvaliacaoJpaEntity entity = avaliacao("Aula excelente", 10, Urgencia.BAIXA,
                LocalDateTime.of(2026, 7, 10, 10, 0));

        AvaliacaoJpaEntity saved = repository.save(entity);
        List<AvaliacaoJpaEntity> result = repository.findAll();

        assertNotNull(saved.getId());
        assertEquals(1, result.size());
        assertEquals("Aula excelente", result.get(0).getDescricao());
        assertEquals(10, result.get(0).getNota());
        assertEquals(Urgencia.BAIXA, result.get(0).getUrgencia());
    }

    @Test
    void deveBuscarAvaliacoesPorPeriodo() {
        AvaliacaoJpaEntity dentroDoPeriodo = repository.save(avaliacao(
                "Dentro do periodo",
                8,
                Urgencia.MEDIA,
                LocalDateTime.of(2026, 7, 15, 10, 0)
        ));
        repository.save(avaliacao(
                "Fora do periodo",
                5,
                Urgencia.ALTA,
                LocalDateTime.of(2026, 8, 1, 10, 0)
        ));

        List<AvaliacaoJpaEntity> result = repository.findByDataEnvioBetween(
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 7, 31, 23, 59)
        );

        assertEquals(1, result.size());
        assertEquals(dentroDoPeriodo.getId(), result.get(0).getId());
        assertEquals("Dentro do periodo", result.get(0).getDescricao());
    }

    @Test
    void deveRetornarListaVaziaQuandoNaoHouverAvaliacoesNoPeriodo() {
        repository.save(avaliacao(
                "Avaliacao antiga",
                6,
                Urgencia.MEDIA,
                LocalDateTime.of(2026, 6, 1, 10, 0)
        ));

        List<AvaliacaoJpaEntity> result = repository.findByDataEnvioBetween(
                LocalDateTime.of(2026, 7, 1, 0, 0),
                LocalDateTime.of(2026, 7, 31, 23, 59)
        );

        assertEquals(List.of(), result);
    }

    @Test
    void deveFalharAoSalvarAvaliacaoSemDescricao() {
        AvaliacaoJpaEntity entity = avaliacao(null, 8, Urgencia.MEDIA,
                LocalDateTime.of(2026, 7, 10, 10, 0));

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    @Test
    void deveFalharAoSalvarAvaliacaoSemUrgencia() {
        AvaliacaoJpaEntity entity = avaliacao("Sem urgencia", 8, null,
                LocalDateTime.of(2026, 7, 10, 10, 0));

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    @Test
    void deveFalharAoSalvarAvaliacaoSemDataEnvio() {
        AvaliacaoJpaEntity entity = avaliacao("Sem data", 8, Urgencia.BAIXA, null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    private AvaliacaoJpaEntity avaliacao(String descricao, int nota, Urgencia urgencia, LocalDateTime dataEnvio) {
        AvaliacaoJpaEntity entity = new AvaliacaoJpaEntity();
        entity.setDescricao(descricao);
        entity.setNota(nota);
        entity.setUrgencia(urgencia);
        entity.setDataEnvio(dataEnvio);
        return entity;
    }
}
