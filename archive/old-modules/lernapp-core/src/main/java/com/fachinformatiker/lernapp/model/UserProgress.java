package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_progress",
    indexes = {
        @Index(name = "idx_progress_user_question", columnList = "user_id, question_id", unique = true),
        @Index(name = "idx_progress_next_review", columnList = "next_review"),
        @Index(name = "idx_progress_confidence", columnList = "confidence_level")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "question"})
@EqualsAndHashCode(callSuper = true, exclude = {"user", "question"})
public class UserProgress extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "Question is required")
    private Question question;
    
    @Column(name = "attempts")
    @Builder.Default
    private Integer attempts = 0;
    
    @Column(name = "correct_attempts")
    @Builder.Default
    private Integer correctAttempts = 0;
    
    @Column(name = "last_attempt")
    private LocalDateTime lastAttempt;
    
    @Column(name = "next_review")
    private LocalDateTime nextReview;
    
    @Column(name = "repetition_box")
    @Builder.Default
    private Integer repetitionBox = 1;
    
    @Column(name = "confidence_level", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal confidenceLevel = BigDecimal.ZERO;
    
    @Column(name = "repetition_interval")
    @Builder.Default
    private Integer repetitionInterval = 1;
    
    @Column(name = "ease_factor", precision = 4, scale = 2)
    @Builder.Default
    private BigDecimal easeFactor = new BigDecimal("2.5");
    
    @Column(name = "consecutive_correct")
    @Builder.Default
    private Integer consecutiveCorrect = 0;
    
    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Long timeSpentSeconds = 0L;
    
    @Column(name = "last_response_time_seconds")
    private Integer lastResponseTimeSeconds;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "mastery_level", length = 20)
    @Builder.Default
    private MasteryLevel masteryLevel = MasteryLevel.NOT_STARTED;
    
    public enum MasteryLevel {
        NOT_STARTED,
        LEARNING,
        FAMILIAR,
        PROFICIENT,
        MASTERED
    }
    
    public BigDecimal getAccuracy() {
        if (attempts == 0) {
            return BigDecimal.ZERO;
        }
        return new BigDecimal(correctAttempts)
            .divide(new BigDecimal(attempts), 2, BigDecimal.ROUND_HALF_UP)
            .multiply(new BigDecimal(100));
    }
    
    public void recordAttempt(boolean isCorrect, int responseTimeSeconds) {
        attempts++;
        lastAttempt = LocalDateTime.now();
        lastResponseTimeSeconds = responseTimeSeconds;
        timeSpentSeconds += responseTimeSeconds;
        
        if (isCorrect) {
            correctAttempts++;
            consecutiveCorrect++;
            updateMasteryLevel();
        } else {
            consecutiveCorrect = 0;
            if (masteryLevel != MasteryLevel.NOT_STARTED) {
                masteryLevel = MasteryLevel.LEARNING;
            }
        }
    }
    
    private void updateMasteryLevel() {
        if (consecutiveCorrect >= 5 && confidenceLevel.compareTo(new BigDecimal("0.9")) >= 0) {
            masteryLevel = MasteryLevel.MASTERED;
        } else if (consecutiveCorrect >= 3 && confidenceLevel.compareTo(new BigDecimal("0.7")) >= 0) {
            masteryLevel = MasteryLevel.PROFICIENT;
        } else if (consecutiveCorrect >= 2 && confidenceLevel.compareTo(new BigDecimal("0.5")) >= 0) {
            masteryLevel = MasteryLevel.FAMILIAR;
        } else {
            masteryLevel = MasteryLevel.LEARNING;
        }
    }
}