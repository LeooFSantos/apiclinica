package apiclinica.email;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@OpenAPIDefinition(info = @Info(
    title = "Email Service - API Clínica",
    version = "v1",
    description = "Microserviço responsável pelo envio de e-mails (ex.: confirmação de consulta)."
))
public class EmailServiceApplication {

  public static void main(String[] args) {
    SpringApplication.run(EmailServiceApplication.class, args);
  }
}
