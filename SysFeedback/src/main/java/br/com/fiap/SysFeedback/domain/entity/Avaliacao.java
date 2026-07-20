package br.com.fiap.SysFeedback.domain.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import br.com.fiap.SysFeedback.domain.exception.AvaliacaoInvalidaException;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Avaliação enviada por um usuário (role ALUNO).
 *
 * <p>Contém a descrição livre e a nota (0 a 10). A urgência é derivada da nota
 * no momento da criação, e a data de envio é registrada automaticamente.
 * Pode ser lida pelo dono (roles PROFESSOR/ADMIN).</p>
 *
 * @author luisbraserv
 */
@Getter
public class Avaliacao {

    private static final int NOTA_MINIMA = 0;
    private static final int NOTA_MAXIMA = 10;

    private UUID id;
    private final String descricao;
    private final int nota;
    private final Urgencia urgencia;
    private final LocalDateTime dataEnvio;
    private final UUID disciplinaId;
    private final UUID alunoId;

    /**
     * Cria uma nova avaliação de uma disciplina, derivando a urgência da nota e
     * registrando a data de envio.
     *
     * @param  descricao  texto livre da avaliação
     * @param  nota  nota atribuída (0 a 10)
     * @param  disciplinaId  disciplina avaliada
     * @param  alunoId  aluno autor da avaliação
     *
     * @throws AvaliacaoInvalidaException  quando a descrição é vazia, a nota está fora do intervalo ou falta a disciplina
     *
     * @author Danilo Fernando
     */
    public Avaliacao(String descricao, int nota, UUID disciplinaId, UUID alunoId) {
        validar(descricao, nota);
        if (disciplinaId == null) {
            throw new AvaliacaoInvalidaException("Disciplina é obrigatória");
        }

        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = Urgencia.fromNota(nota);
        this.dataEnvio = LocalDateTime.now();
        this.disciplinaId = disciplinaId;
        this.alunoId = alunoId;
    }

    /**
     * Reconstrói uma avaliação existente a partir da persistência.
     *
     * @param  id  identificador da avaliação
     * @param  descricao  texto livre da avaliação
     * @param  nota  nota atribuída (0 a 10)
     * @param  urgencia  urgência já calculada da avaliação
     * @param  dataEnvio  data e hora do envio original
     * @param  disciplinaId  disciplina avaliada
     * @param  alunoId  aluno autor da avaliação
     *
     * @throws AvaliacaoInvalidaException  quando a descrição é vazia ou a nota está fora do intervalo
     *
     * @author luisbraserv
     */
    public Avaliacao(UUID id, String descricao, int nota, Urgencia urgencia, LocalDateTime dataEnvio,
                     UUID disciplinaId, UUID alunoId) {
        validar(descricao, nota);

        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
        this.disciplinaId = disciplinaId;
        this.alunoId = alunoId;
    }

    /**
     * Valida a descrição e a nota da avaliação.
     *
     * @param  descricao  texto livre da avaliação
     * @param  nota  nota atribuída (0 a 10)
     *
     * @throws AvaliacaoInvalidaException  quando a descrição é vazia ou a nota está fora do intervalo
     *
     * @author luisbraserv
     */
    private void validar(String descricao, int nota) {
        if (descricao == null || descricao.isBlank()) {
            throw new AvaliacaoInvalidaException("Descrição não pode ser vazia");
        }
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new AvaliacaoInvalidaException("Nota deve estar entre " + NOTA_MINIMA + " e " + NOTA_MAXIMA);
        }
    }

    /**
     * Define o identificador da avaliação após a persistência.
     *
     * @param  id  identificador gerado
     *
     * @author luisbraserv
     */
    public void setId(UUID id) {
        this.id = id;
    }
}
