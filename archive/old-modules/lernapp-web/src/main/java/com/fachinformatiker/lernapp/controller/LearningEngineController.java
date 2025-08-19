package com.fachinformatiker.lernapp.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import java.util.*;

/**
 * API Controller für Learning Engine - OHNE Authentifizierung für Demo
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class LearningEngineController {

    @PostMapping("/learning-engine/session/start")
    public ResponseEntity<Map<String, Object>> startSession(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", UUID.randomUUID().toString());
        response.put("status", "started");
        response.put("questions", generateQuestions(request));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/learning-engine/session/{sessionId}")
    public ResponseEntity<Map<String, Object>> getSession(@PathVariable String sessionId) {
        Map<String, Object> response = new HashMap<>();
        response.put("sessionId", sessionId);
        response.put("status", "active");
        response.put("progress", 0);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/learning-engine/answer")
    public ResponseEntity<Map<String, Object>> submitAnswer(@RequestBody Map<String, Object> answer) {
        Map<String, Object> response = new HashMap<>();
        response.put("correct", true);
        response.put("explanation", "Gut gemacht!");
        response.put("nextQuestion", generateNextQuestion());
        return ResponseEntity.ok(response);
    }

    @GetMapping("/questions")
    public ResponseEntity<List<Map<String, Object>>> getQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<Map<String, Object>> questions = new ArrayList<>();
        
        // Demo-Fragen generieren
        for (int i = 1; i <= limit; i++) {
            Map<String, Object> question = new HashMap<>();
            question.put("id", i);
            question.put("question", "Demo-Frage " + i + ": Was ist " + i + " + " + i + "?");
            question.put("answers", Arrays.asList(
                String.valueOf(i),
                String.valueOf(i * 2),
                String.valueOf(i * 3),
                String.valueOf(i * 4)
            ));
            question.put("correctAnswer", 1);
            question.put("category", category != null ? category : "Mathematik");
            question.put("difficulty", "EASY");
            questions.add(question);
        }
        
        return ResponseEntity.ok(questions);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Map<String, Object>>> getCategories() {
        List<Map<String, Object>> categories = new ArrayList<>();
        
        String[] cats = {"IT-Systeme", "Vernetzte Systeme", "Datenbanken", 
                        "Programmierung", "Geschäftsprozesse", "Datenschutz"};
        
        for (int i = 0; i < cats.length; i++) {
            Map<String, Object> category = new HashMap<>();
            category.put("id", i + 1);
            category.put("name", cats[i]);
            category.put("questionCount", (i + 1) * 100);
            category.put("icon", "📚");
            categories.add(category);
        }
        
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/topics")
    public ResponseEntity<List<Map<String, Object>>> getTopics() {
        List<Map<String, Object>> topics = new ArrayList<>();
        
        Map<String, Object> topic1 = new HashMap<>();
        topic1.put("id", 1);
        topic1.put("name", "Java Grundlagen");
        topic1.put("category", "Programmierung");
        topic1.put("progress", 45);
        topics.add(topic1);
        
        Map<String, Object> topic2 = new HashMap<>();
        topic2.put("id", 2);
        topic2.put("name", "Netzwerk-Protokolle");
        topic2.put("category", "Vernetzte Systeme");
        topic2.put("progress", 67);
        topics.add(topic2);
        
        return ResponseEntity.ok(topics);
    }

    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalQuestions", 600);
        stats.put("answeredQuestions", 145);
        stats.put("correctAnswers", 102);
        stats.put("streak", 5);
        stats.put("level", 7);
        stats.put("experience", 1250);
        return ResponseEntity.ok(stats);
    }

    private List<Map<String, Object>> generateQuestions(Map<String, Object> request) {
        List<Map<String, Object>> questions = new ArrayList<>();
        
        Map<String, Object> q1 = new HashMap<>();
        q1.put("id", 1);
        q1.put("text", "Was ist der Unterschied zwischen TCP und UDP?");
        q1.put("options", Arrays.asList(
            "TCP ist verbindungsorientiert, UDP verbindungslos",
            "UDP ist schneller, TCP sicherer",
            "Beide sind gleich",
            "TCP für Video, UDP für Webseiten"
        ));
        q1.put("correctIndex", 0);
        questions.add(q1);
        
        return questions;
    }

    private Map<String, Object> generateNextQuestion() {
        Map<String, Object> question = new HashMap<>();
        question.put("id", new Random().nextInt(1000));
        question.put("text", "Nächste Frage: Was ist RAID 5?");
        question.put("options", Arrays.asList(
            "Striping ohne Redundanz",
            "Spiegelung",
            "Striping mit Parität",
            "Triple Mirroring"
        ));
        question.put("correctIndex", 2);
        return question;
    }
}

// ViewController entfernt - Konflikt mit WebController behoben
// Alle View-Mappings sind jetzt in WebController.java