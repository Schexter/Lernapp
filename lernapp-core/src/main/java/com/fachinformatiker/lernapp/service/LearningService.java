package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LearningService {
    
    private final UserProgressRepository userProgressRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final TopicRepository topicRepository;
    private final UserService userService;
    
    // Spaced Repetition Algorithm (SM-2 based)
    private static final BigDecimal INITIAL_EASE_FACTOR = new BigDecimal("2.5");
    private static final BigDecimal MIN_EASE_FACTOR = new BigDecimal("1.3");
    private static final BigDecimal MAX_EASE_FACTOR = new BigDecimal("2.5");
    
    public UserProgress recordAnswer(Long userId, Long questionId, boolean isCorrect, int responseTimeSeconds) {
        log.info("Recording answer for user {} on question {}: {}", userId, questionId, isCorrect);
        
        UserProgress progress = userProgressRepository.findByUserIdAndQuestionId(userId, questionId)
            .orElseGet(() -> createNewProgress(userId, questionId));
        
        // Record the attempt
        progress.recordAttempt(isCorrect, responseTimeSeconds);
        
        // Calculate spaced repetition parameters
        calculateSpacedRepetition(progress, isCorrect);
        
        // Update confidence level
        updateConfidenceLevel(progress);
        
        // Save progress
        UserProgress savedProgress = userProgressRepository.save(progress);
        
        // Update user points and streak
        if (isCorrect) {
            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
            userService.addPoints(userId, question.getPoints());
            
            // Check if this maintains the streak
            checkAndUpdateStreak(userId);
        }
        
        log.info("Answer recorded successfully. Next review: {}", savedProgress.getNextReview());
        return savedProgress;
    }
    
    private UserProgress createNewProgress(Long userId, Long questionId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        UserProgress progress = new UserProgress();
        progress.setUser(user);
        progress.setQuestion(question);
        progress.setAttempts(0);
        progress.setCorrectAttempts(0);
        progress.setConfidenceLevel(BigDecimal.ZERO);
        progress.setEaseFactor(INITIAL_EASE_FACTOR);
        progress.setRepetitionInterval(1);
        progress.setConsecutiveCorrect(0);
        progress.setTimeSpentSeconds(0L);
        progress.setMasteryLevel(UserProgress.MasteryLevel.NOT_STARTED);
        progress.setActive(true);
        
        return progress;
    }
    
    private void calculateSpacedRepetition(UserProgress progress, boolean isCorrect) {
        BigDecimal easeFactor = progress.getEaseFactor();
        int interval = progress.getRepetitionInterval();
        
        if (isCorrect) {
            // Increase interval
            if (progress.getConsecutiveCorrect() == 1) {
                interval = 1;
            } else if (progress.getConsecutiveCorrect() == 2) {
                interval = 6;
            } else {
                interval = (int) Math.round(interval * easeFactor.doubleValue());
            }
            
            // Adjust ease factor (make it easier)
            easeFactor = easeFactor.add(new BigDecimal("0.1"));
            
        } else {
            // Reset interval and decrease ease factor
            interval = 1;
            easeFactor = easeFactor.subtract(new BigDecimal("0.2"));
        }
        
        // Ensure ease factor stays within bounds
        if (easeFactor.compareTo(MIN_EASE_FACTOR) < 0) {
            easeFactor = MIN_EASE_FACTOR;
        } else if (easeFactor.compareTo(MAX_EASE_FACTOR) > 0) {
            easeFactor = MAX_EASE_FACTOR;
        }
        
        progress.setEaseFactor(easeFactor);
        progress.setRepetitionInterval(interval);
        progress.setNextReview(LocalDateTime.now().plusDays(interval));
    }
    
    private void updateConfidenceLevel(UserProgress progress) {
        BigDecimal accuracy = progress.getAccuracy();
        int consecutiveCorrect = progress.getConsecutiveCorrect();
        BigDecimal easeFactor = progress.getEaseFactor();
        
        // Calculate confidence based on accuracy, streak, and ease factor
        BigDecimal confidence = accuracy.divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP)
            .multiply(new BigDecimal("0.5"))
            .add(new BigDecimal(consecutiveCorrect).multiply(new BigDecimal("0.1")))
            .add(easeFactor.subtract(MIN_EASE_FACTOR)
                .divide(MAX_EASE_FACTOR.subtract(MIN_EASE_FACTOR), 2, RoundingMode.HALF_UP)
                .multiply(new BigDecimal("0.3")));
        
        // Ensure confidence stays between 0 and 1
        if (confidence.compareTo(BigDecimal.ONE) > 0) {
            confidence = BigDecimal.ONE;
        } else if (confidence.compareTo(BigDecimal.ZERO) < 0) {
            confidence = BigDecimal.ZERO;
        }
        
        progress.setConfidenceLevel(confidence);
    }
    
    private void checkAndUpdateStreak(Long userId) {
        LocalDateTime today = LocalDateTime.now().toLocalDate().atStartOfDay();
        LocalDateTime yesterday = today.minusDays(1);
        
        // Check if user has answered questions today
        List<UserProgress> todayProgress = userProgressRepository.findDueForReviewBetween(
            userId, today, LocalDateTime.now()
        );
        
        if (!todayProgress.isEmpty()) {
            // Check if user answered questions yesterday
            List<UserProgress> yesterdayProgress = userProgressRepository.findDueForReviewBetween(
                userId, yesterday, today
            );
            
            if (!yesterdayProgress.isEmpty()) {
                userService.incrementLearningStreak(userId);
            } else {
                userService.resetLearningStreak(userId);
            }
        }
    }
    
    // Get questions for learning session
    public List<Question> getQuestionsForLearningSession(Long userId, Long topicId, int count) {
        log.debug("Getting {} questions for learning session - user: {}, topic: {}", count, userId, topicId);
        
        List<Question> questions = new ArrayList<>();
        
        // First, get questions due for review
        List<Question> dueQuestions = questionRepository.findQuestionsForReview(userId);
        questions.addAll(dueQuestions.stream()
            .filter(q -> q.getTopic().getId().equals(topicId))
            .limit(count / 2)
            .collect(Collectors.toList()));
        
        // Then, get new questions
        int remaining = count - questions.size();
        if (remaining > 0) {
            List<Question> newQuestions = questionRepository.findUnansweredQuestionsByUserAndTopic(userId, topicId);
            questions.addAll(newQuestions.stream()
                .limit(remaining)
                .collect(Collectors.toList()));
        }
        
        // If still need more, get random questions
        remaining = count - questions.size();
        if (remaining > 0) {
            List<Question> randomQuestions = questionRepository.findRandomQuestionsByTopic(topicId, remaining);
            questions.addAll(randomQuestions);
        }
        
        Collections.shuffle(questions);
        return questions;
    }
    
    // Get user progress statistics
    public Map<String, Object> getUserStatistics(Long userId) {
        log.debug("Getting statistics for user: {}", userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // Basic counts
        stats.put("totalQuestionsAttempted", userProgressRepository.countByUserId(userId));
        stats.put("questionsMastered", userProgressRepository.countMasteredByUserId(userId));
        stats.put("topicsStarted", userProgressRepository.countTopicsStartedByUser(userId));
        
        // Averages
        BigDecimal avgConfidence = userProgressRepository.getAverageConfidenceByUser(userId);
        stats.put("averageConfidence", avgConfidence != null ? avgConfidence : BigDecimal.ZERO);
        
        // Time spent
        Long totalTimeSpent = userProgressRepository.getTotalTimeSpentByUser(userId);
        stats.put("totalTimeSpentSeconds", totalTimeSpent != null ? totalTimeSpent : 0L);
        stats.put("totalTimeSpentHours", totalTimeSpent != null ? totalTimeSpent / 3600.0 : 0.0);
        
        // Mastery distribution
        List<Object[]> masteryDist = userProgressRepository.getMasteryLevelDistribution(userId);
        Map<String, Long> masteryMap = masteryDist.stream()
            .collect(Collectors.toMap(
                r -> ((UserProgress.MasteryLevel) r[0]).name(),
                r -> (Long) r[1]
            ));
        stats.put("masteryDistribution", masteryMap);
        
        // Topic statistics
        List<Object[]> topicStats = userProgressRepository.getUserStatisticsByTopic(userId);
        List<Map<String, Object>> topicStatsList = new ArrayList<>();
        
        for (Object[] row : topicStats) {
            Map<String, Object> topicStat = new HashMap<>();
            topicStat.put("topicId", row[0]);
            topicStat.put("topicName", row[1]);
            topicStat.put("questionsAttempted", row[2]);
            topicStat.put("averageConfidence", row[3]);
            topicStat.put("correctAnswers", row[4]);
            topicStat.put("totalAttempts", row[5]);
            
            if (row[5] != null && ((Long) row[5]) > 0) {
                double accuracy = ((Long) row[4]).doubleValue() / ((Long) row[5]).doubleValue() * 100;
                topicStat.put("accuracy", accuracy);
            } else {
                topicStat.put("accuracy", 0.0);
            }
            
            topicStatsList.add(topicStat);
        }
        
        stats.put("topicStatistics", topicStatsList);
        
        return stats;
    }
    
    public Map<String, Object> getTopicProgress(Long userId, Long topicId) {
        log.debug("Getting topic progress for user {} in topic {}", userId, topicId);
        
        Map<String, Object> progress = new HashMap<>();
        
        // Get all questions in topic
        Long totalQuestions = questionRepository.countActiveByTopicId(topicId);
        progress.put("totalQuestions", totalQuestions);
        
        // Get user progress for this topic
        List<UserProgress> userProgress = userProgressRepository.findByUserIdAndTopicId(userId, topicId);
        progress.put("questionsAttempted", userProgress.size());
        
        // Calculate statistics
        long mastered = userProgress.stream()
            .filter(p -> p.getMasteryLevel() == UserProgress.MasteryLevel.MASTERED)
            .count();
        progress.put("questionsMastered", mastered);
        
        long proficient = userProgress.stream()
            .filter(p -> p.getMasteryLevel() == UserProgress.MasteryLevel.PROFICIENT)
            .count();
        progress.put("questionsProficient", proficient);
        
        // Average confidence
        BigDecimal avgConfidence = userProgressRepository.getAverageConfidenceByUserAndTopic(userId, topicId);
        progress.put("averageConfidence", avgConfidence != null ? avgConfidence : BigDecimal.ZERO);
        
        // Completion percentage
        double completionPercentage = totalQuestions > 0 ? 
            (userProgress.size() * 100.0 / totalQuestions) : 0.0;
        progress.put("completionPercentage", completionPercentage);
        
        // Mastery percentage
        double masteryPercentage = totalQuestions > 0 ? 
            (mastered * 100.0 / totalQuestions) : 0.0;
        progress.put("masteryPercentage", masteryPercentage);
        
        return progress;
    }
    
    // Get questions due for review
    public List<Question> getQuestionsForReview(Long userId) {
        log.debug("Getting questions for review for user: {}", userId);
        return questionRepository.findQuestionsForReview(userId);
    }
    
    public List<UserProgress> getDueReviews(Long userId) {
        log.debug("Getting due reviews for user: {}", userId);
        return userProgressRepository.findDueForReview(userId, LocalDateTime.now());
    }
    
    // Reset progress for a topic
    public void resetTopicProgress(Long userId, Long topicId) {
        log.warn("Resetting progress for user {} in topic {}", userId, topicId);
        
        List<UserProgress> progressList = userProgressRepository.findByUserIdAndTopicId(userId, topicId);
        
        for (UserProgress progress : progressList) {
            userProgressRepository.delete(progress);
        }
        
        log.info("Reset {} progress records", progressList.size());
    }
    
    // Get learning recommendations
    public List<Topic> getRecommendedTopics(Long userId) {
        log.debug("Getting recommended topics for user: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user's preferred topics
        Set<Topic> preferredTopics = user.getPreferredTopics();
        
        // Get topics with high confidence
        List<Object[]> topicStats = userProgressRepository.getUserStatisticsByTopic(userId);
        Set<Long> masteredTopicIds = topicStats.stream()
            .filter(stat -> stat[3] != null && ((BigDecimal) stat[3]).compareTo(new BigDecimal("0.8")) > 0)
            .map(stat -> (Long) stat[0])
            .collect(Collectors.toSet());
        
        // Find related topics that haven't been mastered
        List<Topic> recommendations = new ArrayList<>();
        
        for (Topic preferred : preferredTopics) {
            // Get children topics
            List<Topic> children = topicRepository.findByParentId(preferred.getId());
            recommendations.addAll(children.stream()
                .filter(t -> !masteredTopicIds.contains(t.getId()))
                .collect(Collectors.toList()));
            
            // Get sibling topics
            if (preferred.getParent() != null) {
                List<Topic> siblings = topicRepository.findByParentId(preferred.getParent().getId());
                recommendations.addAll(siblings.stream()
                    .filter(t -> !t.getId().equals(preferred.getId()))
                    .filter(t -> !masteredTopicIds.contains(t.getId()))
                    .collect(Collectors.toList()));
            }
        }
        
        // Remove duplicates and limit
        return recommendations.stream()
            .distinct()
            .limit(10)
            .collect(Collectors.toList());
    }
}