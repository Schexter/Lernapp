package com.fachinformatiker.lernapp.dto;

import lombok.*;

/**
 * Topic Progress DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TopicProgressDTO {
    private Long topicId;
    private String topicName;
    private String topicDescription;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    private Double progressPercentage;
    private Double accuracy;
    private String difficulty;
    private Boolean isWeak;
    private String recommendedAction;
}
