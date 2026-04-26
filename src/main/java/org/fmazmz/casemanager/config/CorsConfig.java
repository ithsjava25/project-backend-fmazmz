package org.fmazmz.casemanager.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${app.frontend.url}")
    private String frontendUrl;

    @Value("${app.cors.allow-localhost-origins:false}")
    private boolean allowLocalhostOrigins;

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                var reg = registry.addMapping("/**")
                        .allowedMethods("GET", "HEAD", "POST", "PUT", "PATCH", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);
                if (allowLocalhostOrigins) {
                    reg.allowedOriginPatterns("http://localhost:*", "http://127.0.0.1:*");
                } else {
                    reg.allowedOrigins(frontendUrl);
                }
            }
        };
    }
}
