package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "answers",
    indexes = {
        @Index(name = "idx_answer_question", columnList = "question_id"),
        @Index(name = "idx_answer_correct", columnList = "is_correct")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "question")
@EqualsAndHashCode(callSuper = true, exclude = "question")
public class Answer extends BaseEntity {
    
    @NotBlank(message = "Answer text is required")
    @Column(name = "answer_text", columnDefinition = "TEXT", nullable = false)
    private String answerText;
    
    @Column(name = "answer_code", columnDefinition = "TEXT")
    private String answerCode;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Column(name = "is_correct", nullable = false)
    @NotNull(message = "Correct flag is required")
    @Builder.Default
    private Boolean isCorrect = false;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "Question is required")
    private Question question;
}