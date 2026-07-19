package br.com.fiap.SysFeedback.application.usecase;

import br.com.fiap.SysFeedback.application.dto.RelatorioSemanalDTO;
import br.com.fiap.SysFeedback.application.repository.RepositoryAvaliacaoPort;
import br.com.fiap.SysFeedback.application.repository.RepositoryFeedbackPort;
import br.com.fiap.SysFeedback.domain.entity.Avaliacao;
import br.com.fiap.SysFeedback.domain.entity.Feedback;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Gera o relatório semanal consolidado para o período informado.
 *
 * <p>Reutiliza o domínio {@link Feedback} para calcular média e contagens (por dia
 * e por urgência), persiste o snapshot do período e devolve, além das agregações,
 * a lista de avaliações (descrição, urgência, data de envio) — os dados que a Cloud
 * Function {@code relatorio-semanal} usa para montar o e-mail.</p>
 *
 * @author Danilo Fernando
 */
public class RelatorioSemanalGenerateUseCase {

    private final RepositoryAvaliacaoPort repositoryAvaliacaoPort;
    private final RepositoryFeedbackPort repositoryFeedbackPort;

    /**
     * Cria o caso de uso com as portas de persistência necessárias.
     *
     * @param  repositoryAvaliacaoPort  porta de acesso às avaliações do período
     * @param  repositoryFeedbackPort  porta de persistência do snapshot de feedback
     *
     * @author Danilo Fernando
     */
    public RelatorioSemanalGenerateUseCase(RepositoryAvaliacaoPort repositoryAvaliacaoPort,
                                           RepositoryFeedbackPort repositoryFeedbackPort) {
        this.repositoryAvaliacaoPort = repositoryAvaliacaoPort;
        this.repositoryFeedbackPort = repositoryFeedbackPort;
    }

    /**
     * Gera e persiste o relatório semanal consolidado do período informado.
     *
     * @param  inicio  início do período a consolidar
     * @param  fim  fim do período a consolidar
     * @return relatório semanal com agregações e a lista de avaliações do período
     *
     * @author Danilo Fernando
     */
    public RelatorioSemanalDTO execute(LocalDateTime inicio, LocalDateTime fim) {
        List<Avaliacao> avaliacoes = repositoryAvaliacaoPort.findByPeriodo(inicio, fim);

        Feedback feedback = Feedback.gerar(inicio, fim, avaliacoes);
        Feedback salvo = repositoryFeedbackPort.save(feedback);

        List<RelatorioSemanalDTO.ItemAvaliacao> itens = avaliacoes.stream()
                .map(a -> new RelatorioSemanalDTO.ItemAvaliacao(
                        a.getDescricao(), a.getUrgencia(), a.getDataEnvio()))
                .toList();

        return new RelatorioSemanalDTO(
                salvo.getPeriodoInicio(),
                salvo.getPeriodoFim(),
                salvo.getMediaNotas(),
                salvo.getTotalAvaliacoes(),
                salvo.getAvaliacoesPorDia(),
                salvo.getAvaliacoesPorUrgencia(),
                itens);
    }
}
