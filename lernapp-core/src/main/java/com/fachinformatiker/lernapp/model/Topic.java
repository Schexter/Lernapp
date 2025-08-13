package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "topics",
    indexes = {
        @Index(name = "idx_topic_parent", columnList = "parent_id"),
        @Index(name = "idx_topic_sort", columnList = "sort_order"),
        @Index(name = "idx_topic_active", columnList = "active")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"parent", "children", "questions", "learningPaths"})
@EqualsAndHashCode(callSuper = true, exclude = {"parent", "children", "questions", "learningPaths"})
public class Topic extends BaseEntity {
    
    @NotBlank(message = "Topic name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Column(name = "color_code", length = 7)
    private String colorCode;
    
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    @Column(name = "difficulty_level")
    @Builder.Default
    private Integer difficultyLevel = 1;
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Topic parent;
    
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Topic> children = new ArrayList<>();
    
    @OneToMany(mappedBy = "topic", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
    
    @ManyToMany(mappedBy = "topics", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<LearningPath> learningPaths = new HashSet<>();
    
    public void addChild(Topic child) {
        children.add(child);
        child.setParent(this);
    }
    
    public void removeChild(Topic child) {
        children.remove(child);
        child.setParent(null);
    }
    
    public String getFullPath() {
        if (parent == null) {
            return name;
        }
        return parent.getFullPath() + " > " + name;
    }
    
    public int getTotalQuestions() {
        int total = questions.size();
        for (Topic child : children) {
            total += child.getTotalQuestions();
        }
        return total;
    }
}