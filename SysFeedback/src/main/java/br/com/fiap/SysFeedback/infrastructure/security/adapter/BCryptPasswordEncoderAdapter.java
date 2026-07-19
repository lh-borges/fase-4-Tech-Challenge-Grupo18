package br.com.fiap.SysFeedback.infrastructure.security.adapter;

import br.com.fiap.SysFeedback.application.security.PasswordEncoderPort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Adaptador que implementa a porta {@link PasswordEncoderPort} delegando a
 * codificação e verificação de senhas ao {@link PasswordEncoder} do Spring Security.
 *
 * @author Thiago de Jesus
 */
@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoderPort {

    private final PasswordEncoder passwordEncoder;

    /**
     * Cria o adaptador injetando o codificador de senhas do Spring Security.
     *
     * @param  passwordEncoder  codificador de senhas utilizado internamente
     *
     * @author Thiago de Jesus
     */
    public BCryptPasswordEncoderAdapter(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * Codifica a senha em texto puro.
     *
     * @param  password  senha em texto puro
     * @return senha codificada
     *
     * @author Thiago de Jesus
     */
    @Override
    public String encode(String password) {
        return passwordEncoder.encode(password);
    }

    /**
     * Verifica se a senha em texto puro corresponde à senha codificada.
     *
     * @param  rawPassword      senha em texto puro informada
     * @param  encodedPassword  senha codificada armazenada
     * @return {@code true} se as senhas conferem, caso contrário {@code false}
     *
     * @author Thiago de Jesus
     */
    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }
}