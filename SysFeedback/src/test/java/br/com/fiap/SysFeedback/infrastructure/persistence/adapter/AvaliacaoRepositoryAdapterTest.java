package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.mapper.AvaliacaoPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.AvaliacaoJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AvaliacaoRepositoryAdapterTest {

    private final AvaliacaoJpaRepository avaliacaoJpaRepository = mock(AvaliacaoJpaRepository.class);
    private final AvaliacaoPersistenceMapper avaliacaoPersistenceMapper = mock(AvaliacaoPersistenceMapper.class);

    private AvaliacaoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AvaliacaoRepositoryAdapter(avaliacaoJpaRepository, avaliacaoPersistenceMapper);
    }

    @Test
    void deveSalvarAvaliacaoMapeandoDomainParaJpaEDepoisParaDomain() {
        Avaliacao avaliacao = Fixture.avaliacao();
        AvaliacaoJpaEntity entity = avaliacaoJpaEntity();

        when(avaliacaoPersistenceMapper.toJpa(avaliacao)).thenReturn(entity);
        when(avaliacaoJpaRepository.save(entity)).thenReturn(entity);
        when(avaliacaoPersistenceMapper.toDomain(entity)).thenReturn(avaliacao);

        Avaliacao result = adapter.save(avaliacao);

        assertEquals(avaliacao, result);
        verify(avaliacaoPersistenceMapper).toJpa(avaliacao);
        verify(avaliacaoJpaRepository).save(entity);
        verify(avaliacaoPersistenceMapper).toDomain(entity);
    }

    @Test
    void deveListarTodasAvaliacoes() {
        Avaliacao avaliacao = Fixture.avaliacao();
        AvaliacaoJpaEntity entity = avaliacaoJpaEntity();
        when(avaliacaoJpaRepository.findAll()).thenReturn(List.of(entity));
        when(avaliacaoPersistenceMapper.toDomain(entity)).thenReturn(avaliacao);

        List<Avaliacao> result = adapter.findAll();

        assertEquals(List.of(avaliacao), result);
        verify(avaliacaoJpaRepository).findAll();
    }

    @Test
    void deveBuscarAvaliacoesPorPeriodo() {
        Avaliacao avaliacao = Fixture.avaliacao();
        AvaliacaoJpaEntity entity = avaliacaoJpaEntity();
        LocalDateTime inicio = Fixture.PERIODO_INICIO;
        LocalDateTime fim = Fixture.PERIODO_FIM;
        when(avaliacaoJpaRepository.findByDataEnvioBetween(inicio, fim)).thenReturn(List.of(entity));
        when(avaliacaoPersistenceMapper.toDomain(entity)).thenReturn(avaliacao);

        List<Avaliacao> result = adapter.findByPeriodo(inicio, fim);

        assertEquals(List.of(avaliacao), result);
        verify(avaliacaoJpaRepository).findByDataEnvioBetween(inicio, fim);
    }

    private AvaliacaoJpaEntity avaliacaoJpaEntity() {
        return new AvaliacaoJpaEntity(
                Fixture.AVALIACAO_ID,
                Fixture.DESCRICAO_AVALIACAO,
                Fixture.NOTA_AVALIACAO,
                Urgencia.BAIXA,
                Fixture.DATA_ENVIO
        );
    }
}
