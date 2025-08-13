package com.fachinformatiker.lernapp.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

/**
 * Answer Entity - Repräsentiert eine Antwortmöglichkeit zu einer Frage.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Entity
@Table(name = "answers",
    indexes = {
        @Index(name = "idx_answer_question", columnList = "question_id"),
        @Index(name = "idx_answer_correct", columnList = "is_correct")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"question"})
@ToString(exclude = {"question"})
public class Answer extends BaseEntity {

    @Column(name = "answer_text", nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Antworttext ist erforderlich")
    private String answerText;

    @Column(name = "is_correct", nullable = false)
    @NotNull(message = "Korrektheitsstatus ist erforderlich")
    @Builder.Default
    private Boolean isCorrect = false;

    @Column(name = "explanation", columnDefinition = "TEXT")
    private String explanation;

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "image_url", length = 500)
    private String imageUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull(message = "Frage ist erforderlich")
    private Question question;

    /**
     * Hilfsmethode: Hat die Antwort ein Bild?
     */
    public boolean hasImage() {
        return imageUrl != null && !imageUrl.trim().isEmpty();
    }

    /**
     * Hilfsmethode: Hat die Antwort eine Erklärung?
     */
    public boolean hasExplanation() {
        return explanation != null && !explanation.trim().isEmpty();
    }
}
