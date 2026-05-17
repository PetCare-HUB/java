package fiap.com.br.petcarehub.config;

import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.OpenAPI;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

	@Bean
	public OpenAPI customOpenAPI() {
		return new OpenAPI()
				.info(new Info()
						.title("PetCarehub API")
						.version("1.0")
						.description("API para gestão de pets, responsáveis, clínicas e consultas")
				);
	}
}
