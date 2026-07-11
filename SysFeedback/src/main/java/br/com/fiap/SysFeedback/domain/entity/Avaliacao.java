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

    /** Criação de uma nova avaliação (id gerado na persistência). */
    public Avaliacao(String descricao, int nota) {
        validar(descricao, nota);

        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = Urgencia.fromNota(nota);
        this.dataEnvio = LocalDateTime.now();
    }

    /** Reconstrução a partir da persistência. */
    public Avaliacao(UUID id, String descricao, int nota, Urgencia urgencia, LocalDateTime dataEnvio) {
        validar(descricao, nota);

        this.id = id;
        this.descricao = descricao;
        this.nota = nota;
        this.urgencia = urgencia;
        this.dataEnvio = dataEnvio;
    }

    private void validar(String descricao, int nota) {
        if (descricao == null || descricao.isBlank()) {
            throw new AvaliacaoInvalidaException("Descrição não pode ser vazia");
        }
        if (nota < NOTA_MINIMA || nota > NOTA_MAXIMA) {
            throw new AvaliacaoInvalidaException("Nota deve estar entre " + NOTA_MINIMA + " e " + NOTA_MAXIMA);
        }
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
