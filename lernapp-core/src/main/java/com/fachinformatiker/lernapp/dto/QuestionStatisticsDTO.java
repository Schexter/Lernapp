package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Question Statistics DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionStatisticsDTO {
    private Long questionId;
    private int totalAttempts;
    private double successRate;
    private double averageConfidence;
    private Integer difficulty;
    private int timesAnswered;
    private int timesCorrect;
    private int timesWrong;
    private double averageTimeSeconds;
}
