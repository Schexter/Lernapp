package com.fachinformatiker.lernapp.service.learning;

import java.math.BigDecimal;

import com.fachinformatiker.lernapp.model.Question;
import com.fachinformatiker.lernapp.model.User;
import com.fachinformatiker.lernapp.model.UserProgress;
import com.fachinformatiker.lernapp.repository.QuestionRepository;
import com.fachinformatiker.lernapp.repository.UserProgressRepository;
import com.fachinformatiker.lernapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service implementing the Leitner System for spaced repetition learning
 * 
 * Box System:
 * - Box 1: Review after 1 day
 * - Box 2: Review after 3 days
 * - Box 3: Review after 7 days
 * - Box 4: Review after 14 days
 * - Box 5: Review after 30 days
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SpacedRepetitionService {

    private final UserProgressRepository userProgressRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    
    // Question selection weights
    private static final double WEIGHT_OVERDUE = 0.40;  // 40% overdue reviews
    private static final double WEIGHT_NEW = 0.35;      // 35% new questions
    private static final double WEIGHT_DIFFICULT = 0.20; // 20% difficult questions
    private static final double WEIGHT_RANDOM = 0.05;   // 5% random refresh
    
    /**
     * Calculate next review date based on Leitner box system
     */
    @Transactional
    public LocalDateTime calculateNextReview(Long questionId, Long userId, boolean wasCorrect) {
        log.debug("Calculating next review for question {} and user {}, correct: {}", 
                  questionId, userId, wasCorrect);
        
        UserProgress progress = userProgressRepository
            .findByUserIdAndQuestionId(userId, questionId)
            .orElseGet(() -> createNewProgress(userId, questionId));
        
        LocalDateTime now = LocalDateTime.now();
        int currentBox = progress.getRepetitionBox() != null ? progress.getRepetitionBox() : 1;
        
        if (wasCorrect) {
            // Move to next box
            int nextBox = Math.min(currentBox + 1, 5);
            progress.setRepetitionBox(nextBox);
            progress.setCorrectAttempts(progress.getCorrectAttempts() + 1);
            
            // Calculate next review based on new box
            switch (nextBox) {
                case 1:
                    progress.setNextReview(now.plusDays(1));
                    break;
                case 2:
                    progress.setNextReview(now.plusDays(3));
                    break;
                case 3:
                    progress.setNextReview(now.plusDays(7));
                    break;
                case 4:
                    progress.setNextReview(now.plusDays(14));
                    break;
                case 5:
                    progress.setNextReview(now.plusDays(30));
                    break;
            }
            
            log.info("Question {} moved to box {} for user {}", questionId, nextBox, userId);
        } else {
            // Reset to box 1
            progress.setRepetitionBox(1);
            progress.setNextReview(now.plusDays(1));
            log.info("Question {} reset to box 1 for user {}", questionId, userId);
        }
        
        progress.setAttempts(progress.getAttempts() + 1);
        progress.setLastAttempt(now);
        progress.setConfidenceLevel(BigDecimal.valueOf(calculateConfidence(progress)));
        
        userProgressRepository.save(progress);
        
        return progress.getNextReview();
    }
    
    /**
     * Get the next question for a user based on spaced repetition algorithm
     */
    @Transactional(readOnly = true)
    public Question getNextQuestion(Long userId, Long topicId) {
        log.debug("Getting next question for user {} and topic {}", userId, topicId);
        
        // Get different categories of questions
        List<Question> overdueQuestions = getOverdueQuestions(userId, topicId);
        List<Question> newQuestions = getNewQuestions(userId, topicId);
        List<Question> difficultQuestions = getDifficultQuestions(userId, topicId);
        List<Question> allQuestions = getAllTopicQuestions(topicId);
        
        // Select based on weights
        double random = Math.random();
        Question selected = null;
        
        if (random < WEIGHT_OVERDUE && !overdueQuestions.isEmpty()) {
            // Priority 1: Overdue reviews
            selected = selectMostOverdue(overdueQuestions, userId);
            log.debug("Selected overdue question: {}", selected.getId());
        } else if (random < (WEIGHT_OVERDUE + WEIGHT_NEW) && !newQuestions.isEmpty()) {
            // Priority 2: New questions
            selected = selectRandomFromList(newQuestions);
            log.debug("Selected new question: {}", selected.getId());
        } else if (random < (WEIGHT_OVERDUE + WEIGHT_NEW + WEIGHT_DIFFICULT) && !difficultQuestions.isEmpty()) {
            // Priority 3: Difficult questions (lower boxes)
            selected = selectMostDifficult(difficultQuestions, userId);
            log.debug("Selected difficult question: {}", selected.getId());
        } else if (!allQuestions.isEmpty()) {
            // Priority 4: Random refresh
            selected = selectRandomFromList(allQuestions);
            log.debug("Selected random question: {}", selected.getId());
        }
        
        if (selected == null && !allQuestions.isEmpty()) {
            // Fallback: any question from the topic
            selected = selectRandomFromList(allQuestions);
            log.debug("Fallback: selected any question: {}", selected.getId());
        }
        
        return selected;
    }
    
    /**
     * Get questions that are overdue for review
     */
    private List<Question> getOverdueQuestions(Long userId, Long topicId) {
        LocalDateTime now = LocalDateTime.now();
        
        List<UserProgress> overdueProgress = userProgressRepository
            .findOverdueReviews(userId, now);
        
        if (topicId != null) {
            overdueProgress = overdueProgress.stream()
                .filter(p -> p.getQuestion().getTopic().getId().equals(topicId))
                .collect(Collectors.toList());
        }
        
        return overdueProgress.stream()
            .map(UserProgress::getQuestion)
            .collect(Collectors.toList());
    }
    
    /**
     * Get questions never attempted by the user
     */
    private List<Question> getNewQuestions(Long userId, Long topicId) {
        if (topicId != null) {
            return questionRepository.findUnansweredQuestionsByUserAndTopic(userId, topicId);
        } else {
            return questionRepository.findUnansweredQuestionsByUser(userId);
        }
    }
    
    /**
     * Get questions in lower boxes (difficult for the user)
     */
    private List<Question> getDifficultQuestions(Long userId, Long topicId) {
        List<UserProgress> lowBoxProgress = userProgressRepository
            .findByUserIdAndRepetitionBoxLessThanEqual(userId, 2);
        
        if (topicId != null) {
            lowBoxProgress = lowBoxProgress.stream()
                .filter(p -> p.getQuestion().getTopic().getId().equals(topicId))
                .collect(Collectors.toList());
        }
        
        return lowBoxProgress.stream()
            .map(UserProgress::getQuestion)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all questions for a topic
     */
    private List<Question> getAllTopicQuestions(Long topicId) {
        if (topicId != null) {
            return questionRepository.findByTopicIdAndActive(topicId, true);
        } else {
            return questionRepository.findByActive(true);
        }
    }
    
    /**
     * Select the most overdue question
     */
    private Question selectMostOverdue(List<Question> questions, Long userId) {
        return questions.stream()
            .min((q1, q2) -> {
                UserProgress p1 = userProgressRepository
                    .findByUserIdAndQuestionId(userId, q1.getId()).orElse(null);
                UserProgress p2 = userProgressRepository
                    .findByUserIdAndQuestionId(userId, q2.getId()).orElse(null);
                
                if (p1 == null) return 1;
                if (p2 == null) return -1;
                
                return p1.getNextReview().compareTo(p2.getNextReview());
            })
            .orElse(null);
    }
    
    /**
     * Select the most difficult question (lowest success rate)
     */
    private Question selectMostDifficult(List<Question> questions, Long userId) {
        return questions.stream()
            .min((q1, q2) -> {
                UserProgress p1 = userProgressRepository
                    .findByUserIdAndQuestionId(userId, q1.getId()).orElse(null);
                UserProgress p2 = userProgressRepository
                    .findByUserIdAndQuestionId(userId, q2.getId()).orElse(null);
                
                if (p1 == null) return 1;
                if (p2 == null) return -1;
                
                double rate1 = calculateSuccessRate(p1);
                double rate2 = calculateSuccessRate(p2);
                
                return Double.compare(rate1, rate2);
            })
            .orElse(null);
    }
    
    /**
     * Select random question from list
     */
    private Question selectRandomFromList(List<Question> questions) {
        if (questions.isEmpty()) {
            return null;
        }
        Random random = new Random();
        return questions.get(random.nextInt(questions.size()));
    }
    
    /**
     * Create new progress entry for a user and question
     */
    private UserProgress createNewProgress(Long userId, Long questionId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        
        return UserProgress.builder()
            .user(user)
            .question(question)
            .attempts(0)
            .correctAttempts(0)
            .repetitionBox(1)
            .nextReview(LocalDateTime.now().plusDays(1))
            .confidenceLevel(BigDecimal.ONE)
            .build();
    }
    
    /**
     * Calculate success rate for a user's progress
     */
    private double calculateSuccessRate(UserProgress progress) {
        if (progress.getAttempts() == 0) {
            return 0.0;
        }
        return (double) progress.getCorrectAttempts() / progress.getAttempts();
    }
    
    /**
     * Calculate confidence level based on progress
     */
    private Integer calculateConfidence(UserProgress progress) {
        double successRate = calculateSuccessRate(progress);
        int box = progress.getRepetitionBox() != null ? progress.getRepetitionBox() : 1;
        
        // Confidence = combination of success rate and box level
        double confidence = (successRate * 0.6) + (box / 5.0 * 0.4);
        
        // Convert to 1-5 scale
        return Math.max(1, Math.min(5, (int) Math.ceil(confidence * 5)));
    }
    
    /**
     * Get learning statistics for a user
     */
    @Transactional(readOnly = true)
    public LearningStatistics getUserStatistics(Long userId, Long topicId) {
        List<UserProgress> allProgress;
        
        if (topicId != null) {
            allProgress = userProgressRepository.findByUserIdAndTopicId(userId, topicId);
        } else {
            allProgress = userProgressRepository.findByUserId(userId);
        }
        
        LearningStatistics stats = new LearningStatistics();
        stats.setTotalQuestions(allProgress.size());
        
        // Calculate box distribution
        Map<Integer, Long> boxDistribution = allProgress.stream()
            .filter(p -> p.getRepetitionBox() != null)
            .collect(Collectors.groupingBy(
                UserProgress::getRepetitionBox,
                Collectors.counting()
            ));
        stats.setBoxDistribution(boxDistribution);
        
        // Calculate mastery (questions in box 4 or 5)
        long masteredCount = allProgress.stream()
            .filter(p -> p.getRepetitionBox() != null && p.getRepetitionBox() >= 4)
            .count();
        stats.setMasteredQuestions((int) masteredCount);
        
        // Calculate average success rate
        double avgSuccessRate = allProgress.stream()
            .mapToDouble(this::calculateSuccessRate)
            .average()
            .orElse(0.0);
        stats.setAverageSuccessRate(avgSuccessRate);
        
        // Count overdue reviews
        long overdueCount = allProgress.stream()
            .filter(p -> p.getNextReview() != null && 
                        p.getNextReview().isBefore(LocalDateTime.now()))
            .count();
        stats.setOverdueReviews((int) overdueCount);
        
        return stats;
    }
    
    /**
     * Reset progress for a specific topic
     */
    @Transactional
    public void resetTopicProgress(Long userId, Long topicId) {
        log.info("Resetting progress for user {} and topic {}", userId, topicId);
        
        List<UserProgress> progressList = userProgressRepository
            .findByUserIdAndTopicId(userId, topicId);
        
        progressList.forEach(progress -> {
            progress.setRepetitionBox(1);
            progress.setAttempts(0);
            progress.setCorrectAttempts(0);
            progress.setNextReview(LocalDateTime.now().plusDays(1));
            progress.setConfidenceLevel(BigDecimal.ONE);
        });
        
        userProgressRepository.saveAll(progressList);
    }
    
    /**
     * Statistics DTO
     */
    @lombok.Data
    public static class LearningStatistics {
        private int totalQuestions;
        private int masteredQuestions;
        private int overdueReviews;
        private double averageSuccessRate;
        private Map<Integer, Long> boxDistribution;
    }
}