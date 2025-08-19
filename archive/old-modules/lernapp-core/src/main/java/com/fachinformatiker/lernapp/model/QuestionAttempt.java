package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entity representing a single question attempt in a learning session
 */
@Entity
@Table(name = "question_attempts",
    indexes = {
        @Index(name = "idx_attempt_session", columnList = "session_id"),
        @Index(name = "idx_attempt_user", columnList = "user_id"),
        @Index(name = "idx_attempt_question", columnList = "question_id"),
        @Index(name = "idx_attempt_time", columnList = "attempt_time")
    })
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(exclude = {"learningSession", "user", "question"})
@ToString(exclude = {"learningSession", "user", "question"})
public class QuestionAttempt {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    private LearningSession learningSession;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    private Question question;
    
    @Column(name = "given_answer")
    private String givenAnswer;
    
    @Column(name = "is_correct", nullable = false)
    private boolean correct;
    
    @Column(name = "response_time")
    private Integer responseTime; // in seconds
    
    @Column(name = "attempt_time", nullable = false)
    @CreationTimestamp
    private LocalDateTime attemptTime;
    
    @Column(name = "confidence_rating")
    private Integer confidenceRating; // 1-5 scale
    
    @Column(name = "was_flagged")
    @Builder.Default
    private boolean flagged = false;
    
    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;
}