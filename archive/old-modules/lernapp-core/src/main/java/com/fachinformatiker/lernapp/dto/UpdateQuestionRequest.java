package com.fachinformatiker.lernapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateQuestionRequest {
    
    @NotBlank(message = "Question text is required")
    private String questionText;
    
    private String explanation;
    
    private Long topicId;
    
    private String questionType;
    
    private Integer difficulty;
    
    private List<String> options;
    
    private String correctAnswer;
    
    private Map<String, Object> metadata;
    
    private String hint;
    
    private String referenceUrl;
    
    private List<String> tags;
    
    private Boolean active;
}
