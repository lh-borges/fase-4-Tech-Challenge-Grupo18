package br.com.fiap.SysFeedback.application.security;

import br.com.fiap.SysFeedback.fixture.Fixture;
import br.com.fiap.SysFeedback.infrastructure.security.adapter.BCryptPasswordEncoderAdapter;
import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PasswordEncoderPortTest {

    private final PasswordEncoder springPasswordEncoder = mock(PasswordEncoder.class);
    private final PasswordEncoderPort passwordEncoderPort =
            new BCryptPasswordEncoderAdapter(springPasswordEncoder);

    @Test
    void deveDelegarEncodeParaPasswordEncoderDoSpring() {
        when(springPasswordEncoder.encode(Fixture.USER_PASSWORD))
                .thenReturn(Fixture.USER_ENCODED_PASSWORD);

        String encoded = passwordEncoderPort.encode(Fixture.USER_PASSWORD);

        assertEquals(Fixture.USER_ENCODED_PASSWORD, encoded);
        verify(springPasswordEncoder).encode(Fixture.USER_PASSWORD);
    }

    @Test
    void deveDelegarMatchesParaPasswordEncoderDoSpring() {
        when(springPasswordEncoder.matches(Fixture.USER_PASSWORD, Fixture.USER_ENCODED_PASSWORD))
                .thenReturn(true);

        boolean matches = passwordEncoderPort.matches(Fixture.USER_PASSWORD, Fixture.USER_ENCODED_PASSWORD);

        assertTrue(matches);
        verify(springPasswordEncoder).matches(Fixture.USER_PASSWORD, Fixture.USER_ENCODED_PASSWORD);
    }
}
