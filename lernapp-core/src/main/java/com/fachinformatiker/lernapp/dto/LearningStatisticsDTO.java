package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Map;

/**
 * Learning Statistics DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningStatisticsDTO {
    private Long userId;
    private int totalSessions;
    private int totalQuestions;
    private int correctAnswers;
    private double averageScore;
    private double averageAccuracy;
    private int totalTimeMinutes;
    private int totalLearningMinutes;
    private int topicsCompleted;
    private Map<LocalDate, Integer> dailyActivity;
    private Map<String, Double> topicPerformance;
    private double improvementRate;
}
