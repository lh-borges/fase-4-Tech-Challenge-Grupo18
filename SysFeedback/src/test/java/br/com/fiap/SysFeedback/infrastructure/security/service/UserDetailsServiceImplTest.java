package br.com.fiap.SysFeedback.infrastructure.security.service;

import br.com.fiap.SysFeedback.application.repository.RepositoryUserPort;
import br.com.fiap.SysFeedback.domain.entity.User;
import br.com.fiap.SysFeedback.domain.enums.Role;
import br.com.fiap.SysFeedback.fixture.Fixture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class UserDetailsServiceImplTest {

    private final RepositoryUserPort repositoryUserPort = mock(RepositoryUserPort.class);

    private UserDetailsServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new UserDetailsServiceImpl(repositoryUserPort);
    }

    @Test
    void deveCarregarUsuarioPorEmail() {
        when(repositoryUserPort.findByEmail(Fixture.USER_EMAIL)).thenReturn(Optional.of(Fixture.user()));

        UserDetails result = service.loadUserByUsername(Fixture.USER_EMAIL);

        assertEquals(Fixture.USER_EMAIL, result.getUsername());
        assertEquals(Fixture.USER_PASSWORD, result.getPassword());
        assertTrue(result.isAccountNonExpired());
        assertTrue(result.isAccountNonLocked());
        assertTrue(result.isCredentialsNonExpired());
        assertTrue(result.isEnabled());
        assertTrue(result.getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_ALUNO".equals(authority.getAuthority())));
        verify(repositoryUserPort).findByEmail(Fixture.USER_EMAIL);
    }

    @Test
    void deveMontarAuthorityComRoleDoUsuario() {
        User admin = new User(
                Fixture.USER_ID,
                "Admin Teste",
                "admin@fiap.com",
                Fixture.USER_PASSWORD,
                Role.ADMIN,
                Fixture.USER_CREATED_AT
        );
        when(repositoryUserPort.findByEmail("admin@fiap.com")).thenReturn(Optional.of(admin));

        UserDetails result = service.loadUserByUsername("admin@fiap.com");

        assertEquals("admin@fiap.com", result.getUsername());
        assertTrue(result.getAuthorities()
                .stream()
                .anyMatch(authority -> "ROLE_ADMIN".equals(authority.getAuthority())));
    }

    @Test
    void deveLancarUsernameNotFoundExceptionQuandoEmailNaoExistir() {
        when(repositoryUserPort.findByEmail("naoexiste@fiap.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername("naoexiste@fiap.com")
        );

        assertEquals("Email não encontrado: naoexiste@fiap.com", exception.getMessage());
        verify(repositoryUserPort).findByEmail("naoexiste@fiap.com");
    }

    @Test
    void deveLancarUsernameNotFoundExceptionQuandoEmailForNuloENaoHouverUsuario() {
        when(repositoryUserPort.findByEmail(null)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(
                UsernameNotFoundException.class,
                () -> service.loadUserByUsername(null)
        );

        assertEquals("Email não encontrado: null", exception.getMessage());
        verify(repositoryUserPort).findByEmail(null);
    }

    @Test
    void devePropagarFalhaDoRepositorio() {
        when(repositoryUserPort.findByEmail(Fixture.USER_EMAIL))
                .thenThrow(new RuntimeException("Falha ao consultar usuario"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.loadUserByUsername(Fixture.USER_EMAIL)
        );

        assertEquals("Falha ao consultar usuario", exception.getMessage());
        verify(repositoryUserPort).findByEmail(Fixture.USER_EMAIL);
    }
}
