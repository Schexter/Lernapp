package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Entity representing a learning session
 */
@Entity
@Table(name = "learning_sessions",
    indexes = {
        @Index(name = "idx_session_user", columnList = "user_id"),
        @Index(name = "idx_session_active", columnList = "active"),
        @Index(name = "idx_session_start", columnList = "start_time")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"user", "topic", "questions", "attempts"})
@ToString(exclude = {"user", "topic", "questions", "attempts"})
public class LearningSession {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "topic_id")
    private Topic topic;
    
    @Column(name = "session_type", nullable = false)
    private String sessionType;
    
    @Column(name = "start_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "duration_minutes")
    private Integer durationMinutes;
    
    @Column(name = "total_questions", nullable = false)
    @Builder.Default
    private Integer totalQuestions = 0;
    
    @Column(name = "answered_questions", nullable = false)
    @Builder.Default
    private Integer answeredQuestions = 0;
    
    @Column(name = "correct_answers", nullable = false)
    @Builder.Default
    private Integer correctAnswers = 0;
    
    @Column(name = "score")
    private Double score;
    
    @Column(name = "active", nullable = false)
    @Builder.Default
    private boolean active = true;
    
    @Column(name = "completed", nullable = false)
    @Builder.Default
    private boolean completed = false;
    
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "session_questions",
        joinColumns = @JoinColumn(name = "session_id"),
        inverseJoinColumns = @JoinColumn(name = "question_id")
    )
    @Builder.Default
    private Set<Question> questions = new HashSet<>();
    
    @OneToMany(mappedBy = "learningSession", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private Set<QuestionAttempt> attempts = new HashSet<>();
}