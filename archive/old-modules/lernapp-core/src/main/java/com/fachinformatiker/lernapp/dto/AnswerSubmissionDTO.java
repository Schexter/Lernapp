package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotNull;

/**
 * Answer Submission DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerSubmissionDTO {
    @NotNull
    private Long userId;
    
    @NotNull
    private Long questionId;
    
    private Long answerId; // Für Multiple Choice
    private String textAnswer; // Für Text-Antworten
    private Boolean booleanAnswer; // Für True/False
}
