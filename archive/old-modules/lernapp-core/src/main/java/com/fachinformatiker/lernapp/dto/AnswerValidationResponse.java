package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnswerValidationResponse {
    
    private boolean correct;
    
    private String correctAnswer;
    
    private String explanation;
    
    private String hint;
    
    private Integer pointsEarned;
    
    private Integer totalPoints;
    
    private Double accuracy;
}
