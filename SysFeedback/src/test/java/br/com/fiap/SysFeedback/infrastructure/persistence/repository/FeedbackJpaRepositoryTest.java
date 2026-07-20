package br.com.fiap.SysFeedback.infrastructure.persistence.repository;

import br.com.fiap.SysFeedback.SysFeedbackApplication;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.FeedbackJpaEntity;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DataJpaTest
@ContextConfiguration(classes = SysFeedbackApplication.class)
@EntityScan(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.entity")
@EnableJpaRepositories(basePackages = "br.com.fiap.SysFeedback.infrastructure.persistence.repository")
class FeedbackJpaRepositoryTest {

    @Autowired
    private FeedbackJpaRepository repository;

    @Test
    void deveSalvarEListarFeedbackComMapas() {
        FeedbackJpaEntity entity = feedback();

        FeedbackJpaEntity saved = repository.save(entity);
        List<FeedbackJpaEntity> result = repository.findAll();

        assertNotNull(saved.getId());
        assertEquals(1, result.size());
        assertEquals(8.5, result.get(0).getMediaNotas());
        assertEquals(3L, result.get(0).getTotalAvaliacoes());
        assertEquals(2L, result.get(0).getAvaliacoesPorDia().get(LocalDate.of(2026, 7, 10)));
        assertEquals(1L, result.get(0).getAvaliacoesPorUrgencia().get(Urgencia.ALTA));
    }

    @Test
    void deveBuscarFeedbackPorId() {
        FeedbackJpaEntity saved = repository.save(feedback());

        FeedbackJpaEntity result = repository.findById(saved.getId()).orElseThrow();

        assertEquals(saved.getId(), result.getId());
        assertEquals(LocalDateTime.of(2026, 7, 1, 0, 0), result.getPeriodoInicio());
        assertEquals(LocalDateTime.of(2026, 7, 31, 23, 59), result.getPeriodoFim());
    }

    @Test
    void deveFalharAoSalvarFeedbackSemPeriodoInicio() {
        FeedbackJpaEntity entity = feedback();
        entity.setPeriodoInicio(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    @Test
    void deveFalharAoSalvarFeedbackSemPeriodoFim() {
        FeedbackJpaEntity entity = feedback();
        entity.setPeriodoFim(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    @Test
    void deveFalharAoSalvarFeedbackSemGeradoEm() {
        FeedbackJpaEntity entity = feedback();
        entity.setGeradoEm(null);

        assertThrows(DataIntegrityViolationException.class, () -> repository.saveAndFlush(entity));
    }

    private FeedbackJpaEntity feedback() {
        Map<LocalDate, Long> porDia = new LinkedHashMap<>();
        porDia.put(LocalDate.of(2026, 7, 10), 2L);
        porDia.put(LocalDate.of(2026, 7, 11), 1L);

        Map<Urgencia, Long> porUrgencia = new LinkedHashMap<>();
        porUrgencia.put(Urgencia.ALTA, 1L);
        porUrgencia.put(Urgencia.BAIXA, 2L);

        FeedbackJpaEntity entity = new FeedbackJpaEntity();
        entity.setPeriodoInicio(LocalDateTime.of(2026, 7, 1, 0, 0));
        entity.setPeriodoFim(LocalDateTime.of(2026, 7, 31, 23, 59));
        entity.setMediaNotas(8.5);
        entity.setTotalAvaliacoes(3L);
        entity.setAvaliacoesPorDia(porDia);
        entity.setAvaliacoesPorUrgencia(porUrgencia);
        entity.setGeradoEm(LocalDateTime.of(2026, 7, 19, 12, 0));
        return entity;
    }
}
