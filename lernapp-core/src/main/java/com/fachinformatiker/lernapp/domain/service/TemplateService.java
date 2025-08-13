package com.fachinformatiker.lernapp.domain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * TEMPLATE für Services - Claude Code kann dies als Vorlage nutzen
 * 
 * TODO für Phase 1.2 - Services zu implementieren:
 * 
 * 1. UserService
 *    - User createUser(UserDTO dto)
 *    - User updateUser(Long id, UserDTO dto)
 *    - void deleteUser(Long id)
 *    - User findById(Long id)
 *    - List<User> findAll(Pageable pageable)
 *    - User findByUsername(String username)
 *    - boolean changePassword(Long userId, String oldPassword, String newPassword)
 *    
 * 2. QuestionService
 *    - Question createQuestion(QuestionDTO dto)
 *    - Question updateQuestion(Long id, QuestionDTO dto)
 *    - List<Question> getQuestionsByTopic(Long topicId)
 *    - List<Question> getRandomQuestions(int count, DifficultyLevel level)
 *    - Question submitAnswer(Long questionId, Long userId, List<Long> answerIds)
 *    
 * 3. ProgressService
 *    - Progress trackProgress(Long userId, Long questionId, boolean correct)
 *    - ProgressStatistics getUserStatistics(Long userId)
 *    - List<Question> getQuestionsForReview(Long userId)
 *    - double calculateConfidenceLevel(Progress progress)
 *    - LocalDateTime calculateNextReviewDate(Progress progress)
 *    
 * 4. LearningService (Hauptservice für Lernlogik)
 *    - LearningSession createSession(Long userId, Long topicId)
 *    - void submitAnswer(Long sessionId, Long questionId, List<Long> answerIds)
 *    - LearningRecommendation getRecommendations(Long userId)
 *    - List<Question> getAdaptiveQuestions(Long userId, int count)
 *    
 * 5. ExaminationService
 *    - Examination createExamination(ExaminationDTO dto)
 *    - ExamSession startExam(Long examId, Long userId)
 *    - ExamResult submitExam(Long sessionId, Map<Long, List<Long>> answers)
 *    - List<ExamSession> getUserExamHistory(Long userId)
 *    
 * WICHTIGE HINWEISE:
 * - @Service Annotation
 * - @Transactional für Datenbankoperationen
 * - Constructor Injection verwenden (nicht @Autowired)
 * - Logging mit SLF4J
 * - Exception Handling mit eigenen Exceptions
 * - Validation der Eingaben
 * - DTOs für Input/Output verwenden
 * - Business Logic hier implementieren, nicht in Controller
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
// @Service - ENTFERNT damit Spring dieses Template nicht als echten Service registriert
// @Transactional
// Dies ist nur ein TEMPLATE zur Dokumentation!
public class TemplateService {
    
    // Dependencies via Constructor Injection
    
    // Service Methods hier...
}
