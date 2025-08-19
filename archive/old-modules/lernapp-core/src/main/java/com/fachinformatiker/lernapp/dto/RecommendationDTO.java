package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.util.List;

/**
 * Recommendation DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationDTO {
    private String type; // TOPIC, QUESTION, LEARNING_PATH
    private Long targetId;
    private String title;
    private String description;
    private String reason;
    private Integer priority;
    private String iconUrl;
    private List<String> benefits;
    private Integer estimatedMinutes;
    private Double confidenceScore;
}
