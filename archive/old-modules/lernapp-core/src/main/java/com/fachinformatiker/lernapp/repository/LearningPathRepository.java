package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.LearningPath;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LearningPathRepository extends JpaRepository<LearningPath, Long> {
    
    Optional<LearningPath> findByName(String name);
    
    List<LearningPath> findByActiveTrue();
    
    Page<LearningPath> findByActiveTrue(Pageable pageable);
    
    List<LearningPath> findByDifficultyLevel(Integer difficultyLevel);
    
    List<LearningPath> findByCertificationAvailable(Boolean certificationAvailable);
    
    @Query("SELECT lp FROM LearningPath lp LEFT JOIN FETCH lp.topics WHERE lp.id = :id")
    Optional<LearningPath> findByIdWithTopics(@Param("id") Long id);
    
    @Query("SELECT lp FROM LearningPath lp WHERE lp.difficultyLevel BETWEEN :minLevel AND :maxLevel")
    List<LearningPath> findByDifficultyRange(
        @Param("minLevel") Integer minLevel,
        @Param("maxLevel") Integer maxLevel
    );
    
    @Query("SELECT lp FROM LearningPath lp WHERE LOWER(lp.name) LIKE LOWER(CONCAT('%', :searchTerm, '%')) " +
           "OR LOWER(lp.description) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    Page<LearningPath> searchPaths(@Param("searchTerm") String searchTerm, Pageable pageable);
    
    @Query("SELECT lp FROM LearningPath lp JOIN lp.enrolledUsers u WHERE u.id = :userId")
    List<LearningPath> findEnrolledPathsByUser(@Param("userId") Long userId);
    
    @Query("SELECT lp FROM LearningPath lp WHERE lp.createdBy.id = :userId")
    List<LearningPath> findByCreatedBy(@Param("userId") Long userId);
    
    @Query("SELECT lp FROM LearningPath lp JOIN lp.topics t WHERE t.id = :topicId")
    List<LearningPath> findByTopicId(@Param("topicId") Long topicId);
    
    @Query("SELECT lp FROM LearningPath lp WHERE lp.estimatedHours <= :maxHours")
    List<LearningPath> findByMaxDuration(@Param("maxHours") Integer maxHours);
    
    @Query("SELECT lp, COUNT(u) as enrollmentCount FROM LearningPath lp " +
           "LEFT JOIN lp.enrolledUsers u " +
           "GROUP BY lp " +
           "ORDER BY COUNT(u) DESC")
    Page<Object[]> findMostPopularPaths(Pageable pageable);
    
    @Query("SELECT COUNT(u) FROM LearningPath lp JOIN lp.enrolledUsers u WHERE lp.id = :pathId")
    Long countEnrolledUsers(@Param("pathId") Long pathId);
    
    @Query("SELECT lp FROM LearningPath lp WHERE lp.id NOT IN " +
           "(SELECT lp2.id FROM LearningPath lp2 JOIN lp2.enrolledUsers u WHERE u.id = :userId) " +
           "AND lp.active = true")
    List<LearningPath> findAvailablePathsForUser(@Param("userId") Long userId);
    
    @Query("SELECT lp FROM LearningPath lp ORDER BY lp.createdAt DESC")
    Page<LearningPath> findNewestPaths(Pageable pageable);
    
    boolean existsByName(String name);
    
    boolean existsByNameAndIdNot(String name, Long id);
}