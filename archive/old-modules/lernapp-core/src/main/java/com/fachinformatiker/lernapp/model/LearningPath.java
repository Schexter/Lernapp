package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "learning_paths",
    indexes = {
        @Index(name = "idx_learning_path_active", columnList = "active"),
        @Index(name = "idx_learning_path_level", columnList = "difficulty_level")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"topics", "enrolledUsers"})
@EqualsAndHashCode(callSuper = true, exclude = {"topics", "enrolledUsers"})
public class LearningPath extends BaseEntity {
    
    @NotBlank(message = "Learning path name is required")
    @Column(name = "name", nullable = false)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "short_description")
    private String shortDescription;
    
    @Column(name = "icon_url")
    private String iconUrl;
    
    @Column(name = "banner_url")
    private String bannerUrl;
    
    @Column(name = "difficulty_level")
    @Builder.Default
    private Integer difficultyLevel = 1;
    
    @Column(name = "estimated_hours")
    private Integer estimatedHours;
    
    @Column(name = "prerequisites", columnDefinition = "TEXT")
    private String prerequisites;
    
    @Column(name = "learning_objectives", columnDefinition = "TEXT")
    private String learningObjectives;
    
    @Column(name = "target_audience", columnDefinition = "TEXT")
    private String targetAudience;
    
    @Column(name = "certification_available")
    @Builder.Default
    private Boolean certificationAvailable = false;
    
    @Column(name = "sort_order")
    @Builder.Default
    private Integer sortOrder = 0;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "learning_path_topics",
        joinColumns = @JoinColumn(name = "learning_path_id"),
        inverseJoinColumns = @JoinColumn(name = "topic_id")
    )
    @OrderBy("sortOrder ASC")
    @Builder.Default
    private List<Topic> topics = new ArrayList<>();
    
    @ManyToMany(mappedBy = "enrolledPaths", fetch = FetchType.LAZY)
    @Builder.Default
    private List<User> enrolledUsers = new ArrayList<>();
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by")
    private User createdBy;
    
    public void addTopic(Topic topic) {
        topics.add(topic);
        topic.getLearningPaths().add(this);
    }
    
    public void removeTopic(Topic topic) {
        topics.remove(topic);
        topic.getLearningPaths().remove(this);
    }
    
    public int getTotalQuestions() {
        return topics.stream()
            .mapToInt(Topic::getTotalQuestions)
            .sum();
    }
}