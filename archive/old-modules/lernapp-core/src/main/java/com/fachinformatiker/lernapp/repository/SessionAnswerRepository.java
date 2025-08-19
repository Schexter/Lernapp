package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.SessionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SessionAnswerRepository extends JpaRepository<SessionAnswer, Long> {
    
    List<SessionAnswer> findBySessionId(Long sessionId);
    
    List<SessionAnswer> findBySessionIdOrderByAnsweredAtAsc(Long sessionId);
    
    Optional<SessionAnswer> findBySessionIdAndQuestionId(Long sessionId, Long questionId);
    
    @Query("SELECT sa FROM SessionAnswer sa WHERE sa.session.id = :sessionId AND sa.isCorrect = true")
    List<SessionAnswer> findCorrectAnswersBySessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT sa FROM SessionAnswer sa WHERE sa.session.id = :sessionId AND sa.markedForReview = true")
    List<SessionAnswer> findMarkedForReviewBySessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT COUNT(sa) FROM SessionAnswer sa WHERE sa.session.id = :sessionId")
    Long countBySessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT COUNT(sa) FROM SessionAnswer sa WHERE sa.session.id = :sessionId AND sa.isCorrect = true")
    Long countCorrectBySessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT AVG(sa.timeSpentSeconds) FROM SessionAnswer sa WHERE sa.session.id = :sessionId")
    Double getAverageTimeSpentBySessionId(@Param("sessionId") Long sessionId);
    
    @Query("SELECT sa.question.topic.id, COUNT(sa), " +
           "SUM(CASE WHEN sa.isCorrect = true THEN 1 ELSE 0 END) " +
           "FROM SessionAnswer sa WHERE sa.session.id = :sessionId " +
           "GROUP BY sa.question.topic.id")
    List<Object[]> getStatisticsByTopicForSession(@Param("sessionId") Long sessionId);
    
    void deleteBySessionId(Long sessionId);
}