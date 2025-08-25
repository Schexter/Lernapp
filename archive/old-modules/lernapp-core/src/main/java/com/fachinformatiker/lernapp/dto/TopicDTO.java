package com.fachinformatiker.lernapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Topic DTO
 * Repräsentiert ein Lernthema
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicDTO {
    private Long id;
    
    @NotBlank(message = "Topic name is required")
    private String name;
    
    private String description;
    private String shortDescription;
    private String iconUrl;
    private String category;
    
    @Min(1) @Max(5)
    private Integer difficultyLevel;
    
    private Integer sortOrder;
    private Boolean active;
    
    // Statistics
    private Integer totalQuestions;
    private Integer totalLearners;
    private Double averageRating;
    
    // User specific
    private Integer userProgress;
    private Boolean isFavorite;
    private Boolean isCompleted;
}
