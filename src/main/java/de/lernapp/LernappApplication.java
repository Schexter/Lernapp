package de.lernapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * MINIMALE Fachinformatiker Lernapp - Test Version
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@SpringBootApplication
public class LernappApplication {

    public static void main(String[] args) {
        System.out.println("🚀 Starting MINIMAL Fachinformatiker Lernapp Backend...");
        SpringApplication.run(LernappApplication.class, args);
        System.out.println("✅ Backend started successfully on http://localhost:8080");
    }
}
