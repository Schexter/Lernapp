package com.fachinformatiker.lernapp.dto;

import com.fachinformatiker.lernapp.model.Question;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateQuestionDTO {
    
    @NotBlank(message = "Question text is required")
    @Size(min = 10, max = 5000, message = "Question text must be between 10 and 5000 characters")
    private String questionText;
    
    private String questionCode;
    
    private String imageUrl;
    
    @NotNull(message = "Question type is required")
    private Question.QuestionType questionType;
    
    @NotNull(message = "Difficulty level is required")
    @Min(value = 1, message = "Difficulty level must be at least 1")
    @Max(value = 5, message = "Difficulty level must be at most 5")
    private Integer difficultyLevel;
    
    @Min(value = 1, message = "Points must be at least 1")
    @Max(value = 100, message = "Points must be at most 100")
    @Builder.Default
    private Integer points = 10;
    
    @Min(value = 10, message = "Time limit must be at least 10 seconds")
    @Max(value = 3600, message = "Time limit must be at most 3600 seconds")
    private Integer timeLimitSeconds;
    
    @Size(max = 5000, message = "Explanation cannot exceed 5000 characters")
    private String explanation;
    
    @Size(max = 1000, message = "Hint cannot exceed 1000 characters")
    private String hint;
    
    private String referenceUrl;
    
    private Map<String, Object> metadata;
    
    @NotNull(message = "Topic ID is required")
    private Long topicId;
    
    @NotEmpty(message = "At least one answer is required")
    @Size(min = 2, max = 10, message = "Must have between 2 and 10 answers")
    private List<CreateAnswerDTO> answers;
    
    private List<String> tags;
    
    @AssertTrue(message = "At least one answer must be marked as correct")
    public boolean hasCorrectAnswer() {
        return answers != null && answers.stream()
            .anyMatch(answer -> Boolean.TRUE.equals(answer.getIsCorrect()));
    }
}