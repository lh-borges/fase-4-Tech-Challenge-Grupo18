package br.com.fiap.SysFeedback.infrastructure.config;

import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.DisciplinaJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.AvaliacaoJpaRepository;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.DisciplinaJpaRepository;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.UserJpaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Carga inicial de dados. Roda em qualquer ambiente (dev e prod) na subida da
 * aplicação, mas só popula quando o banco está vazio — assim é idempotente e
 * não duplica dados a cada reinício.
 *
 * <p>Cria um usuário de cada role, disciplinas com suas relações de ensino
 * (professor) e matrícula (aluno), e avaliações distribuídas entre as disciplinas,
 * para demonstrar a regra: o professor vê apenas as disciplinas que leciona e o
 * admin vê todas.</p>
 *
 * @author luisbraserv
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    /**
     * Registra o runner de carga inicial executado na subida da aplicação.
     *
     * @param  userRepository  repositório de usuários
     * @param  disciplinaRepository  repositório de disciplinas
     * @param  avaliacaoRepository  repositório de avaliações
     * @param  passwordEncoder  codificador aplicado às senhas dos usuários semeados
     * @return runner que popula a base apenas quando ela está vazia
     *
     * @author Danilo Fernando
     */
    @Bean
    public CommandLineRunner seedDatabase(UserJpaRepository userRepository,
                                          DisciplinaJpaRepository disciplinaRepository,
                                          AvaliacaoJpaRepository avaliacaoRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0 || avaliacaoRepository.count() > 0) {
                return;
            }
            try {
                popular(userRepository, disciplinaRepository, avaliacaoRepository, passwordEncoder);
                log.info("Carga inicial concluída: usuários, disciplinas e avaliações criados.");
            } catch (DataIntegrityViolationException e) {
                // Outra instância populou o banco em paralelo (ex.: cold starts
                // simultâneos no Cloud Run). Não é fatal: seguimos sem abortar a subida.
                log.info("Carga inicial ignorada: dados já existem (populados por outra instância).");
            }
        };
    }

    /**
     * Persiste usuários, disciplinas (com relações) e avaliações de exemplo.
     *
     * @param  userRepository  repositório de usuários
     * @param  disciplinaRepository  repositório de disciplinas
     * @param  avaliacaoRepository  repositório de avaliações
     * @param  passwordEncoder  codificador aplicado às senhas
     *
     * @author Danilo Fernando
     */
    private void popular(UserJpaRepository userRepository,
                         DisciplinaJpaRepository disciplinaRepository,
                         AvaliacaoJpaRepository avaliacaoRepository,
                         PasswordEncoder passwordEncoder) {

        userRepository.save(novoUsuario("Admin Teste", "admin@fiap.com", "123456", Role.ADMIN, passwordEncoder));

        // Professores: um leciona Arquitetura e Banco de Dados; outro leciona DevOps.
        UserJpaEntity profArquiteto = userRepository.save(
                novoUsuario("Professor Arquiteto", "professor@fiap.com", "123456", Role.PROFESSOR, passwordEncoder));
        UserJpaEntity profDevops = userRepository.save(
                novoUsuario("Professora DevOps", "professor.devops@fiap.com", "123456", Role.PROFESSOR, passwordEncoder));

        // Alunos com matrículas diferentes.
        UserJpaEntity ana = userRepository.save(
                novoUsuario("Ana Aluna", "aluno@fiap.com", "123456", Role.ALUNO, passwordEncoder));
        UserJpaEntity bruno = userRepository.save(
                novoUsuario("Bruno Aluno", "aluno2@fiap.com", "123456", Role.ALUNO, passwordEncoder));
        UserJpaEntity carla = userRepository.save(
                novoUsuario("Carla Aluna", "aluno3@fiap.com", "123456", Role.ALUNO, passwordEncoder));

        // Disciplinas com professores e alunos matriculados.
        DisciplinaJpaEntity arquitetura = novaDisciplina("Arquitetura de Software", "ARQ",
                Set.of(profArquiteto), Set.of(ana, bruno));
        DisciplinaJpaEntity bancoDados = novaDisciplina("Banco de Dados", "BD",
                Set.of(profArquiteto), Set.of(ana, carla));
        DisciplinaJpaEntity devops = novaDisciplina("DevOps na Nuvem", "DEVOPS",
                Set.of(profDevops), Set.of(ana, bruno));
        disciplinaRepository.save(arquitetura);
        disciplinaRepository.save(bancoDados);
        disciplinaRepository.save(devops);

        List<AvaliacaoJpaEntity> avaliacoes = new ArrayList<>();
        // Arquitetura de Software (alunos Ana e Bruno)
        avaliacoes.add(novaAvaliacao("Aula muito confusa, o professor pulou etapas importantes", 1, 9, 10, arquitetura, ana));
        avaliacoes.add(novaAvaliacao("Nao consegui acompanhar, faltou material de apoio", 3, 9, 2, arquitetura, bruno));
        avaliacoes.add(novaAvaliacao("Conteudo bom mas o audio estava ruim", 5, 8, 14, arquitetura, ana));
        avaliacoes.add(novaAvaliacao("Otima explicacao, gostei bastante", 9, 7, 11, arquitetura, bruno));
        avaliacoes.add(novaAvaliacao("Excelente aula, muito didatica", 10, 7, 3, arquitetura, ana));
        avaliacoes.add(novaAvaliacao("Ritmo muito acelerado, dificil de acompanhar", 2, 5, 20, arquitetura, bruno));
        avaliacoes.add(novaAvaliacao("Melhor aula do modulo ate agora", 10, 3, 16, arquitetura, ana));
        // Banco de Dados (alunos Ana e Carla)
        avaliacoes.add(novaAvaliacao("Aula ok, poderia ter mais exemplos praticos", 6, 8, 4, bancoDados, ana));
        avaliacoes.add(novaAvaliacao("Plataforma travou algumas vezes durante a live", 4, 6, 15, bancoDados, carla));
        avaliacoes.add(novaAvaliacao("Professor dominou o assunto, recomendo", 8, 6, 6, bancoDados, ana));
        avaliacoes.add(novaAvaliacao("Gostei dos exercicios propostos", 7, 5, 9, bancoDados, carla));
        avaliacoes.add(novaAvaliacao("Aula sem preparo, muito improviso", 2, 4, 13, bancoDados, ana));
        avaliacoes.add(novaAvaliacao("Bom conteudo, mas comecou atrasada", 6, 4, 1, bancoDados, carla));
        avaliacoes.add(novaAvaliacao("Faltou aprofundar nos topicos avancados", 5, 3, 5, bancoDados, ana));
        // DevOps na Nuvem (alunos Ana e Bruno)
        avaliacoes.add(novaAvaliacao("Muito boa, tirei todas as minhas duvidas", 9, 2, 12, devops, ana));
        avaliacoes.add(novaAvaliacao("Nao recomendo, desorganizada do inicio ao fim", 0, 2, 21, devops, bruno));
        avaliacoes.add(novaAvaliacao("Aula razoavel, nada excepcional", 6, 1, 8, devops, ana));
        avaliacoes.add(novaAvaliacao("Adorei os estudos de caso reais", 8, 0, 7, devops, bruno));

        avaliacaoRepository.saveAll(avaliacoes);
    }

    /**
     * Monta uma entidade de usuário com senha codificada e data de criação atual.
     *
     * @param  nome  nome do usuário
     * @param  email  e-mail de login do usuário
     * @param  senha  senha em texto puro a ser codificada
     * @param  role  papel/perfil de acesso do usuário
     * @param  encoder  codificador aplicado à senha
     * @return entidade de usuário pronta para persistência
     *
     * @author luisbraserv
     */
    private UserJpaEntity novoUsuario(String nome, String email, String senha,
                                      Role role, PasswordEncoder encoder) {
        UserJpaEntity user = new UserJpaEntity();
        user.setName(nome);
        user.setEmail(email);
        user.setPassword(encoder.encode(senha));
        user.setRole(role);
        user.setCreatedAt(LocalDateTime.now());
        return user;
    }

    /**
     * Monta uma disciplina com seus professores e alunos matriculados.
     *
     * @param  nome  nome da disciplina
     * @param  codigo  código curto identificador
     * @param  professores  professores que lecionam a disciplina
     * @param  alunos  alunos matriculados na disciplina
     * @return entidade de disciplina pronta para persistência
     *
     * @author Danilo Fernando
     */
    private DisciplinaJpaEntity novaDisciplina(String nome, String codigo,
                                               Set<UserJpaEntity> professores, Set<UserJpaEntity> alunos) {
        DisciplinaJpaEntity disciplina = new DisciplinaJpaEntity(nome, codigo);
        disciplina.setProfessores(new java.util.HashSet<>(professores));
        disciplina.setAlunos(new java.util.HashSet<>(alunos));
        return disciplina;
    }

    /**
     * Monta uma avaliação de exemplo com urgência derivada da nota, data no passado,
     * disciplina e aluno autor.
     *
     * @param  descricao  texto descritivo da avaliação
     * @param  nota  nota atribuída, usada para calcular a urgência
     * @param  diasAtras  quantos dias no passado a avaliação foi enviada
     * @param  horaDoDia  hora do dia (0-23) do envio
     * @param  disciplina  disciplina avaliada
     * @param  aluno  aluno autor da avaliação
     * @return entidade de avaliação pronta para persistência
     *
     * @author Danilo Fernando
     */
    private AvaliacaoJpaEntity novaAvaliacao(String descricao, int nota, int diasAtras, int horaDoDia,
                                             DisciplinaJpaEntity disciplina, UserJpaEntity aluno) {
        AvaliacaoJpaEntity avaliacao = new AvaliacaoJpaEntity();
        avaliacao.setDescricao(descricao);
        avaliacao.setNota(nota);
        avaliacao.setUrgencia(Urgencia.fromNota(nota));
        avaliacao.setDataEnvio(
                LocalDateTime.now().minusDays(diasAtras).withHour(horaDoDia).withMinute(0).withSecond(0).withNano(0)
        );
        avaliacao.setDisciplina(disciplina);
        avaliacao.setAluno(aluno);
        return avaliacao;
    }
}
