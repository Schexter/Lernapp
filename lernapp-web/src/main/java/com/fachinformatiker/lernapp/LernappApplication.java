/**
 * Spring Boot Application Main Class
 * 
 * Fachinformatiker Lernapp - Eine hochperformante Java-basierte Lernplattform
 * für Fachinformatiker-Azubis mit Enterprise-Features.
 * 
 * @author Hans Hahn
 * @version 1.0.0
 * @since 2025-08-13
 * 
 * Alle Rechte vorbehalten - Hans Hahn
 */
package com.fachinformatiker.lernapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Hauptklasse der Fachinformatiker Lernapp.
 * 
 * Diese Klasse startet die Spring Boot Anwendung und aktiviert
 * alle notwendigen Features:
 * - JPA Auditing für automatische Zeitstempel
 * - Caching für bessere Performance  
 * - Asynchrone Verarbeitung
 * - Task Scheduling
 * - Configuration Properties
 */
@SpringBootApplication
@EnableJpaAuditing
@EnableCaching
@EnableAsync
@EnableScheduling
@EnableConfigurationProperties
public class LernappApplication {

    private static final Logger log = LoggerFactory.getLogger(LernappApplication.class);

    /**
     * Main-Methode zum Starten der Anwendung.
     * 
     * @param args Kommandozeilen-Argumente
     */
    public static void main(String[] args) {
        // Banner und Startup-Info
        log.info("🚀 Starte Fachinformatiker Lernapp...");
        log.info("📊 Java Version: {}", System.getProperty("java.version"));
        log.info("🔧 Verfügbare Prozessoren: {}", Runtime.getRuntime().availableProcessors());
        log.info("💾 Maximaler Heap-Speicher: {} MB", 
                Runtime.getRuntime().maxMemory() / 1024 / 1024);
        
        try {
            SpringApplication application = new SpringApplication(LernappApplication.class);
            
            // Performance Optimierungen
            application.setLogStartupInfo(true);
            application.setRegisterShutdownHook(true);
            
            // Starte die Anwendung
            var context = application.run(args);
            
            log.info("✅ Fachinformatiker Lernapp erfolgreich gestartet!");
            log.info("🌐 Verfügbare Profile: {}", 
                    String.join(", ", context.getEnvironment().getActiveProfiles()));
            log.info("🔗 Lokale URL: http://localhost:{}", 
                    context.getEnvironment().getProperty("server.port", "8080"));
            
        } catch (Exception e) {
            log.error("❌ Fehler beim Starten der Anwendung: {}", e.getMessage(), e);
            System.exit(1);
        }
    }
}
