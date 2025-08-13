package com.fachinformatiker.lernapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "examination_sessions",
    indexes = {
        @Index(name = "idx_exam_user", columnList = "user_id"),
        @Index(name = "idx_exam_status", columnList = "status"),
        @Index(name = "idx_exam_started", columnList = "started_at")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"user", "sessionAnswers"})
@EqualsAndHashCode(callSuper = true, exclude = {"user", "sessionAnswers"})
public class ExaminationSession extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User is required")
    private User user;
    
    @Column(name = "session_type", length = 30)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionType sessionType = SessionType.PRACTICE;
    
    @Column(name = "topic_ids")
    private String topicIds;
    
    @Column(name = "total_questions")
    private Integer totalQuestions;
    
    @Column(name = "answered_questions")
    @Builder.Default
    private Integer answeredQuestions = 0;
    
    @Column(name = "correct_answers")
    @Builder.Default
    private Integer correctAnswers = 0;
    
    @Column(name = "score", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal score = BigDecimal.ZERO;
    
    @Column(name = "time_limit_minutes")
    private Integer timeLimitMinutes;
    
    @Column(name = "time_spent_seconds")
    @Builder.Default
    private Long timeSpentSeconds = 0L;
    
    @Column(name = "started_at")
    private LocalDateTime startedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "status", length = 20)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private SessionStatus status = SessionStatus.NOT_STARTED;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "session_data", columnDefinition = "jsonb")
    private Map<String, Object> sessionData;
    
    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "results", columnDefinition = "jsonb")
    private Map<String, Object> results;
    
    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("answeredAt ASC")
    @Builder.Default
    private List<SessionAnswer> sessionAnswers = new ArrayList<>();
    
    public enum SessionType {
        PRACTICE,
        QUIZ,
        EXAMINATION,
        ASSESSMENT,
        REVIEW
    }
    
    public enum SessionStatus {
        NOT_STARTED,
        IN_PROGRESS,
        PAUSED,
        COMPLETED,
        ABANDONED,
        TIMED_OUT
    }
    
    public void startSession() {
        this.startedAt = LocalDateTime.now();
        this.status = SessionStatus.IN_PROGRESS;
    }
    
    public void completeSession() {
        this.completedAt = LocalDateTime.now();
        this.status = SessionStatus.COMPLETED;
        calculateScore();
    }
    
    public void pauseSession() {
        this.status = SessionStatus.PAUSED;
    }
    
    public void resumeSession() {
        this.status = SessionStatus.IN_PROGRESS;
    }
    
    private void calculateScore() {
        if (totalQuestions > 0) {
            this.score = new BigDecimal(correctAnswers)
                .divide(new BigDecimal(totalQuestions), 2, BigDecimal.ROUND_HALF_UP)
                .multiply(new BigDecimal(100));
        }
    }
    
    public boolean isActive() {
        return status == SessionStatus.IN_PROGRESS || status == SessionStatus.PAUSED;
    }
    
    public boolean isTimeExpired() {
        if (timeLimitMinutes == null || startedAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(startedAt.plusMinutes(timeLimitMinutes));
    }
}