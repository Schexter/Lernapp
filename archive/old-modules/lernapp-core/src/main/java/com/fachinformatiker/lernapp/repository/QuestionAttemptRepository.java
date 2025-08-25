package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.QuestionAttempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface QuestionAttemptRepository extends JpaRepository<QuestionAttempt, Long> {
    
    /**
     * Find attempts by learning session
     */
    List<QuestionAttempt> findByLearningSessionId(Long sessionId);
    
    /**
     * Find attempts by user
     */
    List<QuestionAttempt> findByUserId(Long userId);
    
    /**
     * Find attempts by user and question
     */
    List<QuestionAttempt> findByUserIdAndQuestionId(Long userId, Long questionId);
    
    /**
     * Count correct attempts for a user
     */
    @Query("SELECT COUNT(qa) FROM QuestionAttempt qa WHERE qa.user.id = :userId AND qa.correct = true")
    Long countCorrectByUserId(@Param("userId") Long userId);
    
    /**
     * Count total attempts for a user
     */
    @Query("SELECT COUNT(qa) FROM QuestionAttempt qa WHERE qa.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    /**
     * Find recent attempts
     */
    @Query("SELECT qa FROM QuestionAttempt qa WHERE qa.user.id = :userId " +
           "AND qa.attemptTime >= :since ORDER BY qa.attemptTime DESC")
    List<QuestionAttempt> findRecentAttempts(
        @Param("userId") Long userId,
        @Param("since") LocalDateTime since
    );
    
    /**
     * Get average response time for a user
     */
    @Query("SELECT AVG(qa.responseTime) FROM QuestionAttempt qa " +
           "WHERE qa.user.id = :userId AND qa.responseTime IS NOT NULL")
    Double getAverageResponseTime(@Param("userId") Long userId);
    
    /**
     * Find flagged questions
     */
    @Query("SELECT qa FROM QuestionAttempt qa WHERE qa.user.id = :userId AND qa.flagged = true")
    List<QuestionAttempt> findFlaggedQuestions(@Param("userId") Long userId);
}