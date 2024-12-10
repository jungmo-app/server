package jungmo.server.global.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        final String security = "bearerAuth";
        return new OpenAPI()
                .info(new Info()
                        .title("Jungmo API")
                        .version("1.0.0")
                        .description("jungmo project API"))
                .addSecurityItem(new SecurityRequirement().addList(security))
                .components(new Components()
                        .addSecuritySchemes(security, new SecurityScheme()
                                .name(security)
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}

