package br.com.fiap.SysFeedback.application.security;

/**
 * Porta de saída para codificação e verificação de senhas.
 *
 * @author Thiago de Jesus
 */
public interface PasswordEncoderPort {

    /**
     * Codifica uma senha em texto puro.
     *
     * @param  password  senha em texto puro
     * @return senha codificada
     *
     * @author Thiago de Jesus
     */
    String encode(String password);

    /**
     * Verifica se a senha em texto puro corresponde à senha codificada.
     *
     * @param  rawPassword      senha em texto puro
     * @param  encodedPassword  senha codificada a ser comparada
     * @return {@code true} se as senhas coincidirem, {@code false} caso contrário
     *
     * @author Thiago de Jesus
     */
    boolean matches(String rawPassword, String encodedPassword);

}
