package inf012.apiclinica;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;

/**
 * Aplicação principal da API Clínica.
 *
 * Responsável por inicializar o contexto Spring Boot do serviço core
 * (pacientes, médicos, consultas e autenticação).
 */
@SpringBootApplication
@OpenAPIDefinition(info = @Info(
	title = "API Clínica",
	version = "v1",
	description = "Serviço principal da Clínica: pacientes, médicos, consultas e autenticação."
))
public class ApiclinicaApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApiclinicaApplication.class, args);
	}

}
