package br.com.fiap.SysFeedback.domain.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RoleTest {

    @Test
    void deveConterRolesEsperadas() {
        assertArrayEquals(
                new Role[]{Role.ADMIN, Role.PROFESSOR, Role.ALUNO},
                Role.values()
        );
    }

    @Test
    void deveConverterStringParaRole() {
        assertEquals(Role.ADMIN, Role.valueOf("ADMIN"));
        assertEquals(Role.PROFESSOR, Role.valueOf("PROFESSOR"));
        assertEquals(Role.ALUNO, Role.valueOf("ALUNO"));
    }
}
