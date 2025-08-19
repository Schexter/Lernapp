package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Question Progress DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionProgressDTO {
    private Long questionId;
    private String questionText;
    private int attempts;
    private int correctAttempts;
    private double confidenceLevel;
    private LocalDateTime lastAttempt;
    private LocalDateTime nextReview;
}
