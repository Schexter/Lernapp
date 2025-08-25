package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {
    
    Optional<Tag> findByName(String name);
    
    Set<Tag> findByNameIn(Set<String> names);
    
    boolean existsByName(String name);
    
    @Query("SELECT t FROM Tag t WHERE SIZE(t.questions) > 0 ORDER BY SIZE(t.questions) DESC")
    List<Tag> findMostUsedTags();
    
    @Query("SELECT t, SIZE(t.questions) as usage FROM Tag t ORDER BY usage DESC")
    List<Object[]> findTagsWithUsageCount();
    
    @Query("SELECT t FROM Tag t WHERE LOWER(t.name) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<Tag> searchByName(String searchTerm);
}