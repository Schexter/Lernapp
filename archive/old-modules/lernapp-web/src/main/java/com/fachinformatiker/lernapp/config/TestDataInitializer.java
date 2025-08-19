package com.fachinformatiker.lernapp.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 * Erstellt Test-User für die Entwicklung
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Configuration
@Profile({"dev", "default"})
public class TestDataInitializer {

    @Bean
    CommandLineRunner initTestData(JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        return args -> {
            // Prüfe ob Tabellen existieren
            try {
                Integer userCount = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM users", Integer.class
                );
                
                if (userCount == 0) {
                    System.out.println("\n========================================");
                    System.out.println("   Erstelle Test-User...");
                    System.out.println("========================================\n");
                    
                    // Admin User
                    String adminPassword = passwordEncoder.encode("admin123");
                    jdbcTemplate.update(
                        "INSERT INTO users (username, email, password, enabled, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)",
                        "admin", "admin@lernapp.de", adminPassword, true, "Admin", "User"
                    );
                    System.out.println("✅ Admin erstellt: admin@lernapp.de / admin123");
                    
                    // Student User
                    String studentPassword = passwordEncoder.encode("student123");
                    jdbcTemplate.update(
                        "INSERT INTO users (username, email, password, enabled, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)",
                        "student", "student@lernapp.de", studentPassword, true, "Max", "Mustermann"
                    );
                    System.out.println("✅ Student erstellt: student@lernapp.de / student123");
                    
                    // Hans User (für dich!)
                    String hansPassword = passwordEncoder.encode("hans2024");
                    jdbcTemplate.update(
                        "INSERT INTO users (username, email, password, enabled, first_name, last_name) VALUES (?, ?, ?, ?, ?, ?)",
                        "hans", "hans@lernapp.de", hansPassword, true, "Hans", "Hahn"
                    );
                    System.out.println("✅ Hans erstellt: hans@lernapp.de / hans2024");
                    
                    System.out.println("\n========================================\n");
                }
            } catch (Exception e) {
                // Tabellen existieren noch nicht, das ist OK
                System.out.println("⚠️ User-Tabelle noch nicht bereit, überspringe Initialisierung");
            }
        };
    }
}