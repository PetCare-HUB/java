package fiap.com.br.petcarehub.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI petCareHubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCare Hub - API Java")
                        .version("1.0.0")
                        .description("API principal do PetCare Hub: pets, responsáveis, consultas, leituras IoT, score de saúde, alertas e plano preventivo."));
    }
}
