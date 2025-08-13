package com.fachinformatiker.lernapp.dto;

import com.fachinformatiker.lernapp.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.Set;

/**
 * Question DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    
    @NotNull(message = "Topic ID ist erforderlich")
    private Long topicId;
    
    @NotBlank(message = "Fragetext ist erforderlich")
    private String questionText;
    
    @NotNull(message = "Fragetyp ist erforderlich")
    private Question.QuestionType questionType;
    
    @NotNull(message = "Schwierigkeitsgrad ist erforderlich")
    private Integer difficultyLevel;
    
    private String explanation;
    private String imageUrl;
    private String codeSnippet;
    private Set<AnswerDTO> answers;
    private LocalDateTime createdAt;
    private Long createdBy;
}
