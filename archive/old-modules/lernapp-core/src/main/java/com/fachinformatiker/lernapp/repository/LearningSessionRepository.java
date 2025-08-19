package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.LearningSession;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface LearningSessionRepository extends JpaRepository<LearningSession, Long> {
    
    /**
     * Find active sessions for a user
     */
    List<LearningSession> findByUserIdAndActive(Long userId, boolean active);
    
    /**
     * Find all sessions for a user
     */
    Page<LearningSession> findByUserId(Long userId, Pageable pageable);
    
    /**
     * Find sessions by user and topic
     */
    List<LearningSession> findByUserIdAndTopicId(Long userId, Long topicId);
    
    /**
     * Find completed sessions in a date range
     */
    @Query("SELECT ls FROM LearningSession ls WHERE ls.user.id = :userId " +
           "AND ls.completed = true AND ls.startTime BETWEEN :start AND :end")
    List<LearningSession> findCompletedSessionsInRange(
        @Param("userId") Long userId,
        @Param("start") LocalDateTime start,
        @Param("end") LocalDateTime end
    );
    
    /**
     * Count sessions by type for a user
     */
    @Query("SELECT ls.sessionType, COUNT(ls) FROM LearningSession ls " +
           "WHERE ls.user.id = :userId GROUP BY ls.sessionType")
    List<Object[]> countSessionsByType(@Param("userId") Long userId);
    
    /**
     * Get average score for a user
     */
    @Query("SELECT AVG(ls.score) FROM LearningSession ls " +
           "WHERE ls.user.id = :userId AND ls.completed = true")
    Double getAverageScore(@Param("userId") Long userId);
    
    /**
     * Get recent sessions
     */
    @Query("SELECT ls FROM LearningSession ls WHERE ls.user.id = :userId " +
           "ORDER BY ls.startTime DESC")
    Page<LearningSession> findRecentSessions(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * Find incomplete sessions older than specified time
     */
    @Query("SELECT ls FROM LearningSession ls WHERE ls.active = true " +
           "AND ls.startTime < :cutoffTime")
    List<LearningSession> findStaleSessions(@Param("cutoffTime") LocalDateTime cutoffTime);
}