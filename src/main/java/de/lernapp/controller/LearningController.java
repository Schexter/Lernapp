package de.lernapp.controller;

import de.lernapp.dto.AnswerRequest;
import de.lernapp.dto.AnswerResponse;
import de.lernapp.dto.QuestionDTO;
import de.lernapp.model.Question;
import de.lernapp.model.User;
import de.lernapp.service.QuestionService;
import de.lernapp.service.UserService;
import de.lernapp.service.ProgressService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Learning Controller - DEVELOPMENT VERSION (CORS deaktiviert)
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@RestController
@RequestMapping("/api/learning")
@RequiredArgsConstructor
@Slf4j
public class LearningController {

    private final QuestionService questionService;
    private final UserService userService;
    private final ProgressService progressService;

    /**
     * CORS Headers manuell setzen für alle Requests
     */
    private void setCorsHeaders(HttpServletResponse response) {
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
        response.setHeader("Access-Control-Allow-Credentials", "true");
    }

    /**
     * OPTIONS Handler für Preflight Requests
     */
    @RequestMapping(method = RequestMethod.OPTIONS, value = "/**")
    public ResponseEntity<Void> handleOptions(HttpServletResponse response) {
        setCorsHeaders(response);
        return ResponseEntity.ok().build();
    }

    /**
     * Get next question for learning mode
     */
    @GetMapping("/next-question")
    public ResponseEntity<QuestionDTO> getNextQuestion(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer difficulty,
            Authentication auth,
            HttpServletResponse response) {
        
        setCorsHeaders(response);
        log.info("🎯 Getting next question - category: {}, difficulty: {}", category, difficulty);
        
        try {
            // Für Development: Demo-Daten verwenden
            Question question = questionService.getRandomQuestion();
            
            if (question == null) {
                log.warn("❌ No question found");
                return ResponseEntity.noContent().build();
            }
            
            log.info("✅ Found question: {}", question.getQuestionText());
            return ResponseEntity.ok(QuestionDTO.fromQuestion(question));
            
        } catch (Exception e) {
            log.error("💥 Error getting question", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get available categories - KOMPLETT ÖFFENTLICH
     */
    @GetMapping("/categories")
    public ResponseEntity<List<String>> getCategories(HttpServletResponse response) {
        setCorsHeaders(response);
        log.info("🎯 Getting all categories");
        
        try {
            List<String> categories = questionService.getAllCategories();
            log.info("✅ Found {} categories", categories.size());
            
            // Fallback wenn keine Kategorien
            if (categories.isEmpty()) {
                categories = List.of("Netzplantechnik", "PowerShell", "Datenschutz", "IPv6");
                log.info("📝 Using fallback categories");
            }
            
            return ResponseEntity.ok(categories);
            
        } catch (Exception e) {
            log.error("💥 Error getting categories", e);
            // Fallback Antwort
            List<String> fallbackCategories = List.of("Allgemein", "Test", "Demo");
            return ResponseEntity.ok(fallbackCategories);
        }
    }

    /**
     * Submit answer
     */
    @PostMapping("/answer")
    public ResponseEntity<AnswerResponse> submitAnswer(
            @RequestBody AnswerRequest request,
            Authentication auth,
            HttpServletResponse response) {
        
        setCorsHeaders(response);
        log.info("🎯 Submitting answer for question: {}", request.getQuestionId());
        
        try {
            Question question = questionService.findById(request.getQuestionId())
                    .orElseThrow(() -> new RuntimeException("Question not found"));
            
            boolean isCorrect = question.getCorrectAnswer().equals(request.getAnswer());
            
            // Update user statistics if authenticated
            if (auth != null && auth.isAuthenticated()) {
                String username = auth.getName();
                Optional<User> userOpt = userService.findByUsername(username);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    
                    // Update statistics
                    user.setTotalQuestionsAnswered(user.getTotalQuestionsAnswered() + 1);
                    if (isCorrect) {
                        user.setCorrectAnswers(user.getCorrectAnswers() + 1);
                        user.setCurrentStreak(user.getCurrentStreak() + 1);
                        if (user.getCurrentStreak() > user.getBestStreak()) {
                            user.setBestStreak(user.getCurrentStreak());
                        }
                        // Add XP for correct answer
                        int xpGained = question.getPoints() != null ? question.getPoints() : 10;
                        user.setExperiencePoints(user.getExperiencePoints() + xpGained);
                        // Update level (every 100 XP = 1 level)
                        user.setLevel(1 + (user.getExperiencePoints() / 100));
                    } else {
                        user.setCurrentStreak(0); // Reset streak on wrong answer
                    }
                    
                    // Save updated user
                    userService.updateUser(user);
                    log.info("✅ Updated statistics for user: {}", username);
                }
            }
            
            AnswerResponse answerResponse = AnswerResponse.builder()
                    .correct(isCorrect)
                    .correctAnswer(question.getCorrectAnswer())
                    .explanation(question.getExplanation())
                    .pointsEarned(isCorrect ? question.getPoints() : 0)
                    .build();
            
            return ResponseEntity.ok(answerResponse);
            
        } catch (Exception e) {
            log.error("💥 Error submitting answer", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get questions by category
     */
    @GetMapping("/questions")
    public ResponseEntity<List<QuestionDTO>> getQuestions(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(defaultValue = "10") int limit,
            HttpServletResponse response) {
        
        setCorsHeaders(response);
        log.info("🎯 Getting questions: category={}, difficulty={}, limit={}", category, difficulty, limit);
        
        try {
            List<Question> questions = questionService.getQuestions(category, difficulty, limit);
            List<QuestionDTO> questionDTOs = questions.stream()
                    .map(QuestionDTO::fromQuestion)
                    .toList();
            
            return ResponseEntity.ok(questionDTOs);
            
        } catch (Exception e) {
            log.error("💥 Error getting questions", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Health Check
     */
    @GetMapping("/health")
    public ResponseEntity<String> health(HttpServletResponse response) {
        setCorsHeaders(response);
        log.info("🎯 Health check requested");
        return ResponseEntity.ok("✅ Learning API is running!");
    }

    /**
     * Test Endpoint
     */
    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> test(HttpServletResponse response) {
        setCorsHeaders(response);
        log.info("🎯 Test endpoint called");
        
        Map<String, Object> testData = Map.of(
            "status", "OK",
            "message", "Learning API is working",
            "timestamp", System.currentTimeMillis(),
            "cors", "enabled"
        );
        
        return ResponseEntity.ok(testData);
    }
}