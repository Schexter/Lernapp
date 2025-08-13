package com.fachinformatiker.lernapp.domain.repository;

import com.fachinformatiker.lernapp.domain.model.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository für Topic-Entity.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Repository
public interface TopicRepository extends JpaRepository<Topic, Long>, JpaSpecificationExecutor<Topic> {

    /**
     * Findet alle Root-Topics (ohne Parent).
     */
    List<Topic> findByParentTopicIsNullOrderBySortOrderAsc();

    /**
     * Findet alle Sub-Topics eines Parents.
     */
    List<Topic> findByParentTopicIdOrderBySortOrderAsc(Long parentId);

    /**
     * Findet ein Topic anhand des Namens.
     */
    Optional<Topic> findByName(String name);

    /**
     * Findet alle aktiven Topics.
     */
    List<Topic> findByActiveTrueOrderBySortOrderAsc();

    /**
     * Findet alle Premium-Topics.
     */
    List<Topic> findByIsPremiumTrue();

    /**
     * Sucht Topics nach Name oder Beschreibung.
     */
    @Query("SELECT t FROM Topic t WHERE " +
           "LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR " +
           "LOWER(t.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Topic> searchTopics(@Param("searchTerm") String searchTerm);

    /**
     * Findet Topics mit mindestens X Fragen.
     */
    @Query("SELECT t FROM Topic t WHERE SIZE(t.questions) >= :minQuestions")
    List<Topic> findTopicsWithMinimumQuestions(@Param("minQuestions") int minQuestions);

    /**
     * Zählt die Gesamtanzahl der Fragen in einem Topic (inkl. Sub-Topics).
     */
    @Query("SELECT COUNT(q) FROM Question q WHERE q.topic.id = :topicId OR " +
           "q.topic.id IN (SELECT st.id FROM Topic st WHERE st.parentTopic.id = :topicId)")
    Long countTotalQuestions(@Param("topicId") Long topicId);

    /**
     * Findet den kompletten Topic-Baum.
     */
    @Query("SELECT DISTINCT t FROM Topic t LEFT JOIN FETCH t.subTopics WHERE t.parentTopic IS NULL")
    List<Topic> findAllWithSubTopics();
}
