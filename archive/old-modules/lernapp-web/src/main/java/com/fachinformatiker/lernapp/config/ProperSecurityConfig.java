package com.fachinformatiker.lernapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Primary;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
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
 * RICHTIGE Security Konfiguration - Mit Schutz für wichtige Bereiche
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
//@Configuration  // DEAKTIVIERT - Verwende SimplifiedSecurityConfig
@EnableWebSecurity
@Profile("old-config")  // Nur mit speziellem Profil aktivieren
@Order(99)  // Niedrige Priorität
public class ProperSecurityConfig {

    @Bean
    @Primary
    public AuthenticationManager authenticationManager(
            UserDetailsService userDetailsService,
            PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(authProvider);
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        System.out.println("============================================");
        System.out.println("   🔒 PROPER SECURITY AKTIVIERT");
        System.out.println("   ✅ Öffentlich: /, /login, /register, /api/auth/**");
        System.out.println("   🔐 Geschützt: /dashboard, /admin, /api/user/**");
        System.out.println("============================================");
        
        http
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(AbstractHttpConfigurer::disable)
            .headers(headers -> headers.frameOptions(frame -> frame.disable()))
            .authorizeHttpRequests(auth -> auth
                // ✅ ÖFFENTLICHE BEREICHE (ohne Login)
                .requestMatchers("/", "/home", "/index.html").permitAll()
                .requestMatchers("/login", "/register", "/simple-login.html").permitAll()
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/demo/**", "/public/**").permitAll()
                
                // ✅ STATISCHE RESOURCES (CSS, JS, Images)
                .requestMatchers("/static/**", "/css/**", "/js/**", "/images/**").permitAll()
                .requestMatchers("/favicon.ico", "/robots.txt").permitAll()
                
                // ✅ DEVELOPER TOOLS (H2 Console, Swagger)
                .requestMatchers("/h2-console/**", "/swagger-ui/**", "/api-docs/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                
                // ✅ API ENDPOINTS - Verschiedene Schutzlevel
                .requestMatchers("/api/questions/**").permitAll() // Fragen können öffentlich sein
                .requestMatchers("/api/topics/**").permitAll()    // Topics auch öffentlich
                .requestMatchers("/api/statistics/**").permitAll() // Statistiken öffentlich
                
                // 🔐 GESCHÜTZTE API BEREICHE
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                .requestMatchers("/api/user/profile/**").authenticated()
                .requestMatchers("/api/learning/**").authenticated()
                
                // 🔐 GESCHÜTZTE WEB-BEREICHE  
                .requestMatchers("/dashboard/**").authenticated()
                .requestMatchers("/profile/**").authenticated()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                
                // ✅ ALLES ANDERE erstmal erlaubt (für Development)
                .anyRequest().permitAll()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/dashboard", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
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
    public UserDetailsService userDetailsService(PasswordEncoder passwordEncoder) {
        InMemoryUserDetailsManager manager = new InMemoryUserDetailsManager();
        
        // 👨‍🎓 STUDENT USER
        UserDetails student = User.builder()
            .username("student")
            .password(passwordEncoder.encode("student123"))
            .roles("STUDENT", "USER")
            .build();
        manager.createUser(student);
        
        // 🎯 DEMO USER
        UserDetails demo = User.builder()
            .username("demo")
            .password(passwordEncoder.encode("demo123"))
            .roles("STUDENT", "USER")
            .build();
        manager.createUser(demo);
        
        // 👨‍🏫 TEACHER USER
        UserDetails teacher = User.builder()
            .username("teacher")
            .password(passwordEncoder.encode("teacher123"))
            .roles("TEACHER", "USER")
            .build();
        manager.createUser(teacher);
        
        // 🔑 ADMIN USER
        UserDetails admin = User.builder()
            .username("admin")
            .password(passwordEncoder.encode("admin123"))
            .roles("ADMIN", "TEACHER", "USER")
            .build();
        manager.createUser(admin);
        
        System.out.println("====================================");
        System.out.println("   🔒 SECURITY ACTIVE - Test Users:");
        System.out.println("   👨‍🎓 student / student123 (STUDENT)");
        System.out.println("   🎯 demo / demo123 (STUDENT)");
        System.out.println("   👨‍🏫 teacher / teacher123 (TEACHER)");
        System.out.println("   🔑 admin / admin123 (ADMIN)");
        System.out.println("====================================");
        
        return manager;
    }
}