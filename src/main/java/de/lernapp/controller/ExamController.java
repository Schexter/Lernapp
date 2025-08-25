package de.lernapp.controller;

import de.lernapp.model.Question;
import de.lernapp.model.User;
import de.lernapp.service.QuestionService;
import de.lernapp.service.UserService;
import de.lernapp.service.StrategyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "*")
public class ExamController {

    @Autowired
    private QuestionService questionService;

    @Autowired
    private UserService userService;

    @Autowired
    private StrategyService strategyService;

    @GetMapping("/questions")
    public ResponseEntity<List<Question>> getExamQuestions(
            @RequestParam(defaultValue = "40") int count,
            @RequestParam(required = false) String categories,
            Authentication authentication) {
        
        try {
            List<Question> questions;
            
            // Prüfe ob 60%-Strategie aktiv ist
            if (authentication != null) {
                String username = authentication.getName();
                Optional<User> userOpt = userService.findByUsername(username);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    // Nutze die Strategie-Methode mit User-Parameter
                    questions = strategyService.getStrategyQuestions(user, count);
                } else {
                    // Normale zufällige Fragen
                    questions = questionService.getRandomQuestions(count);
                }
            } else {
                // Nicht eingeloggt - normale zufällige Fragen
                questions = questionService.getRandomQuestions(count);
            }
            
            // Filter nach Kategorien wenn angegeben
            if (categories != null && !categories.isEmpty()) {
                List<String> categoryList = Arrays.asList(categories.split(","));
                questions = questions.stream()
                    .filter(q -> categoryList.contains(q.getCategory()))
                    .limit(count)
                    .collect(Collectors.toList());
                
                // Falls nicht genug Fragen in den Kategorien, fülle auf
                if (questions.size() < count) {
                    List<Question> additionalQuestions = questionService.getRandomQuestions(count - questions.size());
                    questions.addAll(additionalQuestions);
                }
            }
            
            return ResponseEntity.ok(questions);
        } catch (Exception e) {
            System.err.println("Error loading exam questions: " + e.getMessage());
            // Fallback: Lade normale Fragen
            return ResponseEntity.ok(questionService.getRandomQuestions(count));
        }
    }

    @PostMapping("/submit")
    public ResponseEntity<Map<String, Object>> submitExam(
            @RequestBody ExamSubmission submission,
            Authentication authentication) {
        
        Map<String, Object> response = new HashMap<>();
        
        try {
            // Update user statistics if authenticated
            if (authentication != null) {
                String username = authentication.getName();
                Optional<User> userOpt = userService.findByUsername(username);
                
                if (userOpt.isPresent()) {
                    User user = userOpt.get();
                    // Berechne korrekte Antworten
                    int correctCount = 0;
                    for (ExamAnswer answer : submission.getAnswers()) {
                        if (answer.isCorrect()) {
                            correctCount++;
                        }
                    }
                    
                    // Update Statistiken
                    user.setTotalQuestionsAnswered(user.getTotalQuestionsAnswered() + submission.getAnswers().size());
                    user.setCorrectAnswers(user.getCorrectAnswers() + correctCount);
                    
                    // XP berechnen (10 XP pro richtige Antwort + Bonus für hohe Punktzahl)
                    int xpGained = correctCount * 10;
                    if ((correctCount * 100 / submission.getAnswers().size()) >= 80) {
                        xpGained += 50; // Bonus für >= 80%
                    }
                    if ((correctCount * 100 / submission.getAnswers().size()) >= 90) {
                        xpGained += 50; // Zusätzlicher Bonus für >= 90%
                    }
                    
                    user.setExperiencePoints(user.getExperiencePoints() + xpGained);
                    
                    // Level berechnen (alle 100 XP ein Level)
                    user.setLevel(1 + (user.getExperiencePoints() / 100));
                    
                    userService.updateUser(user);
                    
                    response.put("xpGained", xpGained);
                    response.put("newLevel", user.getLevel());
                    response.put("totalXp", user.getExperiencePoints());
                }
            }
            
            response.put("success", true);
            response.put("message", "Prüfung erfolgreich abgegeben");
            
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Fehler beim Speichern der Prüfungsergebnisse");
        }
        
        return ResponseEntity.ok(response);
    }
    
    // DTO Klassen für Request/Response
    public static class ExamSubmission {
        private String examId;
        private List<ExamAnswer> answers;
        private int timeSpent;
        
        // Getters and Setters
        public String getExamId() { return examId; }
        public void setExamId(String examId) { this.examId = examId; }
        
        public List<ExamAnswer> getAnswers() { return answers; }
        public void setAnswers(List<ExamAnswer> answers) { this.answers = answers; }
        
        public int getTimeSpent() { return timeSpent; }
        public void setTimeSpent(int timeSpent) { this.timeSpent = timeSpent; }
    }
    
    public static class ExamAnswer {
        private Long questionId;
        private String selectedAnswer;
        private boolean isCorrect;
        
        // Getters and Setters
        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }
        
        public String getSelectedAnswer() { return selectedAnswer; }
        public void setSelectedAnswer(String selectedAnswer) { this.selectedAnswer = selectedAnswer; }
        
        public boolean isCorrect() { return isCorrect; }
        public void setCorrect(boolean correct) { isCorrect = correct; }
    }
}