package de.lernapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Hauptklasse der Fachinformatiker Lernapp
 * 
 * Diese Spring Boot Anwendung bietet eine Lernplattform
 * für angehende Fachinformatiker mit:
 * - Lernmodulen
 * - Übungen
 * - Prüfungssimulationen
 * - Fortschrittsverfolgung
 */
@SpringBootApplication
public class LernappApplication {

    public static void main(String[] args) {
        SpringApplication.run(LernappApplication.class, args);
    }
}
