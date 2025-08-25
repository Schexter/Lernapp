package com.fachinformatiker.lernapp.service.progress;

import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for tracking and analyzing user learning progress
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProgressTrackingService {
    
    private final UserProgressRepository userProgressRepository;
    private final LearningSessionRepository learningSessionRepository;
    private final QuestionAttemptRepository questionAttemptRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    
    /**
     * Calculate overall progress for a user in a topic
     */
    @Transactional(readOnly = true)
    public UserProgress calculateProgress(Long userId, Long topicId) {
        log.debug("Calculating progress for user {} in topic {}", userId, topicId);
        
        List<UserProgress> progressList = userProgressRepository
            .findByUserIdAndTopicId(userId, topicId);
        
        if (progressList.isEmpty()) {
            return createEmptyProgress(userId, topicId);
        }
        
        UserProgress overallProgress = new UserProgress();
        
        // Calculate totals
        int totalQuestions = progressList.size();
        int totalAttempts = progressList.stream()
            .mapToInt(UserProgress::getAttempts)
            .sum();
        int correctAttempts = progressList.stream()
            .mapToInt(UserProgress::getCorrectAttempts)
            .sum();
        
        // Calculate averages
        double avgConfidence = progressList.stream()
            .filter(p -> p.getConfidenceLevel() != null)
            .mapToDouble(p -> p.getConfidenceLevel().doubleValue())
            .average()
            .orElse(0.0);
        
        // Count mastered questions (box 4 or 5)
        long masteredCount = progressList.stream()
            .filter(p -> p.getRepetitionBox() != null && p.getRepetitionBox() >= 4)
            .count();
        
        // Set calculated values
        overallProgress.setAttempts(totalAttempts);
        overallProgress.setCorrectAttempts(correctAttempts);
        overallProgress.setConfidenceLevel(BigDecimal.valueOf(avgConfidence));
        
        // Calculate mastery percentage
        double masteryPercentage = (double) masteredCount / totalQuestions * 100;
        
        log.info("Progress for user {} in topic {}: {}% mastery", 
                 userId, topicId, Math.round(masteryPercentage));
        
        return overallProgress;
    }
    
    /**
     * Identify weak areas for a user
     */
    @Transactional(readOnly = true)
    public List<WeakArea> identifyWeakAreas(Long userId) {
        log.debug("Identifying weak areas for user {}", userId);
        
        List<Object[]> topicStats = userProgressRepository.getUserStatisticsByTopic(userId);
        List<WeakArea> weakAreas = new ArrayList<>();
        
        for (Object[] stat : topicStats) {
            Long topicId = (Long) stat[0];
            String topicName = (String) stat[1];
            Long questionCount = (Long) stat[2];
            BigDecimal avgConfidence = (BigDecimal) stat[3];
            Long correctCount = (Long) stat[4];
            Long attemptCount = (Long) stat[5];
            
            if (attemptCount > 0) {
                double successRate = (double) correctCount / attemptCount;
                
                // Consider it weak if success rate < 60% or confidence < 2.5
                if (successRate < 0.6 || (avgConfidence != null && avgConfidence.doubleValue() < 2.5)) {
                    WeakArea weakArea = new WeakArea();
                    weakArea.setTopicId(topicId);
                    weakArea.setTopicName(topicName);
                    weakArea.setSuccessRate(successRate);
                    weakArea.setConfidenceLevel(avgConfidence != null ? avgConfidence.doubleValue() : 0.0);
                    weakArea.setQuestionCount(questionCount.intValue());
                    weakArea.setSuggestedAction(getSuggestedAction(successRate, avgConfidence));
                    
                    weakAreas.add(weakArea);
                }
            }
        }
        
        // Sort by success rate (lowest first)
        weakAreas.sort(Comparator.comparingDouble(WeakArea::getSuccessRate));
        
        log.info("Found {} weak areas for user {}", weakAreas.size(), userId);
        
        return weakAreas;
    }
    
    /**
     * Get learning streak for a user
     */
    @Transactional(readOnly = true)
    public LearningStreak getStreak(Long userId) {
        log.debug("Calculating streak for user {}", userId);
        
        LearningStreak streak = new LearningStreak();
        
        // Get daily activity for last 90 days
        LocalDateTime startDate = LocalDateTime.now().minusDays(90);
        List<Object[]> dailyActivity = userProgressRepository
            .getDailyActivityStats(userId, startDate);
        
        if (dailyActivity.isEmpty()) {
            streak.setCurrentStreak(0);
            streak.setLongestStreak(0);
            return streak;
        }
        
        // Convert to date set for easier processing
        Set<LocalDate> activeDays = new HashSet<>();
        for (Object[] activity : dailyActivity) {
            if (activity[0] != null) {
                LocalDate date = ((java.sql.Date) activity[0]).toLocalDate();
                activeDays.add(date);
            }
        }
        
        // Calculate current streak
        int currentStreak = 0;
        LocalDate today = LocalDate.now();
        LocalDate checkDate = today;
        
        while (activeDays.contains(checkDate)) {
            currentStreak++;
            checkDate = checkDate.minusDays(1);
        }
        
        // Check if streak was broken today (no activity yet)
        if (currentStreak == 0 && activeDays.contains(today.minusDays(1))) {
            // Yesterday had activity but today doesn't yet
            checkDate = today.minusDays(1);
            while (activeDays.contains(checkDate)) {
                currentStreak++;
                checkDate = checkDate.minusDays(1);
            }
        }
        
        streak.setCurrentStreak(currentStreak);
        
        // Calculate longest streak
        int longestStreak = 0;
        int tempStreak = 0;
        List<LocalDate> sortedDays = new ArrayList<>(activeDays);
        Collections.sort(sortedDays);
        
        LocalDate previousDay = null;
        for (LocalDate day : sortedDays) {
            if (previousDay == null || ChronoUnit.DAYS.between(previousDay, day) == 1) {
                tempStreak++;
                longestStreak = Math.max(longestStreak, tempStreak);
            } else {
                tempStreak = 1;
            }
            previousDay = day;
        }
        
        streak.setLongestStreak(longestStreak);
        streak.setLastActivityDate(sortedDays.get(sortedDays.size() - 1));
        streak.setTotalActiveDays(activeDays.size());
        
        log.info("User {} has current streak: {}, longest: {}", 
                 userId, currentStreak, longestStreak);
        
        return streak;
    }
    
    /**
     * Get topic mastery levels for a user
     */
    @Transactional(readOnly = true)
    public Map<Topic, Double> getTopicMastery(Long userId) {
        log.debug("Getting topic mastery for user {}", userId);
        
        Map<Topic, Double> masteryMap = new HashMap<>();
        
        List<Topic> topics = topicRepository.findAll();
        
        for (Topic topic : topics) {
            List<UserProgress> topicProgress = userProgressRepository
                .findByUserIdAndTopicId(userId, topic.getId());
            
            if (!topicProgress.isEmpty()) {
                // Calculate mastery based on box distribution
                long totalQuestions = topicProgress.size();
                long masteredQuestions = topicProgress.stream()
                    .filter(p -> p.getRepetitionBox() != null && p.getRepetitionBox() >= 4)
                    .count();
                
                double mastery = (double) masteredQuestions / totalQuestions;
                masteryMap.put(topic, mastery);
            } else {
                masteryMap.put(topic, 0.0);
            }
        }
        
        return masteryMap;
    }
    
    /**
     * Get detailed statistics for a user
     */
    @Transactional(readOnly = true)
    public DetailedStatistics getDetailedStatistics(Long userId) {
        log.debug("Getting detailed statistics for user {}", userId);
        
        DetailedStatistics stats = new DetailedStatistics();
        stats.setUserId(userId);
        
        // Total questions attempted
        Long totalAttempts = questionAttemptRepository.countByUserId(userId);
        stats.setTotalQuestionsAttempted(totalAttempts != null ? totalAttempts.intValue() : 0);
        
        // Correct answers
        Long correctAnswers = questionAttemptRepository.countCorrectByUserId(userId);
        stats.setCorrectAnswers(correctAnswers != null ? correctAnswers.intValue() : 0);
        
        // Success rate
        if (stats.getTotalQuestionsAttempted() > 0) {
            double successRate = (double) stats.getCorrectAnswers() / stats.getTotalQuestionsAttempted();
            stats.setOverallSuccessRate(successRate);
        } else {
            stats.setOverallSuccessRate(0.0);
        }
        
        // Average response time
        Double avgResponseTime = questionAttemptRepository.getAverageResponseTime(userId);
        stats.setAverageResponseTime(avgResponseTime != null ? avgResponseTime : 0.0);
        
        // Total study time
        Long totalTimeSpent = userProgressRepository.getTotalTimeSpentByUser(userId);
        stats.setTotalStudyTimeMinutes(totalTimeSpent != null ? totalTimeSpent / 60 : 0);
        
        // Topics studied
        Long topicsStudied = userProgressRepository.countTopicsStartedByUser(userId);
        stats.setTopicsStudied(topicsStudied != null ? topicsStudied.intValue() : 0);
        
        // Mastered questions
        Long masteredQuestions = userProgressRepository.countMasteredByUserId(userId);
        stats.setMasteredQuestions(masteredQuestions != null ? masteredQuestions.intValue() : 0);
        
        // Box distribution
        List<Object[]> boxDistribution = userProgressRepository.getMasteryLevelDistribution(userId);
        Map<String, Long> distribution = new HashMap<>();
        for (Object[] dist : boxDistribution) {
            if (dist[0] != null && dist[1] != null) {
                distribution.put(dist[0].toString(), (Long) dist[1]);
            }
        }
        stats.setMasteryDistribution(distribution);
        
        // Recent activity
        List<QuestionAttempt> recentAttempts = questionAttemptRepository
            .findRecentAttempts(userId, LocalDateTime.now().minusDays(7));
        stats.setRecentActivityCount(recentAttempts.size());
        
        // Learning streak
        LearningStreak streak = getStreak(userId);
        stats.setCurrentStreak(streak.getCurrentStreak());
        stats.setLongestStreak(streak.getLongestStreak());
        
        // Weak areas
        List<WeakArea> weakAreas = identifyWeakAreas(userId);
        stats.setWeakAreasCount(weakAreas.size());
        stats.setWeakAreas(weakAreas.stream()
            .limit(3)
            .map(WeakArea::getTopicName)
            .collect(Collectors.toList()));
        
        log.info("Generated detailed statistics for user {}", userId);
        
        return stats;
    }
    
    /**
     * Get learning pace analysis
     */
    @Transactional(readOnly = true)
    public LearningPace analyzeLearningPace(Long userId) {
        log.debug("Analyzing learning pace for user {}", userId);
        
        LearningPace pace = new LearningPace();
        
        // Get sessions from last 30 days
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<LearningSession> recentSessions = learningSessionRepository
            .findCompletedSessionsInRange(userId, thirtyDaysAgo, LocalDateTime.now());
        
        if (recentSessions.isEmpty()) {
            pace.setPaceCategory("INACTIVE");
            pace.setRecommendation("Start with short 10-question sessions to build momentum");
            return pace;
        }
        
        // Calculate average questions per day
        Map<LocalDate, Integer> dailyQuestions = new HashMap<>();
        for (LearningSession session : recentSessions) {
            LocalDate date = session.getStartTime().toLocalDate();
            dailyQuestions.merge(date, session.getAnsweredQuestions(), Integer::sum);
        }
        
        double avgQuestionsPerActiveDay = dailyQuestions.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
        
        pace.setAverageQuestionsPerDay(avgQuestionsPerActiveDay);
        pace.setActiveDaysLast30(dailyQuestions.size());
        
        // Calculate average session duration
        double avgDuration = recentSessions.stream()
            .filter(s -> s.getDurationMinutes() != null)
            .mapToInt(LearningSession::getDurationMinutes)
            .average()
            .orElse(0.0);
        
        pace.setAverageSessionDuration(avgDuration);
        
        // Determine pace category
        if (dailyQuestions.size() >= 25) {
            pace.setPaceCategory("INTENSIVE");
            pace.setRecommendation("Excellent consistency! Consider varying difficulty levels");
        } else if (dailyQuestions.size() >= 15) {
            pace.setPaceCategory("REGULAR");
            pace.setRecommendation("Good pace! Try to maintain this rhythm");
        } else if (dailyQuestions.size() >= 7) {
            pace.setPaceCategory("MODERATE");
            pace.setRecommendation("Increase frequency to improve retention");
        } else {
            pace.setPaceCategory("LIGHT");
            pace.setRecommendation("Try to practice more frequently for better results");
        }
        
        // Calculate optimal study time
        if (avgDuration < 15) {
            pace.setOptimalSessionLength("20-30 minutes");
        } else if (avgDuration < 30) {
            pace.setOptimalSessionLength("30-45 minutes");
        } else {
            pace.setOptimalSessionLength("45-60 minutes");
        }
        
        log.info("User {} learning pace: {} ({} active days/30)", 
                 userId, pace.getPaceCategory(), dailyQuestions.size());
        
        return pace;
    }
    
    /**
     * Create empty progress for new topic
     */
    private UserProgress createEmptyProgress(Long userId, Long topicId) {
        UserProgress progress = new UserProgress();
        progress.setAttempts(0);
        progress.setCorrectAttempts(0);
        progress.setConfidenceLevel(BigDecimal.ZERO);
        return progress;
    }
    
    /**
     * Get suggested action based on performance
     */
    private String getSuggestedAction(double successRate, BigDecimal confidence) {
        if (successRate < 0.4) {
            return "Review fundamentals and practice with easier questions";
        } else if (successRate < 0.6) {
            return "Focus on understanding explanations and patterns";
        } else if (successRate < 0.8) {
            return "Practice more to build confidence";
        } else if (confidence != null && confidence.doubleValue() < 3) {
            return "Keep practicing to increase confidence";
        } else {
            return "Move to more challenging topics";
        }
    }
    
    // DTOs
    @lombok.Data
    public static class WeakArea {
        private Long topicId;
        private String topicName;
        private double successRate;
        private double confidenceLevel;
        private int questionCount;
        private String suggestedAction;
    }
    
    @lombok.Data
    public static class LearningStreak {
        private int currentStreak;
        private int longestStreak;
        private LocalDate lastActivityDate;
        private int totalActiveDays;
    }
    
    @lombok.Data
    public static class DetailedStatistics {
        private Long userId;
        private int totalQuestionsAttempted;
        private int correctAnswers;
        private double overallSuccessRate;
        private double averageResponseTime;
        private long totalStudyTimeMinutes;
        private int topicsStudied;
        private int masteredQuestions;
        private Map<String, Long> masteryDistribution;
        private int recentActivityCount;
        private int currentStreak;
        private int longestStreak;
        private int weakAreasCount;
        private List<String> weakAreas;
    }
    
    @lombok.Data
    public static class LearningPace {
        private double averageQuestionsPerDay;
        private int activeDaysLast30;
        private double averageSessionDuration;
        private String paceCategory;
        private String optimalSessionLength;
        private String recommendation;
    }
}