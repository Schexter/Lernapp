package de.lernapp.controller.api;

import de.lernapp.model.Question;
import de.lernapp.service.QuestionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST API Controller für Questions
 * 
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@RestController
@RequestMapping("/api/questions")
@RequiredArgsConstructor
@CrossOrigin(origins = "*") // Für Entwicklung - später einschränken!
public class QuestionController {
    
    private final QuestionService questionService;
    
    /**
     * GET /api/questions - Hole alle Fragen
     */
    @GetMapping
    public ResponseEntity<List<Question>> getAllQuestions() {
        List<Question> questions = questionService.getAllQuestions();
        return ResponseEntity.ok(questions);
    }
    
    /**
     * GET /api/questions/{id} - Hole eine spezifische Frage
     */
    @GetMapping("/{id}")
    public ResponseEntity<Question> getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * POST /api/questions - Erstelle eine neue Frage
     */
    @PostMapping
    public ResponseEntity<Question> createQuestion(@RequestBody Question question) {
        Question savedQuestion = questionService.saveQuestion(question);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedQuestion);
    }
    
    /**
     * PUT /api/questions/{id} - Aktualisiere eine Frage
     */
    @PutMapping("/{id}")
    public ResponseEntity<Question> updateQuestion(@PathVariable Long id, @RequestBody Question question) {
        try {
            Question updatedQuestion = questionService.updateQuestion(id, question);
            return ResponseEntity.ok(updatedQuestion);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * DELETE /api/questions/{id} - Lösche eine Frage
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * GET /api/questions/category/{category} - Hole Fragen nach Kategorie
     */
    @GetMapping("/category/{category}")
    public ResponseEntity<List<Question>> getQuestionsByCategory(@PathVariable String category) {
        List<Question> questions = questionService.getQuestionsByCategory(category);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * GET /api/questions/difficulty/{difficulty} - Hole Fragen nach Schwierigkeitsgrad
     */
    @GetMapping("/difficulty/{difficulty}")
    public ResponseEntity<List<Question>> getQuestionsByDifficulty(@PathVariable Integer difficulty) {
        List<Question> questions = questionService.getQuestionsByDifficulty(difficulty);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * GET /api/questions/random?count=10 - Hole zufällige Fragen
     */
    @GetMapping("/random")
    public ResponseEntity<List<Question>> getRandomQuestions(@RequestParam(defaultValue = "10") int count) {
        List<Question> questions = questionService.getRandomQuestions(count);
        return ResponseEntity.ok(questions);
    }
    
    /**
     * GET /api/questions/categories - Hole alle verfügbaren Kategorien
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = questionService.getAllCategories();
        return ResponseEntity.ok(categories);
    }
    
    /**
     * POST /api/questions/{id}/check - Prüfe eine Antwort
     */
    @PostMapping("/{id}/check")
    public ResponseEntity<Map<String, Object>> checkAnswer(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        
        String answer = request.get("answer");
        boolean isCorrect = questionService.checkAnswer(id, answer);
        
        return ResponseEntity.ok(Map.of(
            "correct", isCorrect,
            "questionId", id,
            "answer", answer
        ));
    }
}
