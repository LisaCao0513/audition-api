package com.audition.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(authorizeRequests ->
                authorizeRequests
                    // Allow access to info and health endpoints without authentication
                    .requestMatchers("/actuator/info", "/actuator/health").permitAll()
                    // Secure all other actuator endpoints
                    .requestMatchers("/actuator/**").authenticated()
                    .anyRequest().authenticated() // use basic authentication for now
            )
            .csrf(AbstractHttpConfigurer::disable)// Disable CSRF protection for actuator endpoints
            .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
