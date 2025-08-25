package com.fachinformatiker.lernapp.service.importer;

import com.fachinformatiker.lernapp.model.Question;
import com.fachinformatiker.lernapp.model.Topic;
import com.fachinformatiker.lernapp.repository.QuestionRepository;
import com.fachinformatiker.lernapp.repository.TopicRepository;
import com.fachinformatiker.lernapp.enums.DifficultyLevel;
import com.fachinformatiker.lernapp.enums.QuestionType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for importing questions from CSV files
 * CSV Format: topic,subtopic,question,answerA,answerB,answerC,answerD,correctAnswer,difficulty,explanation,tags
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class QuestionImportService {

    private final QuestionRepository questionRepository;
    private final TopicRepository topicRepository;
    
    @Transactional
    public ImportResult importFromCsv(MultipartFile file) {
        ImportResult result = new ImportResult();
        
        if (file == null || file.isEmpty()) {
            result.setSuccess(false);
            result.addError("Die Datei ist leer oder wurde nicht hochgeladen");
            return result;
        }
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            List<Question> questions = parseQuestions(reader);
            result.setTotalRows(questions.size());
            
            for (Question question : questions) {
                try {
                    // Check for duplicates
                    if (isDuplicate(question)) {
                        result.incrementSkipped();
                        result.addWarning("Frage bereits vorhanden: " + 
                            question.getQuestionText().substring(0, Math.min(50, question.getQuestionText().length())));
                        continue;
                    }
                    
                    // Save question
                    questionRepository.save(question);
                    result.incrementImported();
                    
                } catch (Exception e) {
                    result.incrementFailed();
                    result.addError("Fehler bei Frage: " + e.getMessage());
                    log.error("Error importing question: ", e);
                }
            }
            
            result.setSuccess(result.getImportedCount() > 0);
            log.info("Import completed: {} imported, {} skipped, {} failed", 
                result.getImportedCount(), result.getSkippedCount(), result.getFailedCount());
            
        } catch (Exception e) {
            result.setSuccess(false);
            result.addError("Fehler beim Lesen der CSV-Datei: " + e.getMessage());
            log.error("Error reading CSV file: ", e);
        }
        
        return result;
    }
    
    public ValidationResult validateCsvFormat(MultipartFile file) {
        ValidationResult result = new ValidationResult();
        
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            
            String headerLine = reader.readLine();
            if (headerLine == null) {
                result.addError("Die Datei ist leer");
                return result;
            }
            
            String[] headers = headerLine.split(",");
            String[] expectedHeaders = {
                "topic", "subtopic", "question", "answerA", "answerB", 
                "answerC", "answerD", "correctAnswer", "difficulty", "explanation", "tags"
            };
            
            if (headers.length < expectedHeaders.length - 1) { // tags is optional
                result.addError("CSV-Format ungültig. Erwartete Spalten: " + 
                    String.join(", ", expectedHeaders));
                return result;
            }
            
            int lineNumber = 2;
            String line;
            while ((line = reader.readLine()) != null) {
                String[] values = parseCsvLine(line);
                
                if (values.length < 9) {
                    result.addError("Zeile " + lineNumber + ": Zu wenige Spalten");
                }
                
                // Validate difficulty
                if (values.length > 8) {
                    String difficulty = values[8].trim().toUpperCase();
                    if (!isValidDifficulty(difficulty)) {
                        result.addWarning("Zeile " + lineNumber + 
                            ": Ungültiger Schwierigkeitsgrad '" + difficulty + "'");
                    }
                }
                
                // Validate correct answer
                if (values.length > 7) {
                    String correctAnswer = values[7].trim().toUpperCase();
                    if (!correctAnswer.matches("[A-D]")) {
                        result.addError("Zeile " + lineNumber + 
                            ": Ungültige richtige Antwort '" + correctAnswer + "'");
                    }
                }
                
                lineNumber++;
            }
            
            result.setValid(result.getErrors().isEmpty());
            result.setTotalRows(lineNumber - 2);
            
        } catch (Exception e) {
            result.addError("Fehler beim Validieren: " + e.getMessage());
            result.setValid(false);
        }
        
        return result;
    }
    
    private List<Question> parseQuestions(BufferedReader reader) throws Exception {
        List<Question> questions = new ArrayList<>();
        
        String headerLine = reader.readLine(); // Skip header
        if (headerLine == null) {
            return questions;
        }
        
        String line;
        int lineNumber = 2;
        
        while ((line = reader.readLine()) != null) {
            try {
                String[] values = parseCsvLine(line);
                
                if (values.length < 9) {
                    log.warn("Skipping line {}: insufficient columns", lineNumber);
                    continue;
                }
                
                Question question = createQuestionFromCsvLine(values);
                questions.add(question);
                
            } catch (Exception e) {
                log.error("Error parsing line {}: {}", lineNumber, e.getMessage());
            }
            lineNumber++;
        }
        
        return questions;
    }
    
    private Question createQuestionFromCsvLine(String[] values) {
        Question question = new Question();
        
        // Find or create topic
        String topicName = values[0].trim();
        Topic topic = topicRepository.findByName(topicName)
            .orElseGet(() -> {
                Topic newTopic = new Topic();
                newTopic.setName(topicName);
                newTopic.setDescription("Importiert aus CSV");
                return topicRepository.save(newTopic);
            });
        
        question.setTopic(topic);
        question.setSubtopic(values[1].trim());
        question.setQuestionText(values[2].trim());
        
        // Set answers
        question.setAnswerA(values[3].trim());
        question.setAnswerB(values[4].trim());
        question.setAnswerC(values[5].trim());
        question.setAnswerD(values[6].trim());
        
        // Set correct answer
        question.setCorrectAnswer(values[7].trim().toUpperCase());
        
        // Set difficulty
        String difficultyStr = values[8].trim().toUpperCase();
        question.setDifficultyLevel(parseDifficulty(difficultyStr).getLevel());
        
        // Set explanation (optional)
        if (values.length > 9 && !values[9].trim().isEmpty()) {
            question.setExplanation(values[9].trim());
        }
        
        // Set tags (optional)
        if (values.length > 10 && !values[10].trim().isEmpty()) {
            String[] tags = values[10].split(";");
            // Tags would need to be converted to Tag entities
            // For now, we'll skip tag processing
        }
        
        question.setQuestionType(Question.QuestionType.MULTIPLE_CHOICE);
        question.setActive(true);
        question.setPoints(calculatePoints(DifficultyLevel.fromLevel(question.getDifficultyLevel())));
        
        return question;
    }
    
    private String[] parseCsvLine(String line) {
        List<String> values = new ArrayList<>();
        StringBuilder current = new StringBuilder();
        boolean inQuotes = false;
        
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            
            if (c == '"') {
                inQuotes = !inQuotes;
            } else if (c == ',' && !inQuotes) {
                values.add(current.toString());
                current = new StringBuilder();
            } else {
                current.append(c);
            }
        }
        values.add(current.toString());
        
        return values.toArray(new String[0]);
    }
    
    private boolean isDuplicate(Question question) {
        return questionRepository.findByQuestionTextAndTopic(
            question.getQuestionText(), 
            question.getTopic()
        ).isPresent();
    }
    
    private DifficultyLevel parseDifficulty(String difficulty) {
        try {
            return DifficultyLevel.valueOf(difficulty);
        } catch (IllegalArgumentException e) {
            // Map common variations
            switch (difficulty.toLowerCase()) {
                case "leicht":
                case "einfach":
                case "1":
                    return DifficultyLevel.EASY;
                case "mittel":
                case "2":
                    return DifficultyLevel.MEDIUM;
                case "schwer":
                case "schwierig":
                case "3":
                    return DifficultyLevel.HARD;
                case "experte":
                case "4":
                    return DifficultyLevel.EXPERT;
                default:
                    return DifficultyLevel.MEDIUM;
            }
        }
    }
    
    private boolean isValidDifficulty(String difficulty) {
        try {
            DifficultyLevel.valueOf(difficulty);
            return true;
        } catch (IllegalArgumentException e) {
            String lower = difficulty.toLowerCase();
            return lower.matches("leicht|einfach|mittel|schwer|schwierig|experte|[1-4]");
        }
    }
    
    private int calculatePoints(DifficultyLevel level) {
        switch (level) {
            case EASY: return 10;
            case MEDIUM: return 20;
            case HARD: return 30;
            case EXPERT: return 50;
            default: return 20;
        }
    }
    
    /**
     * Result class for import operations
     */
    public static class ImportResult {
        private boolean success;
        private int totalRows;
        private int importedCount;
        private int skippedCount;
        private int failedCount;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        
        // Getters and setters
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        
        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        
        public int getImportedCount() { return importedCount; }
        public void incrementImported() { this.importedCount++; }
        
        public int getSkippedCount() { return skippedCount; }
        public void incrementSkipped() { this.skippedCount++; }
        
        public int getFailedCount() { return failedCount; }
        public void incrementFailed() { this.failedCount++; }
        
        public List<String> getErrors() { return errors; }
        public void addError(String error) { this.errors.add(error); }
        
        public List<String> getWarnings() { return warnings; }
        public void addWarning(String warning) { this.warnings.add(warning); }
    }
    
    /**
     * Result class for validation
     */
    public static class ValidationResult {
        private boolean valid;
        private int totalRows;
        private List<String> errors = new ArrayList<>();
        private List<String> warnings = new ArrayList<>();
        
        // Getters and setters
        public boolean isValid() { return valid; }
        public void setValid(boolean valid) { this.valid = valid; }
        
        public int getTotalRows() { return totalRows; }
        public void setTotalRows(int totalRows) { this.totalRows = totalRows; }
        
        public List<String> getErrors() { return errors; }
        public void addError(String error) { this.errors.add(error); }
        
        public List<String> getWarnings() { return warnings; }
        public void addWarning(String warning) { this.warnings.add(warning); }
    }
}