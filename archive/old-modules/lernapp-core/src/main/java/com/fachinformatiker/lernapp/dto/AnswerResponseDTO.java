package com.fachinformatiker.lernapp.dto;

import lombok.*;

/**
 * Answer Response DTO
 * Antwort auf eine Antwort-Submission
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerResponseDTO {
    private Long questionId;
    private Long answerId;
    private Boolean correct;
    private String feedback;
    private String explanation;
    private Integer pointsEarned;
    private Long nextQuestionId;
    private Boolean sessionComplete;
    private Double sessionProgress;
}
