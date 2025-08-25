package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.util.List;

/**
 * Learning Path DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningPathDTO {
    private Long id;
    private String name;
    private String description;
    private String shortDescription;
    private String iconUrl;
    private String bannerUrl;
    
    private Integer difficultyLevel;
    private Integer estimatedHours;
    private String prerequisites;
    private String learningObjectives;
    private String targetAudience;
    
    private Boolean certificationAvailable;
    private Integer sortOrder;
    
    private List<TopicDTO> topics;
    private Integer totalQuestions;
    private Integer completedQuestions;
    private Double progressPercentage;
    
    private Boolean isEnrolled;
    private Boolean isCompleted;
    private Integer enrolledUsers;
}
