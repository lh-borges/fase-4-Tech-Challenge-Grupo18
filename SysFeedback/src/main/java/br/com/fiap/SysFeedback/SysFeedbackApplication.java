package br.com.fiap.SysFeedback;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Classe principal que inicializa a aplicação Spring Boot SysFeedback.
 *
 * @author Thiago de Jesus
 */
@SpringBootApplication
public class SysFeedbackApplication {

	/**
	 * Ponto de entrada da aplicação.
	 *
	 * @param  args  argumentos de linha de comando
	 *
	 * @author Thiago de Jesus
	 */
	public static void main(String[] args) {
		SpringApplication.run(SysFeedbackApplication.class, args);
	}

}
