package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Learning Session DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LearningSessionDTO {
    private Long id;
    private Long userId;
    private Long topicId;
    private String topicName;
    private List<QuestionDTO> questions;
    private int currentQuestionIndex;
    private int totalQuestions;
    private int answeredQuestions;
    private int correctAnswers;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private boolean active;
    private boolean completed;
}
