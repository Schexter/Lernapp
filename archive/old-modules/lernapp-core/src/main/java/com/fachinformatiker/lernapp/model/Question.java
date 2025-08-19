package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "questions",
    indexes = {
        @Index(name = "idx_question_topic", columnList = "topic_id"),
        @Index(name = "idx_question_type", columnList = "question_type"),
        @Index(name = "idx_question_difficulty", columnList = "difficulty_level"),
        @Index(name = "idx_question_active", columnList = "active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"topic", "answers", "userProgress", "tags"})
@EqualsAndHashCode(callSuper = true, exclude = {"topic", "answers", "userProgress", "tags"})
public class Question extends BaseEntity {
    
    @NotBlank(message = "Question text is required")
    @Column(name = "question_text", columnDefinition = "TEXT", nullable = false)
    private String questionText;
    
    @Column(name = "question_code", columnDefinition = "TEXT")
    private String questionCode;
    
    @Column(name = "image_url")
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", length = 20, nullable = false)
    @NotNull(message = "Question type is required")
    @Builder.Default
    private QuestionType questionType = QuestionType.MULTIPLE_CHOICE;
    
    @Column(name = "difficulty_level", nullable = false)
    @Builder.Default
    private Integer difficultyLevel = 1;
    
    @Column(name = "points")
    @Builder.Default
    private Integer points = 10;
    
    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;
    
    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;
    
    @Column(name = "hint", columnDefinition = "TEXT")
    private String hint;
    
    @Column(name = "reference_url")
    private String referenceUrl;
    
    // H2-kompatibel: CLOB statt JSONB
    @Lob
    @Column(name = "metadata")
    @Builder.Default
    private String metadata = "{}";
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @NotNull(message = "Topic is required")
    private Topic topic;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();
    
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<UserProgress> userProgress = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "question_tags",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
    
    // Additional fields for simplified access
    @Column(name = "answer_a")
    private String answerA;
    
    @Column(name = "answer_b")
    private String answerB;
    
    @Column(name = "answer_c")
    private String answerC;
    
    @Column(name = "answer_d")
    private String answerD;
    
    @Column(name = "correct_answer")
    private String correctAnswer;
    
    @Column(name = "subtopic")
    private String subtopic;
    
    public enum QuestionType {
        MULTIPLE_CHOICE,
        SINGLE_CHOICE,
        TRUE_FALSE,
        TEXT_INPUT,
        CODE_COMPLETION,
        DRAG_DROP,
        MATCHING
    }
    
    public void addAnswer(Answer answer) {
        answers.add(answer);
        answer.setQuestion(this);
    }
    
    // Helper method to get correct answer as string
    public String getCorrectAnswerString() {
        if (correctAnswer != null) {
            return correctAnswer;
        }
        // Fallback to finding correct answer from Answer entities
        for (Answer answer : answers) {
            if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                return answer.getAnswerText();
            }
        }
        return null;
    }
    
    // Helper method to get the correct Answer object
    public Answer getCorrectAnswerObject() {
        for (Answer answer : answers) {
            if (answer.getIsCorrect() != null && answer.getIsCorrect()) {
                return answer;
            }
        }
        return null;
    }
    
    public void removeAnswer(Answer answer) {
        answers.remove(answer);
        answer.setQuestion(null);
    }
    
    public Answer getCorrectAnswer() {
        return answers.stream()
            .filter(Answer::getIsCorrect)
            .findFirst()
            .orElse(null);
    }
    
    public List<Answer> getCorrectAnswers() {
        return answers.stream()
            .filter(Answer::getIsCorrect)
            .toList();
    }
}