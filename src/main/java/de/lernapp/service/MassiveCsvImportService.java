package de.lernapp.service;

import de.lernapp.model.Question;
import de.lernapp.repository.QuestionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class MassiveCsvImportService {
    
    private static final Logger logger = LoggerFactory.getLogger(MassiveCsvImportService.class);
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @Transactional
    public void importAllCsvFiles() {
        // Versuche mehrere mögliche Verzeichnisse
        String[] possibleDirs = {
            "data/ap1_questions",
            "data",
            "./data/ap1_questions",
            "./data"
        };
        
        File dir = null;
        for (String path : possibleDirs) {
            File testDir = new File(path);
            if (testDir.exists() && testDir.isDirectory()) {
                dir = testDir;
                logger.info("Found data directory at: {}", path);
                break;
            }
        }
        
        if (dir == null) {
            logger.error("No data directory found!");
            return;
        }
        
        // Lösche alle existierenden Fragen
        questionRepository.deleteAll();
        logger.info("Cleared all existing questions from database");
        
        List<Question> allQuestions = new ArrayList<>();
        int totalImported = 0;
        
        try (Stream<Path> paths = Files.walk(dir.toPath())) {
            List<File> csvFiles = paths
                .filter(Files::isRegularFile)
                .filter(p -> p.toString().endsWith(".csv"))
                .map(Path::toFile)
                .collect(Collectors.toList());
            
            logger.info("Found {} CSV files to import", csvFiles.size());
            
            for (File csvFile : csvFiles) {
                logger.info("Importing file: {}", csvFile.getName());
                List<Question> questions = importCsvFile(csvFile);
                allQuestions.addAll(questions);
                logger.info("Imported {} questions from {}", questions.size(), csvFile.getName());
            }
            
            // Batch save all questions
            if (!allQuestions.isEmpty()) {
                questionRepository.saveAll(allQuestions);
                totalImported = allQuestions.size();
                logger.info("Successfully imported {} questions total", totalImported);
            }
            
        } catch (IOException e) {
            logger.error("Error reading CSV files", e);
        }
    }
    
    private List<Question> importCsvFile(File csvFile) {
        List<Question> questions = new ArrayList<>();
        
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            boolean isFirstLine = true;
            
            while ((line = br.readLine()) != null) {
                // Skip header
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                
                try {
                    Question question = parseCsvLine(line);
                    if (question != null) {
                        questions.add(question);
                    }
                } catch (Exception e) {
                    logger.warn("Error parsing line in {}: {}", csvFile.getName(), e.getMessage());
                }
            }
        } catch (IOException e) {
            logger.error("Error reading file: {}", csvFile.getName(), e);
        }
        
        return questions;
    }
    
    private Question parseCsvLine(String line) {
        // Parse CSV line considering quoted values
        List<String> values = parseCsvValues(line);
        
        if (values.size() < 11) {
            logger.warn("Insufficient values in line: {}", line);
            return null;
        }
        
        Question question = new Question();
        
        try {
            // Neue CSV-Struktur: exam_year,exam_season,main_category,sub_category,topic_tags,question_text,answer_a,answer_b,answer_c,answer_d,correct_answer,difficulty,points,explanation,hint,frequency
            // Index mapping anpassen
            int idx = 0;
            if (values.size() >= 16) {
                // Neue CSV-Struktur mit allen Feldern
                idx = 0; // exam_year
                idx++; // exam_season
                String mainCategory = cleanValue(values.get(idx++)); // main_category
                String subCategory = cleanValue(values.get(idx++)); // sub_category
                String topicTags = cleanValue(values.get(idx++)); // topic_tags
                
                question.setCategory(normalizeCategory(mainCategory));
                question.setTopic(subCategory);
                question.setTags(topicTags);
                
                question.setQuestionText(cleanValue(values.get(idx++))); // question_text
                question.setOptionA(cleanValue(values.get(idx++))); // answer_a
                question.setOptionB(cleanValue(values.get(idx++))); // answer_b
                question.setOptionC(cleanValue(values.get(idx++))); // answer_c
                question.setOptionD(cleanValue(values.get(idx++))); // answer_d
                question.setCorrectAnswer(cleanValue(values.get(idx++))); // correct_answer
                
                // Parse difficulty
                String difficultyStr = cleanValue(values.get(idx++)); // difficulty
                question.setDifficulty(parseDifficulty(difficultyStr));
                
                // Parse points
                String pointsStr = cleanValue(values.get(idx++)); // points
                try {
                    question.setPoints(Integer.parseInt(pointsStr));
                } catch (NumberFormatException e) {
                    question.setPoints(10); // Default points
                }
                
                question.setExplanation(cleanValue(values.get(idx++))); // explanation
                if (idx < values.size()) {
                    question.setTips(cleanValue(values.get(idx++))); // hint
                }
            } else {
                // Alte CSV-Struktur (Fallback)
                String topic = cleanValue(values.get(0));
                question.setTopic(topic);
                question.setCategory(mapTopicToCategory(topic));
                question.setSubtopic(cleanValue(values.get(1)));
                question.setQuestionText(cleanValue(values.get(2)));
                question.setOptionA(cleanValue(values.get(3)));
                question.setOptionB(cleanValue(values.get(4)));
                question.setOptionC(cleanValue(values.get(5)));
                question.setOptionD(cleanValue(values.get(6)));
                question.setCorrectAnswer(cleanValue(values.get(7)));
                
                String difficultyStr = cleanValue(values.get(8));
                question.setDifficulty(parseDifficulty(difficultyStr));
                
                question.setExplanation(cleanValue(values.get(9)));
                question.setTags(cleanValue(values.get(10)));
                
                if (values.size() > 11) {
                    question.setTips(cleanValue(values.get(11)));
                }
                if (values.size() > 12) {
                    question.setCorrection(cleanValue(values.get(12)));
                }
            }
            
            return question;
            
        } catch (Exception e) {
            logger.error("Error creating question from line: {}", e.getMessage());
            return null;
        }
    }
    
    private List<String> parseCsvValues(String line) {
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
        return values;
    }
    
    private String cleanValue(String value) {
        if (value == null) return "";
        
        // Remove surrounding quotes if present
        value = value.trim();
        if (value.startsWith("\"") && value.endsWith("\"")) {
            value = value.substring(1, value.length() - 1);
        }
        
        // Replace escaped quotes
        value = value.replace("\"\"", "\"");
        
        return value.trim();
    }
    
    private String mapTopicToCategory(String topic) {
        if (topic == null) return "Allgemein";
        
        String topicLower = topic.toLowerCase();
        
        if (topicLower.contains("geschäft") || topicLower.contains("wirtschaft") || 
            topicLower.contains("projekt") || topicLower.contains("recht") ||
            topicLower.contains("marketing") || topicLower.contains("kalkulation")) {
            return "Geschäftsprozesse";
        } else if (topicLower.contains("netzwerk") || topicLower.contains("tcp") || 
                   topicLower.contains("ip") || topicLower.contains("lan") ||
                   topicLower.contains("router") || topicLower.contains("switch")) {
            return "Vernetzte Systeme";
        } else if (topicLower.contains("programmier") || topicLower.contains("datenbank") ||
                   topicLower.contains("sql") || topicLower.contains("software") ||
                   topicLower.contains("entwicklung") || topicLower.contains("code")) {
            return "Anwendungsentwicklung";
        } else if (topicLower.contains("hardware") || topicLower.contains("cpu") ||
                   topicLower.contains("ram") || topicLower.contains("speicher") ||
                   topicLower.contains("drucker")) {
            return "IT-Systeme";
        } else if (topicLower.contains("sicher") || topicLower.contains("datenschutz") ||
                   topicLower.contains("verschlüssel") || topicLower.contains("firewall")) {
            return "IT-Sicherheit";
        } else {
            return "Allgemeinwissen";
        }
    }
    
    private String normalizeCategory(String category) {
        if (category == null) return "Allgemein";
        
        // Normalisiere Kategorien aus CSV
        switch (category.toUpperCase()) {
            case "BETRIEBLICHE_ORGANISATION":
            case "WIRTSCHAFT":
                return "Geschäftsprozesse";
            case "NETZWERKTECHNIK":
            case "VERNETZTE_SYSTEME":
                return "Vernetzte Systeme";
            case "PROGRAMMIERUNG":
            case "DATENBANKEN":
            case "SOFTWARE":
                return "Anwendungsentwicklung";
            case "IT_SYSTEME":
            case "HARDWARE":
                return "IT-Systeme";
            case "SICHERHEIT":
            case "IT_SICHERHEIT":
                return "IT-Sicherheit";
            default:
                return mapTopicToCategory(category);
        }
    }
    
    private Integer parseDifficulty(String difficultyStr) {
        if (difficultyStr == null) return 1;
        
        difficultyStr = difficultyStr.toLowerCase().trim();
        
        // Versuche zuerst als Zahl zu parsen
        try {
            return Integer.parseInt(difficultyStr);
        } catch (NumberFormatException e) {
            // Parse text-basierte Schwierigkeit
            switch (difficultyStr) {
                case "leicht":
                case "einfach":
                case "easy":
                    return 1;
                case "mittel":
                case "medium":
                    return 2;
                case "schwer":
                case "schwierig":
                case "hard":
                    return 3;
                default:
                    return 1; // Default
            }
        }
    }
}