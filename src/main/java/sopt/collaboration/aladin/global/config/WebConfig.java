package sopt.collaboration.aladin.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Value("${app.cors.allowed-origins}")
    private String[] allowedOrigins;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        configureCors(registry, "/api/**");
        configureCors(registry, "/v1/**");
    }

    private void configureCors(CorsRegistry registry, String pathPattern) {
        registry.addMapping(pathPattern)
                .allowedOrigins(allowedOrigins)
                .allowedMethods(
                        "GET", "POST", "PUT",
                        "PATCH", "DELETE", "OPTIONS"
                )
                .allowedHeaders("*")
                .maxAge(3600);
    }
}
