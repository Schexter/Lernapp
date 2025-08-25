package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "session_answers",
    indexes = {
        @Index(name = "idx_session_answer_session", columnList = "session_id"),
        @Index(name = "idx_session_answer_question", columnList = "question_id")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"session", "question", "selectedAnswer"})
@EqualsAndHashCode(callSuper = true, exclude = {"session", "question", "selectedAnswer"})
public class SessionAnswer extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @NotNull(message = "Session is required")
    private ExaminationSession session;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "Question is required")
    private Question question;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "selected_answer_id")
    private Answer selectedAnswer;
    
    @Column(name = "text_answer", columnDefinition = "TEXT")
    private String textAnswer;
    
    @Column(name = "is_correct")
    private Boolean isCorrect;
    
    @Column(name = "points_earned")
    @Builder.Default
    private Integer pointsEarned = 0;
    
    @Column(name = "time_spent_seconds")
    private Integer timeSpentSeconds;
    
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;
    
    @Column(name = "marked_for_review")
    @Builder.Default
    private Boolean markedForReview = false;
    
    @Column(name = "confidence_level")
    private Integer confidenceLevel;
    
    public void evaluateAnswer() {
        if (question.getQuestionType() == Question.QuestionType.TEXT_INPUT) {
            // Text answers need manual evaluation or special logic
            return;
        }
        
        if (selectedAnswer != null) {
            this.isCorrect = selectedAnswer.getIsCorrect();
            if (isCorrect) {
                this.pointsEarned = question.getPoints();
            }
        } else {
            this.isCorrect = false;
        }
    }
}