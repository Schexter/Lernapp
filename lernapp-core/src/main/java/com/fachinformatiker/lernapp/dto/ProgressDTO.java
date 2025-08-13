package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.math.BigDecimal;

/**
 * Progress DTO
 * Vereinfachte Progress-Darstellung
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProgressDTO {
    private Long userId;
    private Long topicId;
    private String topicName;
    private Integer totalQuestions;
    private Integer completedQuestions;
    private Integer correctAnswers;
    private BigDecimal progressPercentage;
    private BigDecimal accuracy;
    private String level;
    private Integer points;
}
