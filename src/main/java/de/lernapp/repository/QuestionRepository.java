package de.lernapp.repository;

import de.lernapp.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository für Datenbankzugriffe auf Questions
 * 
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {
    
    // Finde Fragen nach Kategorie
    List<Question> findByCategory(String category);
    
    // Finde Fragen nach Schwierigkeitsgrad
    List<Question> findByDifficulty(Integer difficulty);
    
    // Finde Fragen nach Kategorie und Schwierigkeitsgrad
    List<Question> findByCategoryAndDifficulty(String category, Integer difficulty);
    
    // Hole zufällige Fragen für einen Test
    @Query(value = "SELECT * FROM questions ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Question> findRandomQuestions(@Param("limit") int limit);
    
    // Hole alle unterschiedlichen Kategorien
    @Query("SELECT DISTINCT q.category FROM Question q")
    List<String> findAllCategories();
    
    // Für Import-Statistiken
    @Query("SELECT q.category, COUNT(q) FROM Question q GROUP BY q.category")
    List<Object[]> countByCategory();
    
    // Zähle Fragen nach Schwierigkeit
    long countByDifficulty(Integer difficulty);
}
