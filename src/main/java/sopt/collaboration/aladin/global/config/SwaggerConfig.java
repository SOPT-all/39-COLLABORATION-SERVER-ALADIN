package sopt.collaboration.aladin.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("알라딘(Aladin) API")
                                .description("PLAY SOPT 합동세미나 Mobile-Web 3조 알라딘 API")
                                .version("v1")
                );
    }
}