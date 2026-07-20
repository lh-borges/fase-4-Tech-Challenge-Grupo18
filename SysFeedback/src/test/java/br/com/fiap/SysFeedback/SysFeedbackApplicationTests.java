package br.com.fiap.SysFeedback;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

/**
 * Testes de integração que verificam a inicialização do contexto Spring da aplicação.
 *
 * @author Thiago de Jesus
 */
@SpringBootTest
@ActiveProfiles("test")
class SysFeedbackApplicationTests {

	/**
	 * Verifica se o contexto da aplicação é carregado sem erros.
	 *
	 * @author Thiago de Jesus
	 */
	@Test
	void contextLoads() {
	}

}
