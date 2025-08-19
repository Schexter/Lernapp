package de.lernapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

/**
 * User Entity für die Lernapp
 * Verwaltet Benutzerkonten und deren Authentifizierung
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String username;
    
    @Column(nullable = false, unique = true, length = 100)
    private String email;
    
    @JsonIgnore
    @Column(nullable = false)
    private String password;
    
    @Column(name = "first_name", length = 50)
    private String firstName;
    
    @Column(name = "last_name", length = 50)
    private String lastName;
    
    @Column(nullable = false)
    private boolean enabled = true;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @Column(name = "last_login")
    private LocalDateTime lastLogin;
    
    // Beziehungen
    @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(
        name = "user_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    
    // Lernfortschritt
    @Column(name = "total_questions_answered")
    private int totalQuestionsAnswered = 0;
    
    @Column(name = "correct_answers")
    private int correctAnswers = 0;
    
    @Column(name = "current_streak")
    private int currentStreak = 0;
    
    @Column(name = "best_streak")
    private int bestStreak = 0;
    
    @Column(name = "experience_points")
    private int experiencePoints = 0;
    
    @Column(name = "level")
    private int level = 1;
    
    // Lifecycle Callbacks
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Helper Methods
    public void addRole(Role role) {
        roles.add(role);
    }
    
    public void removeRole(Role role) {
        roles.remove(role);
    }
    
    public double getAccuracy() {
        if (totalQuestionsAnswered == 0) return 0.0;
        return (double) correctAnswers / totalQuestionsAnswered * 100;
    }
    
    public void incrementCorrectAnswer() {
        correctAnswers++;
        totalQuestionsAnswered++;
        currentStreak++;
        if (currentStreak > bestStreak) {
            bestStreak = currentStreak;
        }
        experiencePoints += 10;
        checkLevelUp();
    }
    
    public void incrementWrongAnswer() {
        totalQuestionsAnswered++;
        currentStreak = 0;
        experiencePoints += 2;
        checkLevelUp();
    }
    
    private void checkLevelUp() {
        int requiredXP = level * 100;
        if (experiencePoints >= requiredXP) {
            level++;
            experiencePoints = experiencePoints - requiredXP;
        }
    }
}