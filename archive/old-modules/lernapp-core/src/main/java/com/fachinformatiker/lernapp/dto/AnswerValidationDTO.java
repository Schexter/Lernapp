package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Answer Validation DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerValidationDTO {
    private boolean correct;
    private String explanation;
    private Long correctAnswerId;
    private String correctAnswer;
}
