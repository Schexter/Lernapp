package de.lernapp.controller;

import de.lernapp.model.*;
import de.lernapp.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller für die 60%-Erfolgsstrategie
 */
@RestController
@RequestMapping("/api/strategy")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:5173", "http://localhost:3000"})
public class StrategyController {
    
    private final StrategyService strategyService;
    private final UserService userService;
    private final QuestionService questionService;
    
    /**
     * Aktiviert die 60%-Strategie für den aktuellen Benutzer
     */
    @PostMapping("/activate")
    public ResponseEntity<?> activateStrategy(Authentication auth) {
        User user = userService.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        LearningStrategy strategy = strategyService.activateStrategy(user);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "60%-Erfolgsstrategie aktiviert!");
        response.put("strategy", Map.of(
            "name", strategy.getName(),
            "active", strategy.isActive(),
            "priorities", strategy.getPriorities()
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Deaktiviert die Strategie
     */
    @PostMapping("/deactivate")
    public ResponseEntity<?> deactivateStrategy(Authentication auth) {
        // TODO: Implementierung
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "Strategie deaktiviert");
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Holt strategiebasierte Fragen
     */
    @GetMapping("/questions")
    public ResponseEntity<?> getStrategyQuestions(
            Authentication auth,
            @RequestParam(defaultValue = "10") int count) {
        
        User user = userService.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        List<Question> questions = strategyService.getStrategyQuestions(user, count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("questions", questions);
        response.put("strategy_active", true);
        response.put("focus_categories", List.of(
            "Projektmanagement", "PowerShell", "Netzwerktechnik", "OSI-Modell"
        ));
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Holt Quick-Win Fragen für schnelle Punkte
     */
    @GetMapping("/quick-wins")
    public ResponseEntity<?> getQuickWins(
            Authentication auth,
            @RequestParam(defaultValue = "10") int count) {
        
        User user = userService.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        List<Question> quickWins = strategyService.getQuickWinQuestions(user, count);
        
        Map<String, Object> response = new HashMap<>();
        response.put("questions", quickWins);
        response.put("description", "Einfache Fragen für garantierte Punkte");
        response.put("estimated_points", quickWins.size() * 3);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Holt personalisierte Lernempfehlungen
     */
    @GetMapping("/recommendations")
    public ResponseEntity<?> getRecommendations(Authentication auth) {
        User user = userService.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
            
        StrategyService.LearningRecommendation recommendation = 
            strategyService.getRecommendation(user);
        
        return ResponseEntity.ok(recommendation);
    }
    
    /**
     * Holt Zeitmanagement-Strategie für die Prüfung
     */
    @GetMapping("/exam-time-strategy")
    public ResponseEntity<?> getExamTimeStrategy() {
        StrategyService.ExamTimeStrategy timeStrategy = 
            strategyService.getExamTimeStrategy();
        
        Map<String, Object> response = new HashMap<>();
        response.put("total_minutes", 90);
        response.put("phases", timeStrategy.getPhases());
        response.put("target_points", 65);
        response.put("success_threshold", 50);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * Holt Strategie-Status
     */
    @GetMapping("/status")
    public ResponseEntity<?> getStrategyStatus(Authentication auth) {
        User user = userService.findByUsername(auth.getName())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Map<String, Object> status = new HashMap<>();
        status.put("strategy_active", true); // TODO: Aus DB laden
        status.put("focus_areas", Map.of(
            "Netzplantechnik", Map.of("priority", 1, "progress", 45),
            "PowerShell", Map.of("priority", 1, "progress", 60),
            "IPv6", Map.of("priority", 1, "progress", 20),
            "OSI-Modell", Map.of("priority", 1, "progress", 70),
            "CIA-Triad", Map.of("priority", 2, "progress", 80)
        ));
        status.put("estimated_success_rate", 62);
        status.put("recommended_study_hours", 80);
        status.put("completed_study_hours", 12);
        
        return ResponseEntity.ok(status);
    }
    
    /**
     * Info-Endpoint über die 60%-Strategie
     */
    @GetMapping("/info")
    public ResponseEntity<?> getStrategyInfo() {
        Map<String, Object> info = new HashMap<>();
        
        info.put("name", "60%-Erfolgsstrategie Post-RAID");
        info.put("description", "Optimierte Lernstrategie für sicheres Bestehen der AP1-Prüfung");
        info.put("key_changes", List.of(
            "RAID-Systeme sind NICHT mehr prüfungsrelevant",
            "IPv6 und PLY-Dateiformate sind NEU",
            "Fokus auf Netzplantechnik (25 Punkte)",
            "PowerShell-Debugging (8 Punkte)",
            "Neue Themen: OSI-Modell, Anschlusstechnik"
        ));
        
        info.put("success_rates", Map.of(
            "50_points", "70-80% Chance",
            "60_points", "55-65% Chance",
            "70_points", "35-45% Chance"
        ));
        
        info.put("required_study_hours", 80);
        info.put("quick_win_points", 30);
        
        return ResponseEntity.ok(info);
    }
}