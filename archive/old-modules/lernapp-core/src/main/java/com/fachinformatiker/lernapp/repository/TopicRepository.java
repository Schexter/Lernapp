package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TopicRepository extends JpaRepository<Topic, Long> {
    
    Optional<Topic> findByName(String name);
    
    List<Topic> findByParentIsNull();
    
    List<Topic> findByParentIsNullOrderBySortOrderAsc();
    
    List<Topic> findByParentId(Long parentId);
    
    List<Topic> findByParentIdOrderBySortOrderAsc(Long parentId);
    
    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.children WHERE t.id = :id")
    Optional<Topic> findByIdWithChildren(@Param("id") Long id);
    
    @Query("SELECT t FROM Topic t LEFT JOIN FETCH t.questions WHERE t.id = :id")
    Optional<Topic> findByIdWithQuestions(@Param("id") Long id);
    
    @Query("SELECT t FROM Topic t WHERE t.active = true ORDER BY t.sortOrder")
    List<Topic> findAllActive();
    
    @Query("SELECT t FROM Topic t WHERE t.parent IS NULL AND t.active = true ORDER BY t.sortOrder")
    List<Topic> findRootTopicsActive();
    
    @Query("SELECT t FROM Topic t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Topic> searchByName(@Param("searchTerm") String searchTerm);
    
    @Query("SELECT t FROM Topic t WHERE t.difficultyLevel = :level")
    List<Topic> findByDifficultyLevel(@Param("level") Integer level);
    
    @Query("SELECT t FROM Topic t WHERE t.difficultyLevel BETWEEN :minLevel AND :maxLevel")
    List<Topic> findByDifficultyRange(@Param("minLevel") Integer minLevel, @Param("maxLevel") Integer maxLevel);
    
    @Query("SELECT t FROM Topic t JOIN t.learningPaths lp WHERE lp.id = :pathId ORDER BY t.sortOrder")
    List<Topic> findByLearningPathId(@Param("pathId") Long pathId);
    
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.id = :topicId")
    Long countQuestionsByTopicId(@Param("topicId") Long topicId);
    
    @Query("SELECT t.id, t.name, COUNT(q) FROM Topic t " +
           "LEFT JOIN Question q ON t.id = q.topic.id " +
           "GROUP BY t.id, t.name " +
           "ORDER BY COUNT(q) DESC")
    List<Object[]> getTopicsWithQuestionCount();
    
    @Query("SELECT t FROM Topic t WHERE SIZE(t.questions) > 0")
    List<Topic> findTopicsWithQuestions();
    
    @Query("SELECT t FROM Topic t WHERE SIZE(t.questions) = 0 AND SIZE(t.children) = 0")
    List<Topic> findEmptyLeafTopics();
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
}