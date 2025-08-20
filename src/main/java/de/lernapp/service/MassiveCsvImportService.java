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
        String csvDirectory = "data/ap1_questions";
        File dir = new File(csvDirectory);
        
        if (!dir.exists() || !dir.isDirectory()) {
            logger.error("Directory not found: {}", csvDirectory);
            return;
        }
        
        // Lösche alle existierenden Fragen
        questionRepository.deleteAll();
        logger.info("Cleared all existing questions from database");
        
        List<Question> allQuestions = new ArrayList<>();
        int totalImported = 0;
        
        try (Stream<Path> paths = Files.walk(Paths.get(csvDirectory))) {
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
            String topic = cleanValue(values.get(0));
            question.setTopic(topic);
            question.setCategory(mapTopicToCategory(topic)); // Map topic to category
            question.setSubtopic(cleanValue(values.get(1)));
            question.setQuestionText(cleanValue(values.get(2)));
            question.setOptionA(cleanValue(values.get(3)));
            question.setOptionB(cleanValue(values.get(4)));
            question.setOptionC(cleanValue(values.get(5)));
            question.setOptionD(cleanValue(values.get(6)));
            question.setCorrectAnswer(cleanValue(values.get(7)));
            
            // Parse difficulty
            String difficultyStr = cleanValue(values.get(8));
            try {
                question.setDifficulty(Integer.parseInt(difficultyStr));
            } catch (NumberFormatException e) {
                question.setDifficulty(1); // Default difficulty
            }
            
            question.setExplanation(cleanValue(values.get(9)));
            question.setTags(cleanValue(values.get(10)));
            
            // Handle optional tips and correction fields
            if (values.size() > 11) {
                question.setTips(cleanValue(values.get(11)));
            }
            if (values.size() > 12) {
                question.setCorrection(cleanValue(values.get(12)));
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
}