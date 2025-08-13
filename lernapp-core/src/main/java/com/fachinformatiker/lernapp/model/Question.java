package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "metadata", columnDefinition = "jsonb")
    private Map<String, Object> metadata;
    
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
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "question_tags",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private List<Tag> tags = new ArrayList<>();
    
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