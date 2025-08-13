package com.fachinformatiker.lernapp.domain.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Tag Entity - Für die Kategorisierung von Fragen.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Entity
@Table(name = "tags",
    indexes = {
        @Index(name = "idx_tag_name", columnList = "name")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "name")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"questions"})
@ToString(exclude = {"questions"})
public class Tag extends BaseEntity {

    @Column(name = "name", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Tag-Name ist erforderlich")
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "color", length = 20)
    @Builder.Default
    private String color = "#6B7280";

    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Question> questions = new HashSet<>();
}
