package com.fachinformatiker.lernapp.domain.repository;

import com.fachinformatiker.lernapp.domain.enums.DifficultyLevel;
import com.fachinformatiker.lernapp.domain.enums.QuestionType;
import com.fachinformatiker.lernapp.domain.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository für Question-Entity.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {

    /**
     * Findet alle Fragen eines Topics.
     */
    List<Question> findByTopicId(Long topicId);

    /**
     * Findet Fragen nach Schwierigkeitsgrad.
     */
    List<Question> findByDifficultyLevel(DifficultyLevel difficultyLevel);

    /**
     * Findet Fragen nach Typ.
     */
    List<Question> findByQuestionType(QuestionType questionType);

    /**
     * Findet alle aktiven Fragen eines Topics.
     */
    List<Question> findByTopicIdAndActiveTrue(Long topicId);

    /**
     * Sucht Fragen nach Text.
     */
    @Query("SELECT q FROM Question q WHERE " +
           "LOWER(q.questionText) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(q.explanation) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Question> searchQuestions(@Param("searchTerm") String searchTerm, Pageable pageable);

    /**
     * Findet zufällige Fragen für ein Topic.
     */
    @Query(value = "SELECT * FROM questions WHERE topic_id = :topicId AND active = true " +
                   "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByTopic(@Param("topicId") Long topicId, @Param("limit") int limit);

    /**
     * Findet Fragen die noch nie beantwortet wurden von einem User.
     */
    @Query("SELECT q FROM Question q WHERE q.topic.id = :topicId AND " +
           "q.id NOT IN (SELECT p.question.id FROM Progress p WHERE p.user.id = :userId)")
    List<Question> findUnansweredQuestions(@Param("userId") Long userId, @Param("topicId") Long topicId);

    /**
     * Statistik: Durchschnittliche Erfolgsrate pro Topic.
     */
    @Query("SELECT q.topic.id, AVG(CAST(q.timesCorrect AS double) / CAST(q.timesAnswered AS double)) " +
           "FROM Question q WHERE q.timesAnswered > 0 GROUP BY q.topic.id")
    List<Object[]> getAverageSuccessRateByTopic();

    /**
     * Findet die schwierigsten Fragen (niedrigste Erfolgsrate).
     */
    @Query("SELECT q FROM Question q WHERE q.timesAnswered > 10 " +
           "ORDER BY (CAST(q.timesCorrect AS double) / CAST(q.timesAnswered AS double)) ASC")
    Page<Question> findMostDifficultQuestions(Pageable pageable);

    /**
     * Findet Premium-Fragen.
     */
    List<Question> findByIsPremiumTrue();

    /**
     * Zählt Fragen pro Topic.
     */
    @Query("SELECT q.topic.id, COUNT(q) FROM Question q GROUP BY q.topic.id")
    List<Object[]> countQuestionsByTopic();
}
