package br.com.fiap.SysFeedback.infrastructure.persistence.adapter;

import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.mapper.AvaliacaoPersistenceMapper;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.AvaliacaoJpaRepository;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.DisciplinaJpaRepository;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.UserJpaRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

class AvaliacaoRepositoryAdapterTest {

    private final AvaliacaoJpaRepository avaliacaoJpaRepository = mock(AvaliacaoJpaRepository.class);
    private final AvaliacaoPersistenceMapper avaliacaoPersistenceMapper = mock(AvaliacaoPersistenceMapper.class);
    private final DisciplinaJpaRepository disciplinaJpaRepository = mock(DisciplinaJpaRepository.class);
    private final UserJpaRepository userJpaRepository = mock(UserJpaRepository.class);

    private AvaliacaoRepositoryAdapter adapter;

    @BeforeEach
    void setUp() {
        adapter = new AvaliacaoRepositoryAdapter(
                avaliacaoJpaRepository,
                avaliacaoPersistenceMapper,
                disciplinaJpaRepository,
                userJpaRepository
        );
    }

    @Test
    void deveSalvarAvaliacaoMapeandoDomainParaJpaEDepoisParaDomain() {
        Avaliacao avaliacao = Fixture.avaliacao();
        AvaliacaoJpaEntity entity = avaliacaoJpaEntity();
        DisciplinaJpaEntity disciplina = disciplinaJpaEntity();
        UserJpaEntity aluno = userJpaEntity();

        when(avaliacaoPersistenceMapper.toJpa(avaliacao)).thenReturn(entity);
        when(disciplinaJpaRepository.getReferenceById(Fixture.DISCIPLINA_ID)).thenReturn(disciplina);
        when(userJpaRepository.getReferenceById(Fixture.USER_ID)).thenReturn(aluno);
        when(avaliacaoJpaRepository.save(entity)).thenReturn(entity);
        when(avaliacaoPersistenceMapper.toDomain(entity)).thenReturn(avaliacao);

        Avaliacao result = adapter.save(avaliacao);

        assertEquals(avaliacao, result);
        verify(avaliacaoPersistenceMapper).toJpa(avaliacao);
        verify(disciplinaJpaRepository).getReferenceById(Fixture.DISCIPLINA_ID);
        verify(userJpaRepository).getReferenceById(Fixture.USER_ID);
        verify(avaliacaoJpaRepository).save(entity);
        verify(avaliacaoPersistenceMapper).toDomain(entity);
    }

    @Test
    void deveSalvarAvaliacaoSemAlunoQuandoAlunoIdForNulo() {
        Avaliacao avaliacao = new Avaliacao(
                Fixture.AVALIACAO_ID,
                Fixture.DESCRICAO_AVALIACAO,
                Fixture.NOTA_AVALIACAO,
                Urgencia.BAIXA,
                Fixture.DATA_ENVIO,
                Fixture.DISCIPLINA_ID,
                null
        );
        AvaliacaoJpaEntity entity = avaliacaoJpaEntity();
        DisciplinaJpaEntity disciplina = disciplinaJpaEntity();
        when(avaliacaoPersistenceMapper.toJpa(avaliacao)).thenReturn(entity);
        when(disciplinaJpaRepository.getReferenceById(Fixture.DISCIPLINA_ID)).thenReturn(disciplina);
        when(avaliacaoJpaRepository.save(entity)).thenReturn(entity);
        when(avaliacaoPersistenceMapper.toDomain(entity)).thenReturn(avaliacao);

        Avaliacao result = adapter.save(avaliacao);

        assertEquals(avaliacao, result);
        verify(disciplinaJpaRepository).getReferenceById(Fixture.DISCIPLINA_ID);
        verify(userJpaRepository, never()).getReferenceById(Fixture.USER_ID);
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

    @Test
    void deveBuscarAvaliacoesPorDisciplinas() {
        Avaliacao avaliacao = Fixture.avaliacao();
        AvaliacaoJpaEntity entity = avaliacaoJpaEntity();
        List<java.util.UUID> disciplinaIds = List.of(Fixture.DISCIPLINA_ID);
        when(avaliacaoJpaRepository.findByDisciplina_IdIn(disciplinaIds)).thenReturn(List.of(entity));
        when(avaliacaoPersistenceMapper.toDomain(entity)).thenReturn(avaliacao);

        List<Avaliacao> result = adapter.findByDisciplinaIds(disciplinaIds);

        assertEquals(List.of(avaliacao), result);
        verify(avaliacaoJpaRepository).findByDisciplina_IdIn(disciplinaIds);
    }

    @Test
    void deveRetornarListaVaziaQuandoDisciplinasForemNulas() {
        List<Avaliacao> result = adapter.findByDisciplinaIds(null);

        assertEquals(List.of(), result);
        verifyNoInteractions(avaliacaoJpaRepository);
    }

    @Test
    void deveRetornarListaVaziaQuandoDisciplinasForemVazias() {
        List<Avaliacao> result = adapter.findByDisciplinaIds(List.of());

        assertEquals(List.of(), result);
        verifyNoInteractions(avaliacaoJpaRepository);
    }

    private AvaliacaoJpaEntity avaliacaoJpaEntity() {
        return new AvaliacaoJpaEntity(
                Fixture.AVALIACAO_ID,
                Fixture.DESCRICAO_AVALIACAO,
                Fixture.NOTA_AVALIACAO,
                Urgencia.BAIXA,
                Fixture.DATA_ENVIO,
                disciplinaJpaEntity(),
                userJpaEntity()
        );
    }

    private DisciplinaJpaEntity disciplinaJpaEntity() {
        DisciplinaJpaEntity disciplina = new DisciplinaJpaEntity(Fixture.DISCIPLINA_NOME, "ARQ");
        disciplina.setId(Fixture.DISCIPLINA_ID);
        return disciplina;
    }

    private UserJpaEntity userJpaEntity() {
        return new UserJpaEntity(
                Fixture.USER_ID,
                Fixture.USER_NAME,
                Fixture.USER_EMAIL,
                Fixture.USER_PASSWORD,
                Fixture.USER_ROLE,
                Fixture.USER_CREATED_AT
        );
    }
}
