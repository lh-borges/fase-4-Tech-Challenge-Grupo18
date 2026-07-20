package br.com.fiap.SysFeedback.application.repository;

import br.com.fiap.SysFeedback.domain.entity.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Porta de saída para persistência e consulta de usuários.
 *
 * @author Thiago de Jesus
 */
public interface RepositoryUserPort {

    /**
     * Persiste um usuário, criando ou atualizando o registro.
     *
     * @param  user  usuário a ser salvo
     * @return usuário persistido
     *
     * @author Thiago de Jesus
     */
    User save(User user);

    /**
     * Busca um usuário pelo endereço de e-mail.
     *
     * @param  email  e-mail a ser pesquisado
     * @return usuário encontrado, ou vazio se não existir
     *
     * @author Thiago de Jesus
     */
    Optional<User> findByEmail(String email);

    /**
     * Busca um usuário pelo identificador único.
     *
     * @param  id  identificador a ser pesquisado
     * @return usuário encontrado, ou vazio se não existir
     *
     * @author Thiago de Jesus
     */
    Optional<User> findById(UUID id);

    /**
     * Remove o usuário informado.
     *
     * @param  user  usuário a ser removido
     *
     * @author Thiago de Jesus
     */
    void delete(User user);

    /**
     * Lista todos os usuários cadastrados.
     *
     * @return lista de todos os usuários
     *
     * @author Thiago de Jesus
     */
    List<User> findAll();

}
