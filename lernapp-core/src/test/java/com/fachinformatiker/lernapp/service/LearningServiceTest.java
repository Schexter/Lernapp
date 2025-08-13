package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.domain.model.*;
import com.fachinformatiker.lernapp.domain.repository.*;
import com.fachinformatiker.lernapp.dto.LearningSessionDTO;
import com.fachinformatiker.lernapp.dto.ProgressDTO;
import com.fachinformatiker.lernapp.dto.QuestionStatisticsDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

/**
 * Unit Tests für LearningService
 * Testet die komplexe Lernlogik inklusive Spaced Repetition Algorithm
 * 
 * @author Hans Hahn
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("LearningService Tests")
class LearningServiceTest {

    @Mock
    private UserRepository userRepository;
    
    @Mock
    private QuestionRepository questionRepository;
    
    @Mock
    private ProgressRepository progressRepository;
    
    @Mock
    private TopicRepository topicRepository;
    
    @Mock
    private ExaminationRepository examinationRepository;
    
    @Mock
    private LearningSessionRepository learningSessionRepository;
    
    @Captor
    private ArgumentCaptor<Progress> progressCaptor;
    
    @Captor
    private ArgumentCaptor<LearningSession> sessionCaptor;
    
    @InjectMocks
    private LearningService learningService;
    
    private User testUser;
    private Topic testTopic;
    private Question testQuestion;
    private Answer correctAnswer;
    private Answer wrongAnswer;
    private Progress testProgress;
    
    @BeforeEach
    void setUp() {
        // Test-User erstellen
        testUser = new User();
        testUser.setId(1L);
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        
        // Test-Topic erstellen
        testTopic = new Topic();
        testTopic.setId(1L);
        testTopic.setName("Java Grundlagen");
        testTopic.setDescription("Basics of Java Programming");
        
        // Test-Question erstellen
        testQuestion = new Question();
        testQuestion.setId(1L);
        testQuestion.setTopic(testTopic);
        testQuestion.setQuestionText("Was ist Java?");
        testQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        testQuestion.setDifficultyLevel(DifficultyLevel.BEGINNER);
        
        // Test-Answers erstellen
        correctAnswer = new Answer();
        correctAnswer.setId(1L);
        correctAnswer.setQuestion(testQuestion);
        correctAnswer.setAnswerText("Eine objektorientierte Programmiersprache");
        correctAnswer.setCorrect(true);
        
        wrongAnswer = new Answer();
        wrongAnswer.setId(2L);
        wrongAnswer.setQuestion(testQuestion);
        wrongAnswer.setAnswerText("Ein Kaffeegetränk");
        wrongAnswer.setCorrect(false);
        
        testQuestion.setAnswers(Set.of(correctAnswer, wrongAnswer));
        
        // Test-Progress erstellen
        testProgress = new Progress();
        testProgress.setId(1L);
        testProgress.setUser(testUser);
        testProgress.setQuestion(testQuestion);
        testProgress.setAttempts(0);
        testProgress.setCorrectAttempts(0);
        testProgress.setConfidenceLevel(0.0);
    }
    
    @Test
    @DisplayName("Sollte neue Lernsession erstellen")
    void shouldCreateLearningSession() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(questionRepository.findByTopicId(1L)).thenReturn(List.of(testQuestion));
        when(progressRepository.findByUserIdAndQuestionId(1L, 1L))
            .thenReturn(Optional.empty());
        when(learningSessionRepository.save(any(LearningSession.class)))
            .thenAnswer(invocation -> {
                LearningSession session = invocation.getArgument(0);
                session.setId(1L);
                return session;
            });
        
        // When
        LearningSessionDTO sessionDTO = learningService.createLearningSession(1L, 1L);
        
        // Then
        assertAll(
            () -> assertThat(sessionDTO).isNotNull(),
            () -> assertThat(sessionDTO.getQuestions()).hasSize(1),
            () -> assertThat(sessionDTO.getQuestions().get(0).getId()).isEqualTo(1L),
            () -> assertThat(sessionDTO.getTotalQuestions()).isEqualTo(1)
        );
        
        verify(learningSessionRepository).save(sessionCaptor.capture());
        LearningSession savedSession = sessionCaptor.getValue();
        assertThat(savedSession.getUser()).isEqualTo(testUser);
        assertThat(savedSession.getTopic()).isEqualTo(testTopic);
    }
    
    @Test
    @DisplayName("Sollte Antwort verarbeiten und Progress aktualisieren")
    void shouldSubmitAnswerAndUpdateProgress() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(progressRepository.findByUserIdAndQuestionId(1L, 1L))
            .thenReturn(Optional.of(testProgress));
        when(progressRepository.save(any(Progress.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When - Richtige Antwort
        boolean isCorrect = learningService.submitAnswer(1L, 1L, correctAnswer.getId());
        
        // Then
        assertThat(isCorrect).isTrue();
        verify(progressRepository).save(progressCaptor.capture());
        Progress updatedProgress = progressCaptor.getValue();
        
        assertAll(
            () -> assertThat(updatedProgress.getAttempts()).isEqualTo(1),
            () -> assertThat(updatedProgress.getCorrectAttempts()).isEqualTo(1),
            () -> assertThat(updatedProgress.getConfidenceLevel()).isGreaterThan(0.0),
            () -> assertThat(updatedProgress.getNextReview()).isNotNull()
        );
    }
    
    @Test
    @DisplayName("Sollte Spaced Repetition Algorithm anwenden")
    void shouldApplySpacedRepetitionAlgorithm() {
        // Given - Progress mit verschiedenen Confidence Levels
        Progress lowConfidence = createProgressWithConfidence(0.2);
        Progress mediumConfidence = createProgressWithConfidence(0.5);
        Progress highConfidence = createProgressWithConfidence(0.8);
        
        when(progressRepository.save(any(Progress.class)))
            .thenAnswer(invocation -> invocation.getArgument(0));
        
        // When
        LocalDateTime lowReview = learningService.calculateNextReview(lowConfidence, true);
        LocalDateTime mediumReview = learningService.calculateNextReview(mediumConfidence, true);
        LocalDateTime highReview = learningService.calculateNextReview(highConfidence, true);
        
        // Then - Je höher die Confidence, desto später die nächste Review
        assertAll(
            () -> assertThat(lowReview).isBefore(mediumReview),
            () -> assertThat(mediumReview).isBefore(highReview),
            () -> assertThat(lowReview).isAfter(LocalDateTime.now()),
            () -> assertThat(highReview).isAfter(LocalDateTime.now().plusDays(7))
        );
    }
    
    @Test
    @DisplayName("Sollte User Progress Statistics berechnen")
    void shouldCalculateUserProgressStatistics() {
        // Given
        List<Progress> progressList = Arrays.asList(
            createProgressWithStats(10, 8, 0.8),
            createProgressWithStats(5, 3, 0.6),
            createProgressWithStats(20, 15, 0.75)
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(progressRepository.findByUserId(1L)).thenReturn(progressList);
        
        // When
        ProgressDTO stats = learningService.getUserProgressStatistics(1L);
        
        // Then
        assertAll(
            () -> assertThat(stats.getTotalAttempts()).isEqualTo(35),
            () -> assertThat(stats.getCorrectAttempts()).isEqualTo(26),
            () -> assertThat(stats.getAverageConfidence()).isEqualTo(0.72), // (0.8+0.6+0.75)/3
            () -> assertThat(stats.getQuestionCount()).isEqualTo(3)
        );
    }
    
    @Test
    @DisplayName("Sollte schwache Themen identifizieren")
    void shouldIdentifyWeakTopics() {
        // Given
        Topic weakTopic = new Topic();
        weakTopic.setId(2L);
        weakTopic.setName("Netzwerke");
        
        Question weakQuestion = new Question();
        weakQuestion.setId(2L);
        weakQuestion.setTopic(weakTopic);
        
        Progress weakProgress = new Progress();
        weakProgress.setQuestion(weakQuestion);
        weakProgress.setConfidenceLevel(0.3);
        weakProgress.setAttempts(10);
        weakProgress.setCorrectAttempts(3);
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(progressRepository.findByUserIdAndConfidenceLevelLessThan(1L, 0.5))
            .thenReturn(List.of(weakProgress));
        
        // When
        List<Topic> weakTopics = learningService.getWeakTopics(1L);
        
        // Then
        assertAll(
            () -> assertThat(weakTopics).hasSize(1),
            () -> assertThat(weakTopics.get(0).getName()).isEqualTo("Netzwerke")
        );
    }
    
    @Test
    @DisplayName("Sollte Fragen für Review auswählen basierend auf Spaced Repetition")
    void shouldSelectQuestionsForReview() {
        // Given
        LocalDateTime now = LocalDateTime.now();
        
        Progress dueProgress = new Progress();
        dueProgress.setQuestion(testQuestion);
        dueProgress.setNextReview(now.minusHours(1)); // Überfällig
        
        Progress notDueProgress = new Progress();
        notDueProgress.setQuestion(testQuestion);
        notDueProgress.setNextReview(now.plusDays(3)); // Noch nicht fällig
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(progressRepository.findByUserIdAndNextReviewBefore(eq(1L), any(LocalDateTime.class)))
            .thenReturn(List.of(dueProgress));
        
        // When
        List<Question> reviewQuestions = learningService.getQuestionsForReview(1L);
        
        // Then
        assertAll(
            () -> assertThat(reviewQuestions).hasSize(1),
            () -> assertThat(reviewQuestions).contains(testQuestion)
        );
    }
    
    @Test
    @DisplayName("Sollte Exception werfen bei ungültiger Answer ID")
    void shouldThrowExceptionForInvalidAnswerId() {
        // Given
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        
        // When & Then
        assertThatThrownBy(() -> learningService.submitAnswer(1L, 1L, 999L))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessage("Invalid answer ID for this question");
    }
    
    @Test
    @DisplayName("Sollte Learning Streak berechnen")
    void shouldCalculateLearningStreak() {
        // Given
        LocalDateTime today = LocalDateTime.now();
        
        List<LearningSession> sessions = Arrays.asList(
            createSessionOnDate(today),
            createSessionOnDate(today.minusDays(1)),
            createSessionOnDate(today.minusDays(2)),
            createSessionOnDate(today.minusDays(4)) // Lücke - Streak bricht ab
        );
        
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(learningSessionRepository.findByUserIdOrderByCreatedAtDesc(1L))
            .thenReturn(sessions);
        
        // When
        int streak = learningService.getLearningStreak(1L);
        
        // Then
        assertThat(streak).isEqualTo(3); // Nur die letzten 3 Tage ohne Unterbrechung
    }
    
    @Test
    @DisplayName("Sollte Question Statistics generieren")
    void shouldGenerateQuestionStatistics() {
        // Given
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(progressRepository.countByQuestionId(1L)).thenReturn(100L);
        when(progressRepository.countCorrectAnswersByQuestionId(1L)).thenReturn(75L);
        when(progressRepository.getAverageConfidenceByQuestionId(1L)).thenReturn(0.65);
        
        // When
        QuestionStatisticsDTO stats = learningService.getQuestionStatistics(1L);
        
        // Then
        assertAll(
            () -> assertThat(stats.getQuestionId()).isEqualTo(1L),
            () -> assertThat(stats.getTotalAttempts()).isEqualTo(100),
            () -> assertThat(stats.getSuccessRate()).isEqualTo(0.75),
            () -> assertThat(stats.getAverageConfidence()).isEqualTo(0.65),
            () -> assertThat(stats.getDifficulty()).isEqualTo(DifficultyLevel.BEGINNER)
        );
    }
    
    // Helper Methods
    
    private Progress createProgressWithConfidence(double confidence) {
        Progress progress = new Progress();
        progress.setUser(testUser);
        progress.setQuestion(testQuestion);
        progress.setConfidenceLevel(confidence);
        progress.setAttempts(5);
        progress.setCorrectAttempts(3);
        return progress;
    }
    
    private Progress createProgressWithStats(int attempts, int correct, double confidence) {
        Progress progress = new Progress();
        progress.setUser(testUser);
        progress.setQuestion(testQuestion);
        progress.setAttempts(attempts);
        progress.setCorrectAttempts(correct);
        progress.setConfidenceLevel(confidence);
        return progress;
    }
    
    private LearningSession createSessionOnDate(LocalDateTime date) {
        LearningSession session = new LearningSession();
        session.setUser(testUser);
        session.setCreatedAt(date);
        session.setCompletedAt(date.plusMinutes(30));
        return session;
    }
}
