package com.fachinformatiker.lernapp.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CreateQuestionRequest {
    
    @NotBlank(message = "Question text is required")
    private String questionText;
    
    private String explanation;
    
    @NotNull(message = "Topic ID is required")
    private Long topicId;
    
    @NotNull(message = "Question type is required")
    private String questionType;
    
    @NotNull(message = "Difficulty is required")
    private Integer difficulty;
    
    private List<String> options;
    
    private String correctAnswer;
    
    private Map<String, Object> metadata;
    
    private String hint;
    
    private String referenceUrl;
    
    private List<String> tags;
}
