package fiap.com.br.petcarehub.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    private static final String ESQUEMA_JWT = "bearer-jwt";

    @Bean
    public OpenAPI petCareHubOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title("PetCare Hub - API Java")
                        .version("1.0.0")
                        .description("""
                                API principal do PetCare Hub: pets, responsáveis, consultas, leituras IoT, \
                                score de saúde, alertas e plano preventivo.

                                **Como autenticar:** chame `POST /auth/login`, copie o campo `token` da \
                                resposta e cole no botão **Authorize**, só o token, sem escrever "Bearer".
                                """))
                // Sem isto o Swagger nao exibe o botao Authorize: a API inteira e
                // protegida por JWT, mas a interface nao tinha como enviar o token,
                // entao todo endpoint autenticado respondia 401 por aqui.
                .components(new Components().addSecuritySchemes(ESQUEMA_JWT,
                        new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .in(SecurityScheme.In.HEADER)
                                .name("Authorization")
                                .description("Token devolvido por /auth/login, /auth/ativar-conta ou /auth/ativar-clinica.")))
                // Aplicado globalmente: as rotas publicas de /auth ignoram o header,
                // entao nao atrapalha quem ainda nao tem token.
                .addSecurityItem(new SecurityRequirement().addList(ESQUEMA_JWT));
    }
}
