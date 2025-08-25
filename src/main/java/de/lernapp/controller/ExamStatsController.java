package de.lernapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.jdbc.core.JdbcTemplate;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/exam")
@CrossOrigin(origins = "*")
public class ExamStatsController {
    
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @GetMapping("/stats")
    public Map<String, Object> getExamStats(Authentication authentication) {
        Map<String, Object> stats = new HashMap<>();
        
        try {
            if (authentication != null && authentication.isAuthenticated()) {
                String username = authentication.getName();
                
                // Beste Leistung (Prozent der richtigen Antworten)
                Integer bestScore = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(MAX((CAST(correct_answers AS DECIMAL) / NULLIF(total_questions, 0)) * 100), 0) " +
                    "FROM learning_progress WHERE username = ?",
                    Integer.class, username
                );
                
                // Durchschnittliche Zeit basierend auf Anzahl der Fragen (geschätzt)
                Integer avgTime = jdbcTemplate.queryForObject(
                    "SELECT COALESCE(AVG(total_questions * 2), 0) " +
                    "FROM learning_progress WHERE username = ? AND total_questions > 0",
                    Integer.class, username
                );
                
                // Anzahl der Lernsessions diesen Monat
                Integer sessionsThisMonth = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM learning_progress " +
                    "WHERE username = ? " +
                    "AND EXTRACT(MONTH FROM last_updated) = EXTRACT(MONTH FROM CURRENT_DATE) " +
                    "AND EXTRACT(YEAR FROM last_updated) = EXTRACT(YEAR FROM CURRENT_DATE)",
                    Integer.class, username
                );
                
                stats.put("bestScore", bestScore != null ? bestScore : 0);
                stats.put("averageTime", avgTime != null ? avgTime : 0);
                stats.put("examsThisMonth", sessionsThisMonth != null ? sessionsThisMonth : 0);
            } else {
                // Nicht authentifiziert - Standardwerte
                stats.put("bestScore", 0);
                stats.put("averageTime", 0);
                stats.put("examsThisMonth", 0);
            }
        } catch (Exception e) {
            // Bei Fehler Standardwerte zurückgeben
            System.err.println("Error fetching exam stats: " + e.getMessage());
            stats.put("bestScore", 0);
            stats.put("averageTime", 0);
            stats.put("examsThisMonth", 0);
        }
        
        return stats;
    }
}
