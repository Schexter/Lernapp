package de.lernapp.service;

import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import de.lernapp.model.Question;
import de.lernapp.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Service für den Import von Prüfungsfragen aus CSV-Dateien
 * Unterstützt Batch-Import und Duplikat-Erkennung
 * 
 * @author Hans Hahn
 */
@Service
@Transactional
public class CsvImportService {
    
    private static final Logger logger = LoggerFactory.getLogger(CsvImportService.class);
    
    @Autowired
    private QuestionRepository questionRepository;
    
    // Für Duplikat-Erkennung
    private Set<String> existingQuestionTexts = new HashSet<>();
    
    /**
     * Import-Statistiken
     */
    public static class ImportResult {
        private int totalProcessed = 0;
        private int successfulImports = 0;
        private int duplicatesSkipped = 0;
        private int errorsOccurred = 0;
        private List<String> errorMessages = new ArrayList<>();
        
        // Getters und Setters
        public int getTotalProcessed() { return totalProcessed; }
        public void setTotalProcessed(int totalProcessed) { this.totalProcessed = totalProcessed; }
        
        public int getSuccessfulImports() { return successfulImports; }
        public void setSuccessfulImports(int successfulImports) { this.successfulImports = successfulImports; }
        
        public int getDuplicatesSkipped() { return duplicatesSkipped; }
        public void setDuplicatesSkipped(int duplicatesSkipped) { this.duplicatesSkipped = duplicatesSkipped; }
        
        public int getErrorsOccurred() { return errorsOccurred; }
        public void setErrorsOccurred(int errorsOccurred) { this.errorsOccurred = errorsOccurred; }
        
        public List<String> getErrorMessages() { return errorMessages; }
        public void setErrorMessages(List<String> errorMessages) { this.errorMessages = errorMessages; }
        
        public void addErrorMessage(String message) { this.errorMessages.add(message); }
    }
    
    /**
     * Importiert Fragen aus einer hochgeladenen CSV-Datei
     */
    public ImportResult importFromUploadedFile(MultipartFile file) throws IOException {
        logger.info("Starte Import von Datei: {}", file.getOriginalFilename());
        
        try (Reader reader = new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)) {
            return processCSV(reader, file.getOriginalFilename());
        }
    }
    
    /**
     * Importiert Fragen aus einer lokalen CSV-Datei
     */
    public ImportResult importFromLocalFile(String filePath) throws IOException {
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            throw new FileNotFoundException("Datei nicht gefunden: " + filePath);
        }
        
        logger.info("Starte Import von lokaler Datei: {}", filePath);
        
        try (Reader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return processCSV(reader, path.getFileName().toString());
        }
    }
    
    /**
     * Importiert alle CSV-Dateien aus dem data/ap1_questions Verzeichnis
     */
    public ImportResult importAllAP1Questions() {
        ImportResult totalResult = new ImportResult();
        String baseDir = "C:/SoftwareEntwicklung/Fachinformatiker_Lernapp_Java/data/ap1_questions/";
        
        String[] csvFiles = {
            "ap1_geschaeftsprozesse.csv",
            "ap1_it_systeme.csv",  
            "ap1_vernetzte_systeme.csv",
            "ap1_datenbanken.csv",
            "ap1_datenschutz_sicherheit.csv",
            "ap1_wirtschaft_sozialkunde.csv"
        };
        
        // Lade existierende Fragen für Duplikat-Check
        loadExistingQuestions();
        
        for (String csvFile : csvFiles) {
            try {
                String fullPath = baseDir + csvFile;
                logger.info("Importiere: {}", csvFile);
                
                ImportResult result = importFromLocalFile(fullPath);
                
                // Addiere Statistiken
                totalResult.totalProcessed += result.totalProcessed;
                totalResult.successfulImports += result.successfulImports;
                totalResult.duplicatesSkipped += result.duplicatesSkipped;
                totalResult.errorsOccurred += result.errorsOccurred;
                totalResult.errorMessages.addAll(result.errorMessages);
                
                logger.info("✅ {} importiert: {} Fragen erfolgreich", 
                    csvFile, result.successfulImports);
                    
            } catch (Exception e) {
                String errorMsg = "Fehler beim Import von " + csvFile + ": " + e.getMessage();
                logger.error(errorMsg, e);
                totalResult.addErrorMessage(errorMsg);
                totalResult.errorsOccurred++;
            }
        }
        
        logger.info("🎯 GESAMT-IMPORT ABGESCHLOSSEN:");
        logger.info("   - Verarbeitet: {} Fragen", totalResult.totalProcessed);
        logger.info("   - Erfolgreich: {} Fragen", totalResult.successfulImports);
        logger.info("   - Duplikate übersprungen: {}", totalResult.duplicatesSkipped);
        logger.info("   - Fehler: {}", totalResult.errorsOccurred);
        
        return totalResult;
    }
    
    /**
     * Verarbeitet die CSV-Datei und importiert die Fragen
     */
    private ImportResult processCSV(Reader reader, String fileName) {
        ImportResult result = new ImportResult();
        
        try (CSVReader csvReader = new CSVReader(reader)) {
            List<String[]> records = csvReader.readAll();
            
            // Skip header row
            boolean isFirstRow = true;
            int batchSize = 50;
            List<Question> batch = new ArrayList<>();
            
            for (String[] record : records) {
                if (isFirstRow) {
                    isFirstRow = false;
                    continue; // Skip header
                }
                
                result.totalProcessed++;
                
                try {
                    Question question = parseQuestionFromCSV(record);
                    
                    // Duplikat-Check
                    if (isDuplicate(question)) {
                        result.duplicatesSkipped++;
                        logger.debug("Duplikat übersprungen: {}", 
                            question.getQuestionText().substring(0, Math.min(50, question.getQuestionText().length())));
                        continue;
                    }
                    
                    batch.add(question);
                    existingQuestionTexts.add(question.getQuestionText());
                    
                    // Batch-Speicherung für Performance
                    if (batch.size() >= batchSize) {
                        questionRepository.saveAll(batch);
                        result.successfulImports += batch.size();
                        batch.clear();
                    }
                    
                } catch (Exception e) {
                    result.errorsOccurred++;
                    String errorMsg = String.format("Zeile %d: %s", result.totalProcessed, e.getMessage());
                    result.addErrorMessage(errorMsg);
                    logger.error("Fehler beim Parsen der Zeile {}: {}", result.totalProcessed, e.getMessage());
                }
            }
            
            // Speichere verbleibende Fragen
            if (!batch.isEmpty()) {
                questionRepository.saveAll(batch);
                result.successfulImports += batch.size();
            }
            
        } catch (IOException | CsvException e) {
            String errorMsg = "CSV-Verarbeitungsfehler: " + e.getMessage();
            result.addErrorMessage(errorMsg);
            logger.error(errorMsg, e);
        }
        
        return result;
    }
    
    /**
     * Parsed eine CSV-Zeile zu einem Question-Objekt
     * Erwartetes Format: Frage, Antwort_A, Antwort_B, Antwort_C, Antwort_D, 
     *                    Korrekte_Antwort, Kategorie, Schwierigkeit, Erklärung, Quelle
     */
    private Question parseQuestionFromCSV(String[] record) throws IllegalArgumentException {
        if (record.length < 10) {
            throw new IllegalArgumentException("CSV-Zeile hat zu wenige Spalten: " + record.length);
        }
        
        Question question = new Question();
        
        // Basis-Felder
        question.setQuestionText(cleanCSVField(record[0]));
        question.setOptionA(cleanCSVField(record[1]));
        question.setOptionB(cleanCSVField(record[2]));
        question.setOptionC(cleanCSVField(record[3]));
        question.setOptionD(cleanCSVField(record[4]));
        
        // Korrekte Antwort (A, B, C oder D)
        String correctAnswer = cleanCSVField(record[5]).toUpperCase();
        if (!correctAnswer.matches("[A-D]")) {
            throw new IllegalArgumentException("Ungültige korrekte Antwort: " + correctAnswer);
        }
        question.setCorrectAnswer(correctAnswer);
        
        // Kategorie
        question.setCategory(cleanCSVField(record[6]));
        
        // Schwierigkeitsgrad
        String difficulty = cleanCSVField(record[7]);
        question.setDifficulty(parseDifficulty(difficulty));
        
        // Erklärung
        question.setExplanation(cleanCSVField(record[8]));
        
        // Quelle (optional)
        if (record.length > 9 && record[9] != null) {
            question.setSource(cleanCSVField(record[9]));
        }
        
        // Standard-Werte
        question.setActive(true);
        question.setPoints(calculatePoints(question.getDifficulty()));
        
        return question;
    }
    
    /**
     * Bereinigt CSV-Felder (entfernt Anführungszeichen, Whitespace)
     */
    private String cleanCSVField(String field) {
        if (field == null) return "";
        
        // Entferne umgebende Anführungszeichen
        field = field.trim();
        if (field.startsWith("\"") && field.endsWith("\"")) {
            field = field.substring(1, field.length() - 1);
        }
        
        // Ersetze doppelte Anführungszeichen durch einfache
        field = field.replace("\"\"", "\"");
        
        return field.trim();
    }
    
    /**
     * Konvertiert Schwierigkeitsgrad-String zu Integer
     */
    private Integer parseDifficulty(String difficulty) {
        switch (difficulty.toLowerCase()) {
            case "leicht":
            case "einfach":
            case "easy":
            case "1":
                return 1;
            case "mittel":
            case "medium":
            case "2":
                return 2;
            case "schwer":
            case "hard":
            case "schwierig":
            case "3":
                return 3;
            default:
                logger.warn("Unbekannter Schwierigkeitsgrad: {}, verwende 'mittel'", difficulty);
                return 2;
        }
    }
    
    /**
     * Berechnet Punkte basierend auf Schwierigkeitsgrad
     */
    private Integer calculatePoints(Integer difficulty) {
        switch (difficulty) {
            case 1: return 10;
            case 2: return 20;
            case 3: return 30;
            default: return 20;
        }
    }
    
    /**
     * Lädt existierende Fragen für Duplikat-Erkennung
     */
    private void loadExistingQuestions() {
        existingQuestionTexts.clear();
        List<Question> existingQuestions = questionRepository.findAll();
        for (Question q : existingQuestions) {
            existingQuestionTexts.add(q.getQuestionText());
        }
        logger.info("Loaded {} existing questions for duplicate check", existingQuestionTexts.size());
    }
    
    /**
     * Prüft ob eine Frage bereits existiert
     */
    private boolean isDuplicate(Question question) {
        return existingQuestionTexts.contains(question.getQuestionText());
    }
    
    /**
     * Löscht alle importierten Fragen (für Tests/Reset)
     */
    @Transactional
    public void deleteAllQuestions() {
        logger.warn("⚠️ Lösche alle Fragen aus der Datenbank!");
        questionRepository.deleteAll();
        existingQuestionTexts.clear();
    }
    
    /**
     * Gibt Statistiken über die importierten Fragen zurück
     */
    public String getImportStatistics() {
        long totalQuestions = questionRepository.count();
        
        StringBuilder stats = new StringBuilder();
        stats.append("📊 IMPORT-STATISTIKEN:\n");
        stats.append("========================\n");
        stats.append("Gesamt-Fragen: ").append(totalQuestions).append("\n\n");
        
        // Kategorien-Statistik
        stats.append("Nach Kategorie:\n");
        List<Object[]> categoryStats = questionRepository.countByCategory();
        for (Object[] stat : categoryStats) {
            stats.append("  - ").append(stat[0]).append(": ").append(stat[1]).append(" Fragen\n");
        }
        
        stats.append("\nNach Schwierigkeit:\n");
        stats.append("  - Leicht: ").append(questionRepository.countByDifficulty(1)).append("\n");
        stats.append("  - Mittel: ").append(questionRepository.countByDifficulty(2)).append("\n");
        stats.append("  - Schwer: ").append(questionRepository.countByDifficulty(3)).append("\n");
        
        return stats.toString();
    }
}

// Erstellt von Hans Hahn - Alle Rechte vorbehalten