package de.lernapp.service;

import de.lernapp.model.Question;
import de.lernapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.BufferedReader;
import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Importiert AP1-Fragen aus CSV-Datei beim Start
 * 
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Configuration
@RequiredArgsConstructor
@Slf4j
public class DataImportService {
    
    private final QuestionRepository questionRepository;
    private final QuestionService questionService;
    
    @Bean
    CommandLineRunner importData() {
        return args -> {
            // Prüfe ob schon Fragen vorhanden sind
            long count = questionRepository.count();
            log.info("Aktuelle Anzahl Fragen in DB: {}", count);
            
            // Wenn weniger als 50 Fragen, importiere AP1-Fragen
            if (count < 50) {
                log.info("Starte Import der AP1-Fragen...");
                importAP1Questions();
            }
        };
    }
    
    private void importAP1Questions() {
        // VERSUCHE ZUERST DIE GROSSE CSV MIT 600 FRAGEN!
        String csvFile = "C:\\SoftwareEntwicklung\\Fachinformatiker_Lernapp_Java\\data\\complete_questions\\MASTER_600_QUESTIONS_SAMPLE.csv";
        boolean isMasterFormat = true;
        
        // Falls diese nicht existiert, versuche die kleine CSV
        if (!Files.exists(Paths.get(csvFile))) {
            log.info("Master CSV nicht gefunden, versuche alternative CSV...");
            csvFile = "C:\\SoftwareEntwicklung\\Fachinformatiker_Lernapp_Java\\data\\ALLE_AP1_FRAGEN_IMPORT.csv";
            isMasterFormat = false;
        }
        
        try {
            if (!Files.exists(Paths.get(csvFile))) {
                log.warn("Keine CSV-Datei gefunden!");
                return;
            }
            
            log.info("Importiere aus: {} (Format: {})", csvFile, isMasterFormat ? "MASTER" : "STANDARD");
            List<Question> questions = new ArrayList<>();
            
            try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
                String line;
                boolean firstLine = true;
                int lineCount = 0;
                int importedCount = 0;
                
                while ((line = br.readLine()) != null) {
                    lineCount++;
                    
                    // Überspringe Header
                    if (firstLine) {
                        firstLine = false;
                        log.info("Header-Zeile übersprungen");
                        continue;
                    }
                    
                    // CSV parsen
                    String[] values = parseCSVLine(line);
                    
                    try {
                        Question question = new Question();
                        
                        if (isMasterFormat) {
                            // MASTER FORMAT (11 Spalten):
                            // topic,subtopic,question,answerA,answerB,answerC,answerD,correctAnswer,difficulty,explanation,tags
                            if (values.length >= 11) {
                                question.setCategory(cleanCsvValue(values[0])); // topic
                                question.setQuestionText(cleanCsvValue(values[2])); // question
                                
                                // Antworten
                                question.setOptionA(cleanCsvValue(values[3])); // answerA
                                question.setOptionB(cleanCsvValue(values[4])); // answerB
                                question.setOptionC(cleanCsvValue(values[5])); // answerC
                                question.setOptionD(cleanCsvValue(values[6])); // answerD
                                
                                // Richtige Antwort
                                String correctAnswer = cleanCsvValue(values[7]).toUpperCase();
                                question.setCorrectAnswer(correctAnswer);
                                
                                // Difficulty
                                String diff = cleanCsvValue(values[8]).toUpperCase();
                                if (diff.contains("LEICHT") || diff.contains("EASY")) {
                                    question.setDifficulty(1); // Leicht
                                } else if (diff.contains("SCHWER") || diff.contains("HARD")) {
                                    question.setDifficulty(3); // Schwer
                                } else {
                                    question.setDifficulty(2); // Mittel
                                }
                                
                                // Erklärung
                                question.setExplanation(cleanCsvValue(values[9]));
                                
                                // Punkte basierend auf Schwierigkeit
                                question.setPoints(question.getDifficulty() * 10);
                                
                                questions.add(question);
                                importedCount++;
                            }
                        } else {
                            // STANDARD FORMAT (16 Spalten)
                            if (values.length >= 15) {
                                question.setQuestionText(cleanCsvValue(values[5]));
                                question.setCategory(cleanCsvValue(values[2]));
                                
                                String diff = cleanCsvValue(values[11]).toLowerCase();
                                if (diff.contains("leicht")) {
                                    question.setDifficulty(1); // Leicht
                                } else if (diff.contains("schwer")) {
                                    question.setDifficulty(3); // Schwer
                                } else {
                                    question.setDifficulty(2); // Mittel
                                }
                                
                                question.setOptionA(cleanCsvValue(values[6]));
                                question.setOptionB(cleanCsvValue(values[7]));
                                question.setOptionC(cleanCsvValue(values[8]));
                                question.setOptionD(cleanCsvValue(values[9]));
                                
                                String correctAnswer = cleanCsvValue(values[10]).toUpperCase();
                                question.setCorrectAnswer(correctAnswer);
                                
                                try {
                                    question.setPoints(Integer.parseInt(cleanCsvValue(values[12])));
                                } catch (NumberFormatException e) {
                                    question.setPoints(1);
                                }
                                
                                String explanation = cleanCsvValue(values[13]);
                                String hint = values.length > 14 ? cleanCsvValue(values[14]) : "";
                                if (!hint.isEmpty()) {
                                    question.setExplanation(explanation + " (Tipp: " + hint + ")");
                                } else {
                                    question.setExplanation(explanation);
                                }
                                
                                questions.add(question);
                                importedCount++;
                            }
                        }
                        
                        // Batch-Speichern alle 50 Fragen
                        if (questions.size() >= 50) {
                            questionRepository.saveAll(questions);
                            log.info("Batch gespeichert: {} Fragen", questions.size());
                            questions.clear();
                        }
                        
                    } catch (Exception e) {
                        log.warn("Fehler in Zeile {}: {}", lineCount, e.getMessage());
                    }
                }
                
                // Speichere restliche Fragen
                if (!questions.isEmpty()) {
                    questionRepository.saveAll(questions);
                    log.info("Letzter Batch gespeichert: {} Fragen", questions.size());
                }
                
                log.info("✅ Import abgeschlossen: {} von {} Zeilen erfolgreich importiert!", 
                         importedCount, lineCount - 1);
                
                // Zeige Statistik
                long totalQuestions = questionRepository.count();
                log.info("📊 Gesamt-Fragen in Datenbank: {}", totalQuestions);
                
                // Zeige Kategorien-Statistik
                List<String> categories = questionRepository.findAllCategories();
                log.info("📚 Kategorien: {}", categories);
                
            }
            
        } catch (Exception e) {
            log.error("Fehler beim Import der AP1-Fragen: ", e);
        }
    }
    
    /**
     * Besserer CSV-Parser der mit Anführungszeichen umgehen kann
     */
    private String[] parseCSVLine(String line) {
        List<String> result = new ArrayList<>();
        boolean inQuotes = false;
        StringBuilder current = new StringBuilder();
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                // Check for escaped quote
                if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                    current.append('"');
                    i++; // Skip next quote
                } else {
                    inQuotes = !inQuotes;
                }
            } else if (c == ',' && !inQuotes) {
                result.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        
        // Add last field
        result.add(current.toString());
        
        return result.toArray(new String[0]);
    }
    
    private String cleanCsvValue(String value) {
        if (value == null) return "";
        value = value.trim();
        
        // Entferne umschließende Anführungszeichen
        if (value.startsWith("\"") && value.endsWith("\"") && value.length() > 1) {
            value = value.substring(1, value.length() - 1);
        }
        
        // Ersetze doppelte Anführungszeichen durch einfache
        value = value.replace("\"\"", "\"");
        
        return value;
    }
}
