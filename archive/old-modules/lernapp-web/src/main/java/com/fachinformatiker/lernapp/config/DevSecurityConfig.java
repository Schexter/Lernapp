package com.fachinformatiker.lernapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * KOMPLETT DEAKTIVIERTE Security für Development - ALLE APIs OFFEN
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
//@Configuration  // DEAKTIVIERT - Ersetzt durch ProperSecurityConfig
@EnableWebSecurity
@Profile({"dev", "default"})
@Order(1)  // Höchste Priorität - überschreibt andere Security Configs
public class DevSecurityConfig {


    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("============================================");
        System.out.println("   🔓 DEV SECURITY: ALLES DEAKTIVIERT");
        System.out.println("   🔓 ALLE API-ENDPUNKTE SIND ÖFFENTLICH");
        System.out.println("   🔓 KEINE AUTHENTIFIZIERUNG ERFORDERLICH");
        System.out.println("============================================");
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                // ALLES KOMPLETT ERLAUBEN - KEINE RESTRICTIONS
                .anyRequest().permitAll()
            )
            .formLogin(AbstractHttpConfigurer::disable)  // Deaktiviere Form Login komplett
            .httpBasic(AbstractHttpConfigurer::disable)  // Deaktiviere HTTP Basic Auth
            .logout(AbstractHttpConfigurer::disable);    // Deaktiviere Logout

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        // Benutzer für Form-Login (falls benötigt)
        UserDetails student = User.builder()
            .username("student")
            .password(passwordEncoder.encode("student123"))
            .roles("STUDENT")
            .build();
        manager.createUser(student);
        
        System.out.println("====================================");
        System.out.println("   DEV MODE - NO SECURITY");
        System.out.println("   User: student / Pass: student123");
        System.out.println("   API: NO AUTH REQUIRED");
        System.out.println("====================================");
        
        return manager;
    }
}
