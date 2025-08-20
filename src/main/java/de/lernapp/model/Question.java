package de.lernapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

/**
 * Entity-Klasse für Prüfungsfragen
 * Optimiert für Multiple-Choice Fragen mit 4 Antwortoptionen
 * 
 * @author Hans Hahn
 */
@Entity
@Table(name = "questions", 
    indexes = {
        @Index(name = "idx_category", columnList = "category"),
        @Index(name = "idx_difficulty", columnList = "difficulty"),
        @Index(name = "idx_active", columnList = "active")
    })
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Question {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 2000)
    private String questionText;
    
    // Die vier Antwortmöglichkeiten
    @Column(nullable = false, length = 500)
    private String optionA;
    
    @Column(nullable = false, length = 500)
    private String optionB;
    
    @Column(nullable = false, length = 500)
    private String optionC;
    
    @Column(nullable = false, length = 500)
    private String optionD;
    
    @Column(nullable = false, length = 1)
    private String correctAnswer; // "A", "B", "C" oder "D"
    
    @Column(nullable = false, length = 100)
    private String category; // z.B. "Geschäftsprozesse", "IT-Systeme", "Vernetzte Systeme"
    
    @Column(length = 100)
    private String topic; // Hauptthema
    
    @Column(length = 100)
    private String subtopic; // Unterthema
    
    @Column(nullable = false)
    private Integer difficulty; // 1 = Leicht, 2 = Mittel, 3 = Schwer
    
    @Column(length = 2000)
    private String explanation; // Erklärung zur richtigen Antwort
    
    @Column(length = 500)
    private String tags; // Komma-getrennte Tags
    
    @Column(length = 1000)
    private String tips; // Tipps zur Lösung
    
    @Column(length = 1000)
    private String correction; // Korrekturhinweise bei falscher Antwort
    
    @Column(length = 200)
    private String source; // Quelle der Frage (z.B. "AP1 Prüfungsvorbereitung")
    
    @Column(nullable = false)
    private Integer points = 10; // Punkte für diese Frage
    
    @Column(nullable = false)
    private Boolean active = true; // Ob die Frage aktiv ist
    
    // Statistik-Felder
    @Column(name = "times_answered")
    private Integer timesAnswered = 0;
    
    @Column(name = "times_correct")
    private Integer timesCorrect = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (points == null) {
            // Standard-Punkte basierend auf Schwierigkeit
            points = difficulty * 10;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * Berechnet die Erfolgsrate dieser Frage
     */
    @Transient
    public Double getSuccessRate() {
        if (timesAnswered == null || timesAnswered == 0) {
            return null;
        }
        return (timesCorrect * 100.0) / timesAnswered;
    }
    
    /**
     * Gibt den Schwierigkeitsgrad als Text zurück
     */
    @Transient
    public String getDifficultyText() {
        if (difficulty == null) return "Unbekannt";
        switch (difficulty) {
            case 1: return "Leicht";
            case 2: return "Mittel";
            case 3: return "Schwer";
            default: return "Unbekannt";
        }
    }
    
    /**
     * Prüft ob eine gegebene Antwort korrekt ist
     */
    public boolean isCorrect(String answer) {
        return correctAnswer != null && correctAnswer.equalsIgnoreCase(answer);
    }
    
    /**
     * Gibt die korrekte Antwort als Text zurück
     */
    @Transient
    public String getCorrectAnswerText() {
        if (correctAnswer == null) return null;
        switch (correctAnswer.toUpperCase()) {
            case "A": return optionA;
            case "B": return optionB;
            case "C": return optionC;
            case "D": return optionD;
            default: return null;
        }
    }
}

// Erstellt von Hans Hahn - Alle Rechte vorbehalten