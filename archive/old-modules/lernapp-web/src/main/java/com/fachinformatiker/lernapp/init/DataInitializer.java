package com.fachinformatiker.lernapp.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Automatischer Daten-Import beim Start
 * Lädt Fragen aus CSV-Dateien in die H2-Datenbank
 */
@Configuration
@Slf4j
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(JdbcTemplate jdbcTemplate) {
        return args -> {
            log.info("=== CHECKING DATABASE FOR QUESTIONS ===");
            
            try {
                // Check if questions table exists and has data
                Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM questions", Integer.class
                );
                
                log.info("Found {} questions in database", count);
                
                if (count == 0) {
                    log.info("No questions found. Attempting to import sample data...");
                    importSampleQuestions(jdbcTemplate);
                } else {
                    log.info("Questions already exist. Skipping import.");
                }
                
            } catch (Exception e) {
                log.warn("Could not check questions table: {}", e.getMessage());
                
                // Try to create table and import data
                try {
                    createQuestionsTable(jdbcTemplate);
                    importSampleQuestions(jdbcTemplate);
                } catch (Exception ex) {
                    log.error("Failed to initialize database: {}", ex.getMessage());
                }
            }
        };
    }
    
    private void createQuestionsTable(JdbcTemplate jdbcTemplate) {
        log.info("Creating questions table...");
        
        String sql = """
            CREATE TABLE IF NOT EXISTS questions (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                topic VARCHAR(255),
                subtopic VARCHAR(255),
                question_text VARCHAR(1000) NOT NULL,
                answer_a VARCHAR(500),
                answer_b VARCHAR(500),
                answer_c VARCHAR(500),
                answer_d VARCHAR(500),
                correct_answer VARCHAR(10),
                difficulty VARCHAR(50),
                points INT,
                explanation VARCHAR(1000),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        jdbcTemplate.execute(sql);
        log.info("Questions table created");
    }
    
    private void importSampleQuestions(JdbcTemplate jdbcTemplate) {
        log.info("Importing sample questions...");
        
        // Sample questions for testing
        String[][] sampleData = {
            {"Java Grundlagen", "Datentypen", "Was ist der Unterschied zwischen int und Integer?", 
             "int ist primitiv, Integer ist Wrapper-Klasse", "Kein Unterschied", 
             "Integer ist schneller", "int kann null sein", "A", "MEDIUM", "3",
             "int ist ein primitiver Datentyp, Integer ist die Wrapper-Klasse"},
             
            {"Datenbanken", "SQL", "Welcher SQL-Befehl fügt Daten ein?", 
             "INSERT", "SELECT", "UPDATE", "DELETE", "A", "EASY", "2",
             "INSERT fügt neue Datensätze in eine Tabelle ein"},
             
            {"Netzwerke", "TCP/IP", "Auf welcher OSI-Schicht arbeitet TCP?", 
             "Schicht 4 (Transport)", "Schicht 3 (Network)", 
             "Schicht 2 (Data Link)", "Schicht 7 (Application)", "A", "MEDIUM", "3",
             "TCP arbeitet auf der Transportschicht (Layer 4)"},
             
            {"Hardware", "RAID", "Welches RAID bietet Redundanz und Performance?", 
             "RAID 0", "RAID 1", "RAID 5", "RAID 10", "D", "HARD", "4",
             "RAID 10 kombiniert Mirroring und Striping"},
             
            {"Projektmanagement", "Scrum", "Wie lange dauert ein Sprint normalerweise?", 
             "1 Tag", "1-4 Wochen", "3 Monate", "1 Jahr", "B", "EASY", "2",
             "Sprints dauern typischerweise 1-4 Wochen"}
        };
        
        String insertSql = """
            INSERT INTO questions (topic, subtopic, question_text, answer_a, answer_b, 
                                  answer_c, answer_d, correct_answer, difficulty, points, explanation)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        int imported = 0;
        for (String[] row : sampleData) {
            try {
                jdbcTemplate.update(insertSql, (Object[]) row);
                imported++;
                log.debug("Imported question: {}", row[2]);
            } catch (Exception e) {
                log.warn("Failed to import question: {}", e.getMessage());
            }
        }
        
        log.info("✅ Imported {} sample questions", imported);
        
        // Try to import from CSV file if exists
        tryImportFromCsvFile(jdbcTemplate);
    }
    
    private void tryImportFromCsvFile(JdbcTemplate jdbcTemplate) {
        String[] csvPaths = {
            "C:\\SoftwareEntwicklung\\Fachinformatiker_Lernapp_Java\\data\\ALLE_AP1_FRAGEN_IMPORT.csv",
            "data/ALLE_AP1_FRAGEN_IMPORT.csv",
            "data/sample_questions.csv"
        };
        
        for (String path : csvPaths) {
            File file = new File(path);
            if (file.exists() && file.canRead()) {
                log.info("Found CSV file: {}", path);
                importCsvFile(jdbcTemplate, file);
                break;
            }
        }
    }
    
    private void importCsvFile(JdbcTemplate jdbcTemplate, File csvFile) {
        log.info("Importing questions from CSV: {}", csvFile.getName());
        
        String insertSql = """
            INSERT INTO questions (topic, subtopic, question_text, answer_a, answer_b, 
                                  answer_c, answer_d, correct_answer, difficulty, points, explanation)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        try (BufferedReader reader = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean firstLine = true;
            int imported = 0;
            int failed = 0;
            
            while ((line = reader.readLine()) != null) {
                if (firstLine) {
                    firstLine = false;
                    continue; // Skip header
                }
                
                try {
                    String[] parts = line.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
                    
                    if (parts.length >= 11) {
                        // Clean up quotes
                        for (int i = 0; i < parts.length; i++) {
                            parts[i] = parts[i].replace("\"", "").trim();
                        }
                        
                        // Map CSV columns to database
                        String topic = parts[2]; // main_category
                        String subtopic = parts[3]; // sub_category
                        String question = parts[5]; // question_text
                        String answerA = parts[6];
                        String answerB = parts[7];
                        String answerC = parts[8];
                        String answerD = parts[9];
                        String correct = parts[10]; // correct_answer
                        String difficulty = parts[11]; // difficulty
                        String points = parts[12]; // points
                        String explanation = parts.length > 13 ? parts[13] : "";
                        
                        jdbcTemplate.update(insertSql, 
                            topic, subtopic, question, answerA, answerB, 
                            answerC, answerD, correct, difficulty, points, explanation
                        );
                        
                        imported++;
                    }
                } catch (Exception e) {
                    failed++;
                    log.debug("Failed to import line: {}", e.getMessage());
                }
            }
            
            log.info("✅ CSV Import complete: {} imported, {} failed", imported, failed);
            
        } catch (Exception e) {
            log.error("Failed to read CSV file: {}", e.getMessage());
        }
    }
}
