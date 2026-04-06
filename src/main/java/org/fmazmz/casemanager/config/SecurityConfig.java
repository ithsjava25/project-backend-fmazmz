package org.fmazmz.casemanager.config;

import org.fmazmz.casemanager.user.application.UserAuthentication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, UserAuthentication userAuthentication) throws Exception {
        SavedRequestAwareAuthenticationSuccessHandler defaultSuccessHandler =
                new SavedRequestAwareAuthenticationSuccessHandler();
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(oauth -> oauth.successHandler((request, response, authentication) -> {
                    try {
                        if (authentication instanceof OAuth2AuthenticationToken token) {
                            userAuthentication.resolveUser(token);
                        }
                        defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication);
                    } catch (IllegalStateException | IllegalArgumentException ex) {
                        response.setStatus(403);
                        response.setContentType("application/json");
                        response.getWriter().write("{\"error\":\"" + ex.getMessage().replace("\"", "\\\"") + "\"}");
                    }
                }))
                .logout(logout -> logout.logoutSuccessUrl("/"))
                .csrf(csrf -> csrf.disable())
                ;

        return http.build();
    }
}
