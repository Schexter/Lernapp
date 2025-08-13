package com.fachinformatiker.lernapp.domain.model;

import jakarta.persistence.*;
import lombok.*;

/**
 * TEMPLATE für Domain Models - Claude Code kann dies als Vorlage nutzen
 * 
 * TODO für Phase 1.2 - Domain Models zu implementieren:
 * 
 * 1. User Entity
 *    - username, email, password
 *    - firstName, lastName
 *    - role (UserRole enum)
 *    - learningProgress (OneToMany zu Progress)
 *    - preferences (JSON)
 *    
 * 2. Topic Entity
 *    - name, description
 *    - parentTopic (ManyToOne self-reference)
 *    - subTopics (OneToMany self-reference)
 *    - questions (OneToMany zu Question)
 *    - sortOrder
 *    
 * 3. Question Entity
 *    - questionText, explanation
 *    - questionType (QuestionType enum)
 *    - difficultyLevel (DifficultyLevel enum)
 *    - topic (ManyToOne zu Topic)
 *    - answers (OneToMany zu Answer)
 *    - tags (ManyToMany zu Tag)
 *    - imageUrl, codeSnippet (optional)
 *    
 * 4. Answer Entity
 *    - answerText
 *    - isCorrect
 *    - explanation
 *    - question (ManyToOne zu Question)
 *    - sortOrder
 *    
 * 5. LearningPath Entity
 *    - name, description
 *    - user (ManyToOne zu User)
 *    - topics (ManyToMany zu Topic)
 *    - targetCompletionDate
 *    - progressPercentage
 *    
 * 6. Progress Entity
 *    - user (ManyToOne zu User)
 *    - question (ManyToOne zu Question)
 *    - attempts, correctAttempts
 *    - lastAttempt, nextReview (LocalDateTime)
 *    - confidenceLevel (0.0 - 1.0)
 *    - reviewCount
 *    
 * 7. Examination Entity
 *    - name, description
 *    - createdBy (ManyToOne zu User)
 *    - questions (ManyToMany zu Question)
 *    - timeLimit (in Minuten)
 *    - passingScore (in Prozent)
 *    
 * 8. ExamSession Entity
 *    - examination (ManyToOne zu Examination)
 *    - user (ManyToOne zu User)
 *    - startTime, endTime
 *    - score, passed
 *    - answers (JSON mit Antworten)
 *    
 * 9. Tag Entity
 *    - name
 *    - questions (ManyToMany zu Question)
 *    
 * 10. Permission Entity (für fein-granulare Rechteverwaltung)
 *     - name, description
 *     - resource, action
 *     - roles (ManyToMany zu Role)
 * 
 * WICHTIGE HINWEISE:
 * - Alle Entities erben von BaseEntity
 * - Lombok Annotations nutzen (@Data, @Builder, @NoArgsConstructor, @AllArgsConstructor)
 * - JPA Annotations korrekt setzen (@Entity, @Table, @Column)
 * - Bidirektionale Beziehungen mit mappedBy
 * - Cascade-Types sinnvoll wählen
 * - Fetch-Types optimieren (LAZY vs EAGER)
 * - Indexes für häufige Queries definieren
 * - Validation Annotations nutzen (@NotNull, @Size, @Email)
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
// @Entity - ENTFERNT damit Spring dieses Template nicht als echte Entity registriert
// @Table(name = "template_entity")
// Dies ist nur ein TEMPLATE zur Dokumentation!
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TemplateEntity extends BaseEntity {
    
    @Column(name = "name", nullable = false, length = 255)
    private String name;
    
    // Weitere Felder hier...
}
