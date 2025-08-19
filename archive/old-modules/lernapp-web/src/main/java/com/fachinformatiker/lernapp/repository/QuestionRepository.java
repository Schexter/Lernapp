package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.Question;
import com.fachinformatiker.lernapp.model.Question.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository für Question-Entity
 * Bietet Datenbankzugriff für Fragen
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Finde Fragen nach Kategorie
    List<Question> findByCategory(String category);
    
    // Finde Fragen nach Schwierigkeit
    List<Question> findByDifficulty(Difficulty difficulty);
    
    // Finde Fragen nach Kategorie und Schwierigkeit
    List<Question> findByCategoryAndDifficulty(String category, Difficulty difficulty);
    
    // Zufällige Fragen (Native Query für bessere Performance)
    @Query(value = "SELECT * FROM questions WHERE category = :category ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCategory(@Param("category") String category, @Param("limit") int limit);
    
    // Zufällige Fragen mit Schwierigkeit
    @Query(value = "SELECT * FROM questions WHERE category = :category AND difficulty = :difficulty ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestionsByCategoryAndDifficulty(
        @Param("category") String category, 
        @Param("difficulty") String difficulty, 
        @Param("limit") int limit
    );
    
    // Alle Kategorien abrufen
    @Query("SELECT DISTINCT q.category FROM Question q")
    List<String> findAllCategories();
    
    // Anzahl Fragen pro Kategorie
    @Query("SELECT q.category, COUNT(q) FROM Question q GROUP BY q.category")
    List<Object[]> countQuestionsByCategory();
}
