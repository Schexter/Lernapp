package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.ExaminationSession;
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
public interface ExaminationSessionRepository extends JpaRepository<ExaminationSession, Long> {
    
    List<ExaminationSession> findByUserId(Long userId);
    
    Page<ExaminationSession> findByUserId(Long userId, Pageable pageable);
    
    List<ExaminationSession> findByUserIdAndStatus(
        Long userId, 
        ExaminationSession.SessionStatus status
    );
    
    List<ExaminationSession> findByUserIdAndSessionType(
        Long userId,
        ExaminationSession.SessionType sessionType
    );
    
    @Query("SELECT es FROM ExaminationSession es WHERE es.user.id = :userId " +
           "AND es.status IN ('IN_PROGRESS', 'PAUSED')")
    Optional<ExaminationSession> findActiveSessionByUser(@Param("userId") Long userId);
    
    @Query("SELECT es FROM ExaminationSession es WHERE es.user.id = :userId " +
           "AND es.completedAt BETWEEN :startDate AND :endDate")
    List<ExaminationSession> findCompletedSessionsInPeriod(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate,
        @Param("endDate") LocalDateTime endDate
    );
    
    @Query("SELECT es FROM ExaminationSession es WHERE es.user.id = :userId " +
           "AND es.topicIds LIKE CONCAT('%', :topicId, '%')")
    List<ExaminationSession> findByUserAndTopic(
        @Param("userId") Long userId,
        @Param("topicId") String topicId
    );
    
    @Query("SELECT es FROM ExaminationSession es WHERE es.user.id = :userId " +
           "ORDER BY es.startedAt DESC")
    Page<ExaminationSession> findRecentSessions(@Param("userId") Long userId, Pageable pageable);
    
    @Query("SELECT AVG(es.score) FROM ExaminationSession es " +
           "WHERE es.user.id = :userId AND es.status = 'COMPLETED'")
    Double getAverageScoreByUser(@Param("userId") Long userId);
    
    @Query("SELECT AVG(es.score) FROM ExaminationSession es " +
           "WHERE es.user.id = :userId AND es.sessionType = :type AND es.status = 'COMPLETED'")
    Double getAverageScoreByUserAndType(
        @Param("userId") Long userId,
        @Param("type") ExaminationSession.SessionType type
    );
    
    @Query("SELECT COUNT(es) FROM ExaminationSession es " +
           "WHERE es.user.id = :userId AND es.status = 'COMPLETED'")
    Long countCompletedSessionsByUser(@Param("userId") Long userId);
    
    @Query("SELECT es.sessionType, COUNT(es), AVG(es.score) " +
           "FROM ExaminationSession es " +
           "WHERE es.user.id = :userId AND es.status = 'COMPLETED' " +
           "GROUP BY es.sessionType")
    List<Object[]> getSessionStatisticsByType(@Param("userId") Long userId);
    
    @Query("SELECT DATE(es.completedAt), COUNT(es), AVG(es.score) " +
           "FROM ExaminationSession es " +
           "WHERE es.user.id = :userId AND es.status = 'COMPLETED' " +
           "AND es.completedAt >= :startDate " +
           "GROUP BY DATE(es.completedAt) " +
           "ORDER BY DATE(es.completedAt)")
    List<Object[]> getDailySessionStats(
        @Param("userId") Long userId,
        @Param("startDate") LocalDateTime startDate
    );
    
    @Query("SELECT es FROM ExaminationSession es " +
           "WHERE es.status = 'IN_PROGRESS' " +
           "AND es.startedAt < :timeoutThreshold")
    List<ExaminationSession> findTimedOutSessions(@Param("timeoutThreshold") LocalDateTime timeoutThreshold);
    
    @Query("SELECT es FROM ExaminationSession es " +
           "WHERE es.user.id = :userId AND es.score >= :minScore " +
           "AND es.status = 'COMPLETED' " +
           "ORDER BY es.score DESC")
    List<ExaminationSession> findHighScoreSessions(
        @Param("userId") Long userId,
        @Param("minScore") Double minScore
    );
}