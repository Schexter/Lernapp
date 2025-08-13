package com.fachinformatiker.lernapp.domain.model;

import com.fachinformatiker.lernapp.domain.enums.DifficultyLevel;
import com.fachinformatiker.lernapp.domain.enums.QuestionType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Question Entity - Repräsentiert eine Lernfrage.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Entity
@Table(name = "questions",
    indexes = {
        @Index(name = "idx_question_topic", columnList = "topic_id"),
        @Index(name = "idx_question_type", columnList = "question_type"),
        @Index(name = "idx_question_difficulty", columnList = "difficulty_level"),
        @Index(name = "idx_question_active", columnList = "active")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"topic", "answers", "tags"})
@ToString(exclude = {"topic", "answers", "tags"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Question extends BaseEntity {

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Fragentext ist erforderlich")
    private String questionText;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type", nullable = false, length = 30)
    @NotNull(message = "Fragentyp ist erforderlich")
    @Builder.Default
    private QuestionType questionType = QuestionType.SINGLE_CHOICE;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty_level", nullable = false, length = 20)
    @NotNull(message = "Schwierigkeitsgrad ist erforderlich")
    @Builder.Default
    private DifficultyLevel difficultyLevel = DifficultyLevel.MEDIUM;

    @Column(name = "points")
    @Builder.Default
    private Integer points = 10;

    @Column(name = "time_limit_seconds")
    private Integer timeLimitSeconds;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @Column(name = "code_snippet", columnDefinition = "TEXT")
    private String codeSnippet;

    @Column(name = "code_language", length = 50)
    private String codeLanguage;

    @Column(name = "hint", columnDefinition = "TEXT")
    private String hint;

    @Column(name = "times_answered")
    @Builder.Default
    private Integer timesAnswered = 0;

    @Column(name = "times_correct")
    @Builder.Default
    private Integer timesCorrect = 0;

    @Column(name = "average_time_seconds")
    private Double averageTimeSeconds;

    @Column(name = "is_premium")
    @Builder.Default
    private Boolean isPremium = false;

    // Beziehungen
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id", nullable = false)
    @NotNull(message = "Topic ist erforderlich")
    private Topic topic;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.EAGER)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Answer> answers = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "question_tags",
        joinColumns = @JoinColumn(name = "question_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    @Builder.Default
    private Set<Tag> tags = new HashSet<>();

    /**
     * Hilfsmethode: Erfolgsrate berechnen
     */
    public double getSuccessRate() {
        if (timesAnswered == null || timesAnswered == 0) {
            return 0.0;
        }
        return (timesCorrect * 100.0) / timesAnswered;
    }

    /**
     * Hilfsmethode: Punkte basierend auf Schwierigkeit
     */
    public int calculatePoints() {
        if (points != null && points > 0) {
            return points;
        }
        // Default-Punkte basierend auf Schwierigkeit
        return switch (difficultyLevel) {
            case BEGINNER -> 5;
            case EASY -> 10;
            case MEDIUM -> 15;
            case HARD -> 20;
            case EXPERT -> 30;
        };
    }

    /**
     * Hilfsmethode: Antwort hinzufügen
     */
    public void addAnswer(Answer answer) {
        if (answers == null) {
            answers = new ArrayList<>();
        }
        answers.add(answer);
        answer.setQuestion(this);
    }

    /**
     * Hilfsmethode: Tag hinzufügen
     */
    public void addTag(Tag tag) {
        if (tags == null) {
            tags = new HashSet<>();
        }
        tags.add(tag);
    }

    /**
     * Hilfsmethode: Korrekte Antworten finden
     */
    public List<Answer> getCorrectAnswers() {
        if (answers == null) {
            return new ArrayList<>();
        }
        return answers.stream()
            .filter(Answer::getIsCorrect)
            .toList();
    }

    /**
     * Hilfsmethode: Hat Code-Snippet?
     */
    public boolean hasCodeSnippet() {
        return codeSnippet != null && !codeSnippet.trim().isEmpty();
    }

    /**
     * Hilfsmethode: Hat Bild?
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    /**
     * Statistik aktualisieren nach Antwort
     */
    public void updateStatistics(boolean wasCorrect, double timeInSeconds) {
        this.timesAnswered = (timesAnswered == null ? 0 : timesAnswered) + 1;
        if (wasCorrect) {
            this.timesCorrect = (timesCorrect == null ? 0 : timesCorrect) + 1;
        }
        
        // Durchschnittszeit aktualisieren
        if (averageTimeSeconds == null) {
            averageTimeSeconds = timeInSeconds;
        } else {
            averageTimeSeconds = ((averageTimeSeconds * (timesAnswered - 1)) + timeInSeconds) / timesAnswered;
        }
    }
}
