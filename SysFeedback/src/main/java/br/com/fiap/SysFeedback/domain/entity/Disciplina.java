package br.com.fiap.SysFeedback.domain.entity;

import lombok.Getter;

import java.util.UUID;

/**
 * Entidade de domínio que representa uma disciplina (a aula avaliada).
 *
 * <p>Uma disciplina é lecionada por um ou mais professores e cursada por alunos
 * matriculados. Cada avaliação pertence a uma disciplina, o que permite ao
 * professor ver apenas as avaliações das disciplinas que leciona.</p>
 *
 * @author Danilo Fernando
 */
@Getter
public class Disciplina {

    private UUID id;
    private final String nome;
    private final String codigo;

    /**
     * Cria uma disciplina para persistência (id atribuído pela infraestrutura).
     *
     * @param  nome  nome da disciplina
     * @param  codigo  código curto identificador (ex.: ARQ, BD)
     *
     * @author Danilo Fernando
     */
    public Disciplina(String nome, String codigo) {
        this.nome = nome;
        this.codigo = codigo;
    }

    /**
     * Reconstrói uma disciplina existente a partir da persistência.
     *
     * @param  id  identificador da disciplina
     * @param  nome  nome da disciplina
     * @param  codigo  código curto identificador
     *
     * @author Danilo Fernando
     */
    public Disciplina(UUID id, String nome, String codigo) {
        this.id = id;
        this.nome = nome;
        this.codigo = codigo;
    }

    /**
     * Define o identificador atribuído na persistência.
     *
     * @param  id  identificador gerado
     *
     * @author Danilo Fernando
     */
    public void setId(UUID id) {
        this.id = id;
    }
}
