package de.lernapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Vereinfachte Security-Konfiguration für die Entwicklung
 * 
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CSRF für API-Endpunkte deaktivieren (nur für Entwicklung!)
            .csrf(csrf -> csrf.disable())
            
            // H2-Console Frame-Options deaktivieren
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            
            // Alle Requests erlauben (nur für Entwicklung!)
            .authorizeHttpRequests(auth -> auth
                .anyRequest().permitAll()
            );
            
        return http.build();
    }
}
