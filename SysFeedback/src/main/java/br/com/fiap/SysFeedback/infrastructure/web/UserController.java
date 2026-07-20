package br.com.fiap.SysFeedback.infrastructure.web;

import br.com.fiap.SysFeedback.application.dto.UserRequestDTO;
import br.com.fiap.SysFeedback.application.dto.UserResponseDTO;
import br.com.fiap.SysFeedback.application.dto.UserUpdateDTO;
import br.com.fiap.SysFeedback.application.usecase.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * Controlador REST responsável pelas operações de gerenciamento de usuários
 * (criação, consulta, atualização e remoção).
 *
 * @author Thiago de Jesus
 */
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCreateUseCase userCreateUseCase;
    private final UserFindByEmailUseCase userFindByEmailUseCase;
    private final UserFindAllUseCase userFindAllUseCase;
    private final UserUpdateUseCase userUpdateUseCase;
    private final UserDeleteUseCase userDeleteUseCase;

    /**
     * Cria um novo usuário a partir dos dados informados.
     *
     * @param  request  dados de criação do usuário
     * @return resposta HTTP 201 com os dados do usuário criado
     *
     * @author Thiago de Jesus
     */
    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO request) {
        UserResponseDTO response = userCreateUseCase.execute(request);
        return ResponseEntity.status(201).body(response);
    }

    /**
     * Busca um usuário pelo e-mail informado.
     *
     * @param  email  e-mail do usuário a ser consultado
     * @return resposta HTTP 200 com os dados do usuário encontrado
     *
     * @author Thiago de Jesus
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> findByEmail(@PathVariable String email) {
        UserResponseDTO response = userFindByEmailUseCase.execute(email);
        return ResponseEntity.ok(response);
    }

    /**
     * Lista todos os usuários cadastrados.
     *
     * @return resposta HTTP 200 com a lista de usuários
     *
     * @author Thiago de Jesus
     */
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserResponseDTO> response = userFindAllUseCase.execute();
        return ResponseEntity.ok(response);
    }

    /**
     * Atualiza os dados de um usuário existente.
     *
     * @param  id       identificador do usuário a ser atualizado
     * @param  request  novos dados do usuário
     * @return resposta HTTP 200 com os dados do usuário atualizado
     *
     * @author Thiago de Jesus
     */
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UserUpdateDTO request) {
        UserResponseDTO response = userUpdateUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * Remove o usuário identificado pelo id.
     *
     * @param  id  identificador do usuário a ser removido
     * @return resposta HTTP 204 sem conteúdo
     *
     * @author Thiago de Jesus
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
