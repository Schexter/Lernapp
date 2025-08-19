package com.fachinformatiker.lernapp.dto;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateAnswerDTO {
    
    @NotBlank(message = "Answer text is required")
    @Size(min = 1, max = 2000, message = "Answer text must be between 1 and 2000 characters")
    private String answerText;
    
    private String answerCode;
    
    private String imageUrl;
    
    @NotNull(message = "Correct flag is required")
    private Boolean isCorrect;
    
    @Size(max = 2000, message = "Explanation cannot exceed 2000 characters")
    private String explanation;
    
    @Min(value = 0, message = "Sort order must be non-negative")
    @Builder.Default
    private Integer sortOrder = 0;
}