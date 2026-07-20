package br.com.fiap.SysFeedback.fixture;

import br.com.fiap.SysFeedback.application.dto.AvaliacaoRequestDTO;
import br.com.fiap.SysFeedback.application.dto.AvaliacaoResponseDTO;
import br.com.fiap.SysFeedback.application.dto.FeedbackRequestDTO;
import br.com.fiap.SysFeedback.application.dto.FeedbackResponseDTO;
import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.dto.UserUpdateDTO;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.entity.Feedback;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.domain.enums.Urgencia;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public final class Fixture {

    public static final UUID AVALIACAO_ID =
            UUID.fromString("11111111-1111-1111-1111-111111111111");
    public static final String DESCRICAO_AVALIACAO =
            "Aula muito boa, com exemplos praticos";
    public static final int NOTA_AVALIACAO = 9;
    public static final LocalDateTime DATA_ENVIO =
            LocalDateTime.of(2026, 7, 18, 10, 30);

    public static final UUID FEEDBACK_ID =
            UUID.fromString("22222222-2222-2222-2222-222222222222");
    public static final LocalDateTime PERIODO_INICIO =
            LocalDateTime.of(2026, 7, 1, 0, 0);
    public static final LocalDateTime PERIODO_FIM =
            LocalDateTime.of(2026, 7, 31, 23, 59);
    public static final double MEDIA_NOTAS = 7.5;
    public static final long TOTAL_AVALIACOES = 4L;
    public static final LocalDateTime FEEDBACK_GERADO_EM =
            LocalDateTime.of(2026, 7, 18, 11, 0);

    public static final UUID USER_ID =
            UUID.fromString("33333333-3333-3333-3333-333333333333");
    public static final String USER_NAME = "Aluno Teste";
    public static final String USER_EMAIL = "aluno@fiap.com";
    public static final String USER_PASSWORD = "123456";
    public static final String USER_ENCODED_PASSWORD = "$2a$10$encoded-password";
    public static final Role USER_ROLE = Role.ALUNO;
    public static final LocalDateTime USER_CREATED_AT =
            LocalDateTime.of(2026, 7, 18, 9, 0);

    private Fixture() {
    }

    public static AvaliacaoRequestDTO avaliacaoRequestDTOValida() {
        return new AvaliacaoRequestDTO(DESCRICAO_AVALIACAO, NOTA_AVALIACAO);
    }

    public static AvaliacaoRequestDTO avaliacaoRequestDTO(String descricao, Integer nota) {
        return new AvaliacaoRequestDTO(descricao, nota);
    }

    public static AvaliacaoResponseDTO avaliacaoResponseDTO() {
        return new AvaliacaoResponseDTO(
                AVALIACAO_ID,
                DESCRICAO_AVALIACAO,
                NOTA_AVALIACAO,
                Urgencia.BAIXA,
                DATA_ENVIO
        );
    }

    public static AvaliacaoResponseDTO avaliacaoResponseDTOComNulos() {
        return new AvaliacaoResponseDTO(null, null, NOTA_AVALIACAO, null, null);
    }

    public static Avaliacao avaliacao() {
        return new Avaliacao(
                AVALIACAO_ID,
                DESCRICAO_AVALIACAO,
                NOTA_AVALIACAO,
                Urgencia.BAIXA,
                DATA_ENVIO
        );
    }

    public static List<Avaliacao> avaliacoes() {
        return List.of(
                avaliacao(),
                new Avaliacao(
                        UUID.fromString("44444444-4444-4444-4444-444444444444"),
                        "Aula confusa",
                        2,
                        Urgencia.ALTA,
                        LocalDateTime.of(2026, 7, 17, 10, 0)
                )
        );
    }

    public static FeedbackRequestDTO feedbackRequestDTOValido() {
        return new FeedbackRequestDTO(PERIODO_INICIO, PERIODO_FIM);
    }

    public static FeedbackRequestDTO feedbackRequestDTO(LocalDateTime inicio, LocalDateTime fim) {
        return new FeedbackRequestDTO(inicio, fim);
    }

    public static FeedbackResponseDTO feedbackResponseDTO() {
        return new FeedbackResponseDTO(
                FEEDBACK_ID,
                PERIODO_INICIO,
                PERIODO_FIM,
                MEDIA_NOTAS,
                TOTAL_AVALIACOES,
                avaliacoesPorDia(),
                avaliacoesPorUrgencia(),
                FEEDBACK_GERADO_EM
        );
    }

    public static Feedback feedback() {
        return new Feedback(
                FEEDBACK_ID,
                PERIODO_INICIO,
                PERIODO_FIM,
                MEDIA_NOTAS,
                TOTAL_AVALIACOES,
                avaliacoesPorDia(),
                avaliacoesPorUrgencia(),
                FEEDBACK_GERADO_EM
        );
    }

    public static FeedbackResponseDTO feedbackResponseDTOComNulos() {
        return new FeedbackResponseDTO(
                null,
                null,
                null,
                0.0,
                0L,
                null,
                null,
                null
        );
    }

    public static Map<LocalDate, Long> avaliacoesPorDia() {
        Map<LocalDate, Long> avaliacoesPorDia = new LinkedHashMap<>();
        avaliacoesPorDia.put(LocalDate.of(2026, 7, 17), 2L);
        avaliacoesPorDia.put(LocalDate.of(2026, 7, 18), 2L);
        return avaliacoesPorDia;
    }

    public static Map<Urgencia, Long> avaliacoesPorUrgencia() {
        Map<Urgencia, Long> avaliacoesPorUrgencia = new LinkedHashMap<>();
        avaliacoesPorUrgencia.put(Urgencia.ALTA, 1L);
        avaliacoesPorUrgencia.put(Urgencia.MEDIA, 1L);
        avaliacoesPorUrgencia.put(Urgencia.BAIXA, 2L);
        return avaliacoesPorUrgencia;
    }

    public static UserRequestDTO userRequestDTOValido() {
        return new UserRequestDTO(USER_NAME, USER_EMAIL, USER_PASSWORD, USER_ROLE);
    }

    public static UserRequestDTO userRequestDTOComNulos() {
        return new UserRequestDTO(null, null, null, null);
    }

    public static UserUpdateDTO userUpdateDTOValido() {
        return new UserUpdateDTO(USER_NAME, USER_EMAIL, USER_PASSWORD, USER_ROLE);
    }

    public static UserUpdateDTO userUpdateDTO(String name, String email, String password, Role role) {
        return new UserUpdateDTO(name, email, password, role);
    }

    public static UserUpdateDTO userUpdateDTOComNulos() {
        return new UserUpdateDTO(null, null, null, null);
    }

    public static UserResponseDTO userResponseDTO() {
        return new UserResponseDTO(USER_ID, USER_NAME, USER_EMAIL, USER_ROLE, USER_CREATED_AT);
    }

    public static UserResponseDTO userResponseDTOComNulos() {
        return new UserResponseDTO(null, null, null, null, null);
    }

    public static User user() {
        return new User(
                USER_ID,
                USER_NAME,
                USER_EMAIL,
                USER_PASSWORD,
                USER_ROLE,
                USER_CREATED_AT
        );
    }

    public static User userComSenhaCriptografada() {
        return new User(
                USER_ID,
                USER_NAME,
                USER_EMAIL,
                USER_ENCODED_PASSWORD,
                USER_ROLE,
                USER_CREATED_AT
        );
    }

    public static User professor() {
        return new User(
                UUID.fromString("55555555-5555-5555-5555-555555555555"),
                "Professor Teste",
                "professor@fiap.com",
                USER_PASSWORD,
                Role.PROFESSOR,
                USER_CREATED_AT
        );
    }

    public static List<User> users() {
        return List.of(user(), professor());
    }

    public static List<Feedback> feedbacks() {
        return List.of(feedback());
    }
}
