package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;

/**
 * User Progress DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProgressDTO {
    private Long id;
    private Long userId;
    private Long topicId;
    private Long questionId;
    private String topicName;
    private String username;
    
    private Integer attempts;
    private Integer correctAttempts;
    private BigDecimal confidenceLevel;
    private Double progressPercentage;
    
    private LocalDateTime lastAttemptAt;
    private LocalDateTime nextReviewAt;
    
    private Integer repetitionInterval;
    private BigDecimal easeFactor;
    private Integer consecutiveCorrect;
    
    private Long timeSpentSeconds;
    private String masteryLevel; // NOT_STARTED, LEARNING, REVIEWING, MASTERED
    
    // Calculated fields
    private Double accuracy;
    private Boolean needsReview;
}
