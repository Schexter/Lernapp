package de.lernapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 60%-Erfolgsstrategie Modell
 */
@Entity
@Table(name = "learning_strategies")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LearningStrategy {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name = "60% Erfolgsstrategie";
    
    @Column(nullable = false)
    private boolean active = false;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column(name = "activated_at")
    private LocalDateTime activatedAt;
    
    // Prioritätsstufen für Kategorien
    @ElementCollection
    @CollectionTable(name = "strategy_priorities")
    private List<CategoryPriority> priorities = new ArrayList<>();
    
    // Zeitmanagement-Einstellungen
    @Column(name = "phase1_minutes")
    private Integer phase1Minutes = 20; // Definitionen
    
    @Column(name = "phase2_minutes")
    private Integer phase2Minutes = 30; // Netzplan
    
    @Column(name = "phase3_minutes")
    private Integer phase3Minutes = 20; // PowerShell + IPv6
    
    @Column(name = "phase4_minutes")
    private Integer phase4Minutes = 15; // Berechnungen
    
    @Column(name = "phase5_minutes")
    private Integer phase5Minutes = 5;  // Kontrolle
    
    @Embeddable
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryPriority {
        private String category;
        private Integer priority; // 1 = höchste Priorität
        private Integer targetPoints;
        private Integer recommendedHours;
        private Double weight; // Gewichtung für Fragenauswahl
    }
    
    // Vordefinierte Prioritäten basierend auf 60%-Strategie
    public static List<CategoryPriority> getDefaultPriorities() {
        List<CategoryPriority> defaults = new ArrayList<>();
        
        // STUFE 1 - ABSOLUT UNVERZICHTBAR
        defaults.add(new CategoryPriority("Projektmanagement", 1, 25, 15, 2.0));
        defaults.add(new CategoryPriority("PowerShell", 1, 8, 10, 1.8));
        defaults.add(new CategoryPriority("Netzwerktechnik", 1, 20, 12, 1.8)); // IPv6
        defaults.add(new CategoryPriority("OSI-Modell", 1, 4, 8, 1.5));
        
        // STUFE 2 - SEHR WICHTIG
        defaults.add(new CategoryPriority("IT-Sicherheit", 2, 24, 10, 1.5));
        defaults.add(new CategoryPriority("Datenschutz", 2, 6, 5, 1.4));
        defaults.add(new CategoryPriority("Wirtschaftlichkeit", 2, 25, 8, 1.3));
        defaults.add(new CategoryPriority("3D-Daten", 2, 15, 10, 1.2)); // PLY
        defaults.add(new CategoryPriority("Hardware", 2, 20, 7, 1.2)); // Anschlüsse
        
        // STUFE 3 - SICHERHEITSPUFFER
        defaults.add(new CategoryPriority("Kaufvertragsrecht", 3, 4, 6, 0.8));
        defaults.add(new CategoryPriority("Malware", 3, 4, 6, 0.8));
        defaults.add(new CategoryPriority("Datenbanken", 3, 0, 0, 0.3)); // Nicht mehr relevant
        
        return defaults;
    }
    
    public void activate() {
        this.active = true;
        this.activatedAt = LocalDateTime.now();
        if (this.priorities.isEmpty()) {
            this.priorities = getDefaultPriorities();
        }
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public CategoryPriority getPriorityForCategory(String category) {
        return priorities.stream()
            .filter(p -> p.getCategory().equalsIgnoreCase(category))
            .findFirst()
            .orElse(new CategoryPriority(category, 4, 0, 0, 0.5));
    }
}