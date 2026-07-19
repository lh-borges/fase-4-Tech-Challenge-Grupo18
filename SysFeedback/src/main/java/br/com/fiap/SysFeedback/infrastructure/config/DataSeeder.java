package br.com.fiap.SysFeedback.infrastructure.config;

import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.AvaliacaoJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.entity.UserJpaEntity;
import br.com.fiap.SysFeedback.infrastructure.persistence.repository.AvaliacaoJpaRepository;
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

/**
 * Carga inicial de dados. Roda em qualquer ambiente (dev e prod) na subida da
 * aplicação, mas só popula quando o banco está vazio — assim é idempotente e
 * não duplica dados a cada reinício.
 *
 * <p>Cria um usuário de cada role e um conjunto de avaliações distribuídas ao
 * longo dos últimos dias, com notas variadas, para que o relatório de feedback
 * (avaliações por dia e por urgência) tenha dados significativos.</p>
 *
 * @author luisbraserv
 */
@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    /**
     * Registra o runner de carga inicial executado na subida da aplicação.
     *
     * @param  userRepository  repositório de usuários usado para verificar e popular a base
     * @param  avaliacaoRepository  repositório de avaliações usado para verificar e popular a base
     * @param  passwordEncoder  codificador aplicado às senhas dos usuários semeados
     * @return runner que popula a base apenas quando ela está vazia
     *
     * @author luisbraserv
     */
    @Bean
    public CommandLineRunner seedDatabase(UserJpaRepository userRepository,
                                          AvaliacaoJpaRepository avaliacaoRepository,
                                          PasswordEncoder passwordEncoder) {
        return args -> {
            if (userRepository.count() > 0 || avaliacaoRepository.count() > 0) {
                return;
            }
            try {
                popular(userRepository, avaliacaoRepository, passwordEncoder);
                log.info("Carga inicial concluída: usuários e avaliações criados.");
            } catch (DataIntegrityViolationException e) {
                // Outra instância populou o banco em paralelo (ex.: cold starts
                // simultâneos no Cloud Run). Não é fatal: seguimos sem abortar a subida.
                log.info("Carga inicial ignorada: dados já existem (populados por outra instância).");
            }
        };
    }

    /**
     * Persiste os usuários e as avaliações de exemplo na base.
     *
     * @param  userRepository  repositório onde os usuários de exemplo são salvos
     * @param  avaliacaoRepository  repositório onde as avaliações de exemplo são salvas
     * @param  passwordEncoder  codificador aplicado às senhas dos usuários criados
     *
     * @author luisbraserv
     */
    private void popular(UserJpaRepository userRepository,
                         AvaliacaoJpaRepository avaliacaoRepository,
                         PasswordEncoder passwordEncoder) {
        userRepository.save(novoUsuario("Admin Teste", "admin@fiap.com", "123456", Role.ADMIN, passwordEncoder));
        userRepository.save(novoUsuario("Professor Teste", "professor@fiap.com", "123456", Role.PROFESSOR, passwordEncoder));
        userRepository.save(novoUsuario("Aluno Teste", "aluno@fiap.com", "123456", Role.ALUNO, passwordEncoder));

        List<AvaliacaoJpaEntity> avaliacoes = new ArrayList<>();
        avaliacoes.add(novaAvaliacao("Aula muito confusa, o professor pulou etapas importantes", 1, 9, 10));
        avaliacoes.add(novaAvaliacao("Nao consegui acompanhar, faltou material de apoio", 3, 9, 2));
        avaliacoes.add(novaAvaliacao("Conteudo bom mas o audio estava ruim", 5, 8, 14));
        avaliacoes.add(novaAvaliacao("Aula ok, poderia ter mais exemplos praticos", 6, 8, 4));
        avaliacoes.add(novaAvaliacao("Otima explicacao, gostei bastante", 9, 7, 11));
        avaliacoes.add(novaAvaliacao("Excelente aula, muito didatica", 10, 7, 3));
        avaliacoes.add(novaAvaliacao("Plataforma travou algumas vezes durante a live", 4, 6, 15));
        avaliacoes.add(novaAvaliacao("Professor dominou o assunto, recomendo", 8, 6, 6));
        avaliacoes.add(novaAvaliacao("Ritmo muito acelerado, dificil de acompanhar", 2, 5, 20));
        avaliacoes.add(novaAvaliacao("Gostei dos exercicios propostos", 7, 5, 9));
        avaliacoes.add(novaAvaliacao("Aula sem preparo, muito improviso", 2, 4, 13));
        avaliacoes.add(novaAvaliacao("Bom conteudo, mas comecou atrasada", 6, 4, 1));
        avaliacoes.add(novaAvaliacao("Melhor aula do modulo ate agora", 10, 3, 16));
        avaliacoes.add(novaAvaliacao("Faltou aprofundar nos topicos avancados", 5, 3, 5));
        avaliacoes.add(novaAvaliacao("Muito boa, tirei todas as minhas duvidas", 9, 2, 12));
        avaliacoes.add(novaAvaliacao("Nao recomendo, desorganizada do inicio ao fim", 0, 2, 21));
        avaliacoes.add(novaAvaliacao("Aula razoavel, nada excepcional", 6, 1, 8));
        avaliacoes.add(novaAvaliacao("Adorei os estudos de caso reais", 8, 0, 7));

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
     * Monta uma avaliação de exemplo com urgência derivada da nota e data no passado.
     *
     * @param  descricao  texto descritivo da avaliação
     * @param  nota  nota atribuída, usada para calcular a urgência
     * @param  diasAtras  quantos dias no passado a avaliação foi enviada
     * @param  horaDoDia  hora do dia (0-23) do envio
     * @return entidade de avaliação pronta para persistência
     *
     * @author luisbraserv
     */
    private AvaliacaoJpaEntity novaAvaliacao(String descricao, int nota,
                                             int diasAtras, int horaDoDia) {
        AvaliacaoJpaEntity avaliacao = new AvaliacaoJpaEntity();
        avaliacao.setDescricao(descricao);
        avaliacao.setNota(nota);
        avaliacao.setUrgencia(Urgencia.fromNota(nota));
        avaliacao.setDataEnvio(
                LocalDateTime.now().minusDays(diasAtras).withHour(horaDoDia).withMinute(0).withSecond(0).withNano(0)
        );
        return avaliacao;
    }
}
