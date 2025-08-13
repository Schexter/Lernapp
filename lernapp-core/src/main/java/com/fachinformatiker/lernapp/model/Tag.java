package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tags",
    indexes = {
        @Index(name = "idx_tag_name", columnList = "name", unique = true)
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = "questions")
@EqualsAndHashCode(callSuper = true, exclude = "questions")
public class Tag extends BaseEntity {
    
    @NotBlank(message = "Tag name is required")
    @Column(name = "name", unique = true, nullable = false, length = 50)
    private String name;
    
    @Column(name = "description")
    private String description;
    
    @Column(name = "color", length = 7)
    private String color;
    
    @ManyToMany(mappedBy = "tags", fetch = FetchType.LAZY)
    @Builder.Default
    private List<Question> questions = new ArrayList<>();
    
    public int getUsageCount() {
        return questions.size();
    }
}