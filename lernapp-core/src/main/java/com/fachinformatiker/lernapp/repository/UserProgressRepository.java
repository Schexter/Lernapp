package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.UserProgress;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserProgressRepository extends JpaRepository<UserProgress, Long> {
    
    Optional<UserProgress> findByUserIdAndQuestionId(Long userId, Long questionId);
    
    List<UserProgress> findByUserId(Long userId);
    
    Page<UserProgress> findByUserId(Long userId, Pageable pageable);
    
    List<UserProgress> findByQuestionId(Long questionId);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId AND up.question.topic.id = :topicId")
    List<UserProgress> findByUserIdAndTopicId(@Param("userId") Long userId, @Param("topicId") Long topicId);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId " +
           "AND up.nextReview <= :now ORDER BY up.nextReview ASC")
    List<UserProgress> findDueForReview(@Param("userId") Long userId, @Param("now") LocalDateTime now);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId " +
           "AND up.nextReview BETWEEN :start AND :end ORDER BY up.nextReview ASC")
    List<UserProgress> findDueForReviewBetween(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId " +
           "AND up.masteryLevel = :level")
    List<UserProgress> findByUserAndMasteryLevel(
        @Param("userId") Long userId,
        @Param("level") UserProgress.MasteryLevel level
    );
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId " +
           "AND up.confidenceLevel >= :minConfidence")
    List<UserProgress> findByUserAndMinConfidence(
        @Param("userId") Long userId,
        @Param("minConfidence") BigDecimal minConfidence
    );
    
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user.id = :userId")
    Long countByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(up) FROM UserProgress up WHERE up.user.id = :userId " +
           "AND up.masteryLevel = 'MASTERED'")
    Long countMasteredByUserId(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(DISTINCT up.question.topic.id) FROM UserProgress up " +
           "WHERE up.user.id = :userId AND up.attempts > 0")
    Long countTopicsStartedByUser(@Param("userId") Long userId);
    
    @Query("SELECT AVG(up.confidenceLevel) FROM UserProgress up WHERE up.user.id = :userId")
    BigDecimal getAverageConfidenceByUser(@Param("userId") Long userId);
    
    @Query("SELECT AVG(up.confidenceLevel) FROM UserProgress up " +
           "WHERE up.user.id = :userId AND up.question.topic.id = :topicId")
    BigDecimal getAverageConfidenceByUserAndTopic(
        @Param("userId") Long userId,
        @Param("topicId") Long topicId
    );
    
    @Query("SELECT SUM(up.timeSpentSeconds) FROM UserProgress up WHERE up.user.id = :userId")
    Long getTotalTimeSpentByUser(@Param("userId") Long userId);
    
    @Query("SELECT up.question.topic.id, up.question.topic.name, " +
           "COUNT(up), AVG(up.confidenceLevel), SUM(up.correctAttempts), SUM(up.attempts) " +
           "FROM UserProgress up WHERE up.user.id = :userId " +
           "GROUP BY up.question.topic.id, up.question.topic.name")
    List<Object[]> getUserStatisticsByTopic(@Param("userId") Long userId);
    
    @Query("SELECT up.masteryLevel, COUNT(up) FROM UserProgress up " +
           "WHERE up.user.id = :userId GROUP BY up.masteryLevel")
    List<Object[]> getMasteryLevelDistribution(@Param("userId") Long userId);
    
    @Query("SELECT DATE(up.lastAttempt), COUNT(DISTINCT up.question.id) " +
           "FROM UserProgress up WHERE up.user.id = :userId " +
           "AND up.lastAttempt >= :startDate " +
           "GROUP BY DATE(up.lastAttempt) ORDER BY DATE(up.lastAttempt)")
    List<Object[]> getDailyActivityStats(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate
    );
    
    @Modifying
    @Query("UPDATE UserProgress up SET up.nextReview = :nextReview, " +
           "up.repetitionInterval = :interval, up.easeFactor = :easeFactor " +
           "WHERE up.id = :progressId")
    void updateSpacedRepetitionData(
        @Param("progressId") Long progressId,
        @Param("nextReview") LocalDateTime nextReview,
        @Param("interval") Integer interval,
        @Param("easeFactor") BigDecimal easeFactor
    );
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId " +
           "ORDER BY up.lastAttempt DESC")
    Page<UserProgress> findRecentActivityByUser(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT up FROM UserProgress up WHERE up.user.id = :userId " +
           "AND up.consecutiveCorrect >= :minStreak")
    List<UserProgress> findByUserWithStreak(
        @Param("userId") Long userId,
        @Param("minStreak") Integer minStreak
    );
    
    void deleteByUserIdAndQuestionId(Long userId, Long questionId);
}