package br.com.fiap.SysFeedback.infrastructure.persistence.entity;

import br.com.fiap.SysFeedback.domain.enums.Urgencia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "avaliacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AvaliacaoJpaEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false)
    private int nota;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Urgencia urgencia;

    @Column(name = "data_envio", nullable = false)
    private LocalDateTime dataEnvio;
}
