package com.fachinformatiker.lernapp.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.util.ArrayList;
import java.util.List;

/**
 * Topic Entity - Repräsentiert ein Lernthema mit hierarchischer Struktur.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Entity
@Table(name = "topics",
    indexes = {
        @Index(name = "idx_topic_parent", columnList = "parent_topic_id"),
        @Index(name = "idx_topic_sort", columnList = "sort_order"),
        @Index(name = "idx_topic_active", columnList = "active")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"parentTopic", "subTopics", "questions"})
@ToString(exclude = {"parentTopic", "subTopics", "questions"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class Topic extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    @NotBlank(message = "Topic-Name ist erforderlich")
    @Size(max = 255, message = "Topic-Name darf maximal 255 Zeichen lang sein")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "icon", length = 100)
    private String icon;

    @Column(name = "color", length = 20)
    @Builder.Default
    private String color = "#3B82F6"; // Default: Blau

    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;

    @Column(name = "difficulty_level")
    @Builder.Default
    private Integer difficultyLevel = 1;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "is_premium")
    @Builder.Default
    private Boolean isPremium = false;

    // Self-referencing für Hierarchie
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_topic_id")
    private Topic parentTopic;

    @OneToMany(mappedBy = "parentTopic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @OrderBy("sortOrder ASC, name ASC")
    @Builder.Default
    private List<Topic> subTopics = new ArrayList<>();

    // Beziehung zu Questions
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();

    /**
     * Hilfsmethode: Ist dies ein Root-Topic?
     */
    public boolean isRootTopic() {
        return parentTopic == null;
    }

    /**
     * Hilfsmethode: Hat das Topic Unter-Topics?
     */
    public boolean hasSubTopics() {
        return subTopics != null && !subTopics.isEmpty();
    }

    /**
     * Hilfsmethode: Anzahl der Fragen (inkl. Sub-Topics)
     */
    public int getTotalQuestionCount() {
        int count = questions != null ? questions.size() : 0;
        if (subTopics != null) {
            for (Topic subTopic : subTopics) {
                count += subTopic.getTotalQuestionCount();
            }
        }
        return count;
    }

    /**
     * Hilfsmethode: Pfad zum Root-Topic
     */
    public String getFullPath() {
        if (parentTopic == null) {
            return name;
        }
        return parentTopic.getFullPath() + " > " + name;
    }

    /**
     * Hilfsmethode: Topic-Tiefe in der Hierarchie
     */
    public int getDepth() {
        if (parentTopic == null) {
            return 0;
        }
        return parentTopic.getDepth() + 1;
    }

    /**
     * Hilfsmethode: Sub-Topic hinzufügen
     */
    public void addSubTopic(Topic subTopic) {
        if (subTopics == null) {
            subTopics = new ArrayList<>();
        }
        subTopics.add(subTopic);
        subTopic.setParentTopic(this);
    }

    /**
     * Hilfsmethode: Question hinzufügen
     */
    public void addQuestion(Question question) {
        if (questions == null) {
            questions = new ArrayList<>();
        }
        questions.add(question);
        question.setTopic(this);
    }
}
