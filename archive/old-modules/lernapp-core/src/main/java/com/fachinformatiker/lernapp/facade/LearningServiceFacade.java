package com.fachinformatiker.lernapp.facade;

import com.fachinformatiker.lernapp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Learning Service Facade Interface - VOLLSTÄNDIG
 * Alle Methoden die vom LearningController benötigt werden
 * 
 * @author Hans Hahn
 */
public interface LearningServiceFacade {
    
    // Session Management
    LearningSessionDTO createLearningSession(Long userId, Long topicId, int questionCount);
    Optional<LearningSessionDTO> getActiveSession(Long userId);
    LearningSessionDTO completeSession(Long sessionId);
    
    // Answer Submission
    AnswerSubmissionDTO submitAnswer(@Valid AnswerSubmissionDTO submission);
    
    // Progress Tracking - VEREINHEITLICHT
    UserProgressDTO getUserProgressByTopic(Long userId, Long topicId);
    UserProgressDTO getUserProgress(Long userId);
    DetailedProgressDTO getDetailedProgress(Long userId, LocalDate startDate, LocalDate endDate);
    
    // Review Questions
    List<QuestionDTO> getQuestionsForReview(Long userId, int limit);
    
    // Learning Streak
    StreakDTO getLearningStreak(Long userId);
    
    // Weak Topics
    List<TopicProgressDTO> getWeakTopics(Long userId, double threshold);
    
    // Recommendations - EINZELNES OBJEKT
    RecommendationDTO generateRecommendations(Long userId);
    
    // Statistics
    LearningStatisticsDTO getLearningStatistics(Long userId, int days);
    
    // Daily Activity - ALS MAP
    Map<LocalDate, Integer> getDailyActivity(Long userId, int days);
    
    // Learning Goals
    LearningGoalDTO setLearningGoal(@Valid LearningGoalDTO goalDTO);
    List<LearningGoalDTO> getLearningGoals(Long userId);
    
    // Topic Progress
    List<TopicProgressDTO> getAllTopicProgress(Long userId);
    void resetTopicProgress(Long userId, Long topicId);
    
    // Leaderboard - ALS LISTE
    List<LeaderboardEntryDTO> getLeaderboard(Long topicId, int limit);
    
    // Performance Trend
    PerformanceTrendDTO getPerformanceTrend(Long userId, int days);
}
