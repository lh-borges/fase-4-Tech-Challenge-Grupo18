package br.com.fiap.SysFeedback.domain.enums;

/**
 * Nível de urgência de uma avaliação.
 *
 * <p>A urgência é derivada automaticamente da nota (0 a 10): quanto menor a nota,
 * maior a urgência. É usada tanto no aviso de itens críticos (mensageria) quanto
 * no relatório semanal (quantidade de avaliações por urgência).</p>
 */
public enum Urgencia {
    BAIXA,
    MEDIA,
    ALTA;

    /**
     * Deriva a urgência a partir da nota da avaliação.
     *
     * <ul>
     *     <li>0 a 3  → {@link #ALTA} (item crítico)</li>
     *     <li>4 a 6  → {@link #MEDIA}</li>
     *     <li>7 a 10 → {@link #BAIXA}</li>
     * </ul>
     *
     * @param nota nota da avaliação (0 a 10)
     * @return urgência correspondente
     */
    public static Urgencia fromNota(int nota) {
        if (nota <= 3) {
            return ALTA;
        }
        if (nota <= 6) {
            return MEDIA;
        }
        return BAIXA;
    }
}
