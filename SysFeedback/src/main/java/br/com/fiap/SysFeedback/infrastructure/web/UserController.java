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

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserCreateUseCase userCreateUseCase;
    private final UserFindByEmailUseCase userFindByEmailUseCase;
    private final UserFindAllUseCase userFindAllUseCase;
    private final UserUpdateUseCase userUpdateUseCase;
    private final UserDeleteUseCase userDeleteUseCase;

    @PostMapping
    public ResponseEntity<UserResponseDTO> create(@RequestBody UserRequestDTO request) {
        UserResponseDTO response = userCreateUseCase.execute(request);
        return ResponseEntity.status(201).body(response);
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserResponseDTO> findByEmail(@PathVariable String email) {
        UserResponseDTO response = userFindByEmailUseCase.execute(email);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> findAll() {
        List<UserResponseDTO> response = userFindAllUseCase.execute();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> update(
            @PathVariable UUID id,
            @RequestBody UserUpdateDTO request) {
        UserResponseDTO response = userUpdateUseCase.execute(id, request);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        userDeleteUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
