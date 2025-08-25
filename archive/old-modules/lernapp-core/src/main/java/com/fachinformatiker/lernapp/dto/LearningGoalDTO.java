package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.time.LocalDateTime;

/**
 * Learning Goal DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningGoalDTO {
    private Long id;
    private Long userId;
    private String goalType; // DAILY, WEEKLY, MONTHLY, CUSTOM
    private String title;
    private String description;
    
    private Integer targetQuestions;
    private Integer targetMinutes;
    private Integer targetTopics;
    private Double targetAccuracy;
    
    private Integer completedQuestions;
    private Integer completedMinutes;
    private Integer completedTopics;
    private Double currentAccuracy;
    
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private LocalDateTime createdAt;
    
    private String status; // ACTIVE, COMPLETED, FAILED, CANCELLED
    private Double progressPercentage;
    private Boolean isActive;
}
