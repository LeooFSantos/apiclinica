package inf012.apiclinica.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI apiClinicaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API Clínica")
                        .version("v1")
                        .description("Documentação da API Clínica")
                        .contact(new Contact().name("API Clínica")));
    }
}
