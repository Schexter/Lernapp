package com.fachinformatiker.lernapp.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

/**
 * Progress Entity - Trackt den Lernfortschritt eines Users für eine Frage.
 * Implementiert Spaced Repetition für optimales Lernen.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Entity
@Table(name = "user_progress",
    indexes = {
        @Index(name = "idx_progress_user", columnList = "user_id"),
        @Index(name = "idx_progress_question", columnList = "question_id"),
        @Index(name = "idx_progress_next_review", columnList = "next_review"),
        @Index(name = "idx_progress_mastery", columnList = "mastery_level")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "question_id"})
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"user", "question"})
@ToString(exclude = {"user", "question"})
public class Progress extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User ist erforderlich")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "Question ist erforderlich")
    private Question question;

    @Column(name = "attempts")
    @Builder.Default
    private Integer attempts = 0;

    @Column(name = "correct_attempts")
    @Builder.Default
    private Integer correctAttempts = 0;

    @Column(name = "incorrect_attempts")
    @Builder.Default
    private Integer incorrectAttempts = 0;

    @Column(name = "correct_streak")
    @Builder.Default
    private Integer correctStreak = 0;

    @Column(name = "last_attempt")
    private LocalDateTime lastAttempt;

    @Column(name = "next_review")
    private LocalDateTime nextReview;

    @Column(name = "confidence_level", precision = 3, scale = 2)
    @Min(0)
    @Max(1)
    @Builder.Default
    private Double confidenceLevel = 0.0;

    @Column(name = "mastery_level")
    @Min(0)
    @Max(5)
    @Builder.Default
    private Integer masteryLevel = 0;

    @Column(name = "average_response_time")
    private Double averageResponseTime;

    @Column(name = "total_time_spent")
    @Builder.Default
    private Long totalTimeSpent = 0L;

    // Spaced Repetition Parameter (SM-2 Algorithm)
    @Column(name = "easiness_factor", precision = 3, scale = 2)
    @Builder.Default
    private Double easinessFactor = 2.5;

    @Column(name = "repetition_interval")
    @Builder.Default
    private Integer repetitionInterval = 1;

    @Column(name = "review_count")
    @Builder.Default
    private Integer reviewCount = 0;

    /**
     * Erfolgsrate berechnen
     */
    public double getSuccessRate() {
        if (attempts == null || attempts == 0) {
            return 0.0;
        }
        return (correctAttempts * 100.0) / attempts;
    }

    /**
     * Antwort verarbeiten und Progress aktualisieren
     */
    public void processAnswer(boolean isCorrect, long responseTimeSeconds) {
        // Basis-Statistiken aktualisieren
        this.attempts = (attempts == null ? 0 : attempts) + 1;
        this.lastAttempt = LocalDateTime.now();
        this.totalTimeSpent = (totalTimeSpent == null ? 0 : totalTimeSpent) + responseTimeSeconds;
        
        if (isCorrect) {
            this.correctAttempts = (correctAttempts == null ? 0 : correctAttempts) + 1;
            this.correctStreak = (correctStreak == null ? 0 : correctStreak) + 1;
        } else {
            this.incorrectAttempts = (incorrectAttempts == null ? 0 : incorrectAttempts) + 1;
            this.correctStreak = 0;
        }
        
        // Durchschnittliche Antwortzeit aktualisieren
        if (averageResponseTime == null) {
            averageResponseTime = (double) responseTimeSeconds;
        } else {
            averageResponseTime = ((averageResponseTime * (attempts - 1)) + responseTimeSeconds) / attempts;
        }
        
        // Confidence Level aktualisieren
        updateConfidenceLevel();
        
        // Mastery Level aktualisieren
        updateMasteryLevel();
        
        // Spaced Repetition berechnen
        calculateNextReview(isCorrect);
    }

    /**
     * Confidence Level basierend auf Performance aktualisieren
     */
    private void updateConfidenceLevel() {
        if (attempts == null || attempts == 0) {
            confidenceLevel = 0.0;
            return;
        }
        
        double successRate = getSuccessRate() / 100.0;
        double streakBonus = Math.min(correctStreak * 0.1, 0.3);
        confidenceLevel = Math.min(1.0, successRate * 0.7 + streakBonus);
    }

    /**
     * Mastery Level basierend auf Performance aktualisieren
     */
    private void updateMasteryLevel() {
        if (correctStreak >= 5) {
            masteryLevel = 5; // Meister
        } else if (correctStreak >= 4) {
            masteryLevel = 4; // Fortgeschritten
        } else if (correctStreak >= 3) {
            masteryLevel = 3; // Kompetent
        } else if (correctStreak >= 2) {
            masteryLevel = 2; // Entwickelt
        } else if (correctStreak >= 1) {
            masteryLevel = 1; // Anfänger
        } else {
            masteryLevel = 0; // Unbeherrscht
        }
    }

    /**
     * SM-2 Algorithm für Spaced Repetition
     */
    private void calculateNextReview(boolean isCorrect) {
        reviewCount = (reviewCount == null ? 0 : reviewCount) + 1;
        
        if (isCorrect) {
            // Erfolgreiche Antwort
            if (reviewCount == 1) {
                repetitionInterval = 1; // 1 Tag
            } else if (reviewCount == 2) {
                repetitionInterval = 6; // 6 Tage
            } else {
                repetitionInterval = (int) Math.round(repetitionInterval * easinessFactor);
            }
            
            // Easiness Factor anpassen (nie unter 1.3)
            easinessFactor = Math.max(1.3, easinessFactor + 0.1);
        } else {
            // Falsche Antwort - zurücksetzen
            repetitionInterval = 1;
            reviewCount = 0;
            easinessFactor = Math.max(1.3, easinessFactor - 0.2);
        }
        
        // Nächste Review-Zeit berechnen
        nextReview = LocalDateTime.now().plusDays(repetitionInterval);
    }

    /**
     * Ist die Frage zur Review fällig?
     */
    public boolean isDueForReview() {
        return nextReview == null || LocalDateTime.now().isAfter(nextReview);
    }
}
