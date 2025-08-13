package com.fachinformatiker.lernapp.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

/**
 * LearningPath Entity - Repräsentiert einen strukturierten Lernpfad.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Entity
@Table(name = "learning_paths")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"users", "topics"})
@ToString(exclude = {"users", "topics"})
public class LearningPath extends BaseEntity {

    @Column(name = "name", nullable = false, length = 255)
    @NotBlank(message = "Lernpfad-Name ist erforderlich")
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "target_completion_date")
    private LocalDate targetCompletionDate;

    @Column(name = "estimated_hours")
    private Integer estimatedHours;

    @Column(name = "is_public")
    @Builder.Default
    private Boolean isPublic = true;

    @ManyToMany(mappedBy = "learningPaths", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "learning_path_topics",
        joinColumns = @JoinColumn(name = "learning_path_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @Builder.Default
    private Set<Topic> topics = new HashSet<>();
}
