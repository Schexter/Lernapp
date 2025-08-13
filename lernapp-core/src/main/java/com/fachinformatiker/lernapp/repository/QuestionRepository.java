package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long>, JpaSpecificationExecutor<Question> {
    
    @Query("SELECT q FROM Question q LEFT JOIN FETCH q.answers WHERE q.id = :id")
    Optional<Question> findByIdWithAnswers(@Param("id") Long id);
    
    @Query("SELECT DISTINCT q FROM Question q LEFT JOIN FETCH q.answers WHERE q.id IN :ids")
    List<Question> findByIdsWithAnswers(@Param("ids") List<Long> ids);
    
    List<Question> findByTopicId(Long topicId);
    
    Page<Question> findByTopicId(Long topicId, Pageable pageable);
    
    List<Question> findByTopicIdAndActive(Long topicId, Boolean active);
    
    @Query("SELECT q FROM Question q WHERE q.topic.id IN :topicIds")
    List<Question> findByTopicIds(@Param("topicIds") List<Long> topicIds);
    
    List<Question> findByDifficultyLevel(Integer difficultyLevel);
    
    List<Question> findByQuestionType(Question.QuestionType questionType);
    
    @Query("SELECT q FROM Question q WHERE q.topic.id = :topicId AND q.difficultyLevel = :level")
    List<Question> findByTopicAndDifficulty(@Param("topicId") Long topicId, @Param("level") Integer level);
    
    @Query("SELECT q FROM Question q WHERE q.topic.id = :topicId AND q.difficultyLevel BETWEEN :minLevel AND :maxLevel")
    List<Question> findByTopicAndDifficultyRange(
        @Param("topicId") Long topicId, 
        @Param("minLevel") Integer minLevel, 
        @Param("maxLevel") Integer maxLevel
    );
    
    @Query("SELECT q FROM Question q WHERE LOWER(q.questionText) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<Question> searchByQuestionText(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT q FROM Question q WHERE q.createdBy.id = :userId")
    List<Question> findByCreatedBy(@Param("userId") Long userId);
    
    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t.name IN :tagNames")
    List<Question> findByTagNames(@Param("tagNames") List<String> tagNames);
    
    @Query("SELECT q FROM Question q JOIN q.tags t WHERE t.id IN :tagIds")
    List<Question> findByTagIds(@Param("tagIds") List<Long> tagIds);
    
    @Query("SELECT q FROM Question q WHERE q.active = true ORDER BY FUNCTION('RANDOM')")
    Page<Question> findRandomQuestions(Pageable pageable);
    
    @Query(value = "SELECT q.* FROM questions q " +
           "WHERE q.topic_id = :topicId AND q.active = true " +
           "ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByTopic(@Param("topicId") Long topicId, @Param("limit") int limit);
    
    @Query("SELECT q FROM Question q WHERE q.id NOT IN (" +
           "SELECT up.question.id FROM UserProgress up WHERE up.user.id = :userId" +
           ") AND q.topic.id = :topicId AND q.active = true")
    List<Question> findUnansweredQuestionsByUserAndTopic(
        @Param("userId") Long userId, 
        @Param("topicId") Long topicId
    );
    
    @Query("SELECT q FROM Question q JOIN UserProgress up ON q.id = up.question.id " +
           "WHERE up.user.id = :userId AND up.nextReview <= CURRENT_TIMESTAMP " +
           "ORDER BY up.nextReview ASC")
    List<Question> findQuestionsForReview(@Param("userId") Long userId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.id = :topicId")
    Long countByTopicId(@Param("topicId") Long topicId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.id = :topicId AND q.active = true")
    Long countActiveByTopicId(@Param("topicId") Long topicId);
    
    @Query("SELECT AVG(q.difficultyLevel) FROM Question q WHERE q.topic.id = :topicId")
    Double getAverageDifficultyByTopic(@Param("topicId") Long topicId);
    
    @Query("SELECT q.questionType, COUNT(q) FROM Question q WHERE q.topic.id = :topicId " +
           "GROUP BY q.questionType")
    List<Object[]> getQuestionTypeDistributionByTopic(@Param("topicId") Long topicId);
    
    @Query("SELECT q FROM Question q WHERE q.explanation IS NULL OR q.explanation = ''")
    List<Question> findQuestionsWithoutExplanation();
    
    @Query("SELECT q FROM Question q WHERE SIZE(q.answers) < 2")
    List<Question> findQuestionsWithInsufficientAnswers();
}