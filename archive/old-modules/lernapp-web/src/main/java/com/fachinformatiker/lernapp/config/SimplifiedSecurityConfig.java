package com.fachinformatiker.lernapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

/**
 * VEREINFACHTE Security-Konfiguration für Development
 * Alle Seiten sind zugänglich, nur API-Endpunkte benötigen Auth
 */
@Configuration
@EnableWebSecurity
@Order(1)  // Höchste Priorität
public class SimplifiedSecurityConfig {

    @Bean
    @Primary
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("================================================");
        System.out.println("   🔧 SIMPLIFIED SECURITY CONFIG ACTIVE");
        System.out.println("   ✅ Web Pages: No Auth Required");
        System.out.println("   ✅ API Endpoints: Basic Auth");
        System.out.println("   ✅ Test Users: demo/demo123");
        System.out.println("================================================");
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                // H2 Console
                .requestMatchers("/h2-console/**").permitAll()
                
                // Alle Web-Seiten OHNE Auth
                .requestMatchers("/", "/login", "/register", "/dashboard", "/dashboard/**").permitAll()
                .requestMatchers("/learn", "/learn/**").permitAll()
                .requestMatchers("/practice", "/practice/**").permitAll()
                .requestMatchers("/exams", "/exams/**").permitAll()
                .requestMatchers("/progress", "/progress/**").permitAll()
                .requestMatchers("/quick-login", "/static-dashboard").permitAll()
                
                // Statische Ressourcen
                .requestMatchers("/css/**", "/js/**", "/images/**", "/webjars/**").permitAll()
                
                // API Auth Endpoints sind öffentlich
                .requestMatchers("/api/auth/**").permitAll()
                
                // Andere API Endpoints - optional mit Auth
                .requestMatchers("/api/**").permitAll()
                
                // Alles andere erlauben
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login")
                .permitAll()
            );

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
    @Primary
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        // Test-Benutzer
        manager.createUser(User.builder()
            .username("demo")
            .password(passwordEncoder.encode("demo123"))
            .roles("STUDENT")
            .build());
            
        manager.createUser(User.builder()
            .username("student")
            .password(passwordEncoder.encode("student123"))
            .roles("STUDENT")
            .build());
            
        manager.createUser(User.builder()
            .username("teacher")
            .password(passwordEncoder.encode("teacher123"))
            .roles("TEACHER")
            .build());
            
        manager.createUser(User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN")
            .build());
        
        return manager;
    }
    
    // passwordEncoder Bean is defined in PasswordEncoderConfig
    
    @Bean
    @Primary
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}
