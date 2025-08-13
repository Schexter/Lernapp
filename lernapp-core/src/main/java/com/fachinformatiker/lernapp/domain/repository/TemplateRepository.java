package com.fachinformatiker.lernapp.domain.repository;

import com.fachinformatiker.lernapp.domain.model.TemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * TEMPLATE für Repositories - Claude Code kann dies als Vorlage nutzen
 * 
 * TODO für Phase 1.2 - Repositories zu implementieren:
 * 
 * 1. UserRepository
 *    - Optional<User> findByUsername(String username)
 *    - Optional<User> findByEmail(String email)
 *    - List<User> findByRole(UserRole role)
 *    - boolean existsByUsername(String username)
 *    - boolean existsByEmail(String email)
 *    
 * 2. QuestionRepository
 *    - List<Question> findByTopicId(Long topicId)
 *    - List<Question> findByDifficultyLevel(DifficultyLevel level)
 *    - List<Question> findByQuestionType(QuestionType type)
 *    - @Query für komplexe Suchen mit Pagination
 *    
 * 3. ProgressRepository
 *    - List<Progress> findByUserId(Long userId)
 *    - Optional<Progress> findByUserIdAndQuestionId(Long userId, Long questionId)
 *    - @Query für Lernstatistiken
 *    
 * 4. TopicRepository
 *    - List<Topic> findByParentTopicIsNull() // Root-Topics
 *    - List<Topic> findByParentTopicId(Long parentId)
 *    
 * 5. ExaminationRepository
 *    - List<Examination> findByCreatedById(Long userId)
 *    - @Query für aktive Prüfungen
 *    
 * WICHTIGE HINWEISE:
 * - JpaRepository<Entity, ID> erweitern
 * - JpaSpecificationExecutor für komplexe Queries
 * - @Repository Annotation
 * - Custom Query Methods mit @Query wenn nötig
 * - Pagination Support mit Pageable
 * - @Modifying für Update/Delete Queries
 * - @Transactional wo nötig
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
// @Repository - ENTFERNT damit Spring dieses Template nicht als echtes Repository registriert
// Dies ist nur ein TEMPLATE zur Dokumentation!
public interface TemplateRepository extends JpaRepository<TemplateEntity, Long>, 
                                           JpaSpecificationExecutor<TemplateEntity> {
    
    // Custom Query Methods hier...
}
