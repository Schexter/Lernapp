package com.fachinformatiker.lernapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ValidateAnswerRequest {
    
    @NotBlank(message = "Answer is required")
    private String answer;
    
    private Long userId;
    
    private Integer timeSpentSeconds;
}
