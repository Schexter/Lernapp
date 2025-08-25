package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * User Statistics DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatisticsDTO {
    private Long userId;
    private int totalQuestions;
    private int answeredQuestions;
    private int correctAnswers;
    private double averageScore;
    private int learningStreak;
    private int totalLearningTime;
    private double progressPercentage;
}
