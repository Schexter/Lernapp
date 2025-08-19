package com.fachinformatiker.lernapp.service.session;

import com.fachinformatiker.lernapp.dto.LearningSessionDTO;
import com.fachinformatiker.lernapp.dto.QuestionDTO;
import com.fachinformatiker.lernapp.enums.DifficultyLevel;
import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.repository.*;
import com.fachinformatiker.lernapp.service.learning.SpacedRepetitionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for managing learning sessions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LearningSessionService {
    
    private final LearningSessionRepository learningSessionRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final QuestionAttemptRepository questionAttemptRepository;
    private final SpacedRepetitionService spacedRepetitionService;
    
    public enum SessionType {
        PRACTICE,        // Practice mode with selected topics
        REVIEW,          // Spaced repetition review
        EXAM,           // Exam simulation
        QUICK_TEST,     // Quick 10-question test
        WEAKNESS_TRAINING // Focus on weak areas
    }
    
    /**
     * Start a new learning session
     */
    @Transactional
    public LearningSession startSession(Long userId, SessionType type, Long topicId, Integer questionCount) {
        log.info("Starting {} session for user {} with topic {}", type, userId, topicId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        LearningSession session = new LearningSession();
        session.setUser(user);
        session.setSessionType(type.name());
        session.setStartTime(LocalDateTime.now());
        session.setActive(true);
        session.setTotalQuestions(questionCount != null ? questionCount : getDefaultQuestionCount(type));
        session.setAnsweredQuestions(0);
        session.setCorrectAnswers(0);
        
        // Get questions based on session type
        List<Question> questions = selectQuestionsForSession(userId, type, topicId, session.getTotalQuestions());
        session.setQuestions(new HashSet<>(questions));
        
        if (topicId != null) {
            Topic topic = new Topic();
            topic.setId(topicId);
            session.setTopic(topic);
        }
        
        session = learningSessionRepository.save(session);
        log.info("Started session {} with {} questions", session.getId(), questions.size());
        
        return session;
    }
    
    /**
     * Submit an answer for a question in a session
     */
    @Transactional
    public AnswerResult submitAnswer(Long sessionId, Long questionId, String answer, Integer responseTime) {
        log.debug("Submitting answer for session {}, question {}", sessionId, questionId);
        
        LearningSession session = learningSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        
        if (!session.isActive()) {
            throw new IllegalStateException("Session is not active");
        }
        
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new IllegalArgumentException("Question not found: " + questionId));
        
        // Check if answer is correct
        String correctAnswer = question.getCorrectAnswerString();
        boolean isCorrect = correctAnswer != null && correctAnswer.equalsIgnoreCase(answer);
        
        // Create question attempt record
        QuestionAttempt attempt = new QuestionAttempt();
        attempt.setLearningSession(session);
        attempt.setQuestion(question);
        attempt.setUser(session.getUser());
        attempt.setGivenAnswer(answer);
        attempt.setCorrect(isCorrect);
        attempt.setResponseTime(responseTime);
        attempt.setAttemptTime(LocalDateTime.now());
        questionAttemptRepository.save(attempt);
        
        // Update session statistics
        session.setAnsweredQuestions(session.getAnsweredQuestions() + 1);
        if (isCorrect) {
            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
        }
        
        // Update user progress with spaced repetition
        spacedRepetitionService.calculateNextReview(questionId, session.getUser().getId(), isCorrect);
        
        // Update user progress statistics
        updateUserProgress(session.getUser().getId(), questionId, isCorrect, responseTime);
        
        learningSessionRepository.save(session);
        
        // Create result
        AnswerResult result = new AnswerResult();
        result.setCorrect(isCorrect);
        result.setCorrectAnswer(correctAnswer != null ? correctAnswer : "A");
        result.setExplanation(question.getExplanation());
        result.setSessionProgress(session.getAnsweredQuestions() + "/" + session.getTotalQuestions());
        result.setAccuracyRate(calculateAccuracy(session));
        
        return result;
    }
    
    /**
     * End a learning session
     */
    @Transactional
    public SessionResult endSession(Long sessionId) {
        log.info("Ending session {}", sessionId);
        
        LearningSession session = learningSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        
        session.setActive(false);
        session.setEndTime(LocalDateTime.now());
        session.setCompleted(true);
        
        // Calculate duration
        Duration duration = Duration.between(session.getStartTime(), session.getEndTime());
        session.setDurationMinutes((int) duration.toMinutes());
        
        // Calculate final score
        double score = calculateScore(session);
        session.setScore(score);
        
        learningSessionRepository.save(session);
        
        // Create session result
        SessionResult result = new SessionResult();
        result.setSessionId(sessionId);
        result.setTotalQuestions(session.getTotalQuestions());
        result.setAnsweredQuestions(session.getAnsweredQuestions());
        result.setCorrectAnswers(session.getCorrectAnswers());
        result.setScore(score);
        result.setDurationMinutes(session.getDurationMinutes());
        result.setAccuracyRate(calculateAccuracy(session));
        result.setCompleted(session.getAnsweredQuestions() >= session.getTotalQuestions());
        
        // Get weak areas
        result.setWeakAreas(identifyWeakAreas(sessionId));
        
        log.info("Session {} ended with score {}", sessionId, score);
        
        return result;
    }
    
    /**
     * Get session statistics
     */
    @Transactional(readOnly = true)
    public SessionStats getSessionStats(Long sessionId) {
        LearningSession session = learningSessionRepository.findById(sessionId)
            .orElseThrow(() -> new IllegalArgumentException("Session not found: " + sessionId));
        
        SessionStats stats = new SessionStats();
        stats.setSessionId(sessionId);
        stats.setSessionType(session.getSessionType());
        stats.setStartTime(session.getStartTime());
        stats.setTotalQuestions(session.getTotalQuestions());
        stats.setAnsweredQuestions(session.getAnsweredQuestions());
        stats.setCorrectAnswers(session.getCorrectAnswers());
        stats.setAccuracyRate(calculateAccuracy(session));
        
        if (session.getEndTime() != null) {
            Duration duration = Duration.between(session.getStartTime(), session.getEndTime());
            stats.setDurationMinutes((int) duration.toMinutes());
        }
        
        // Get question breakdown by difficulty
        List<QuestionAttempt> attempts = questionAttemptRepository.findByLearningSessionId(sessionId);
        Map<Integer, Long> difficultyBreakdown = attempts.stream()
            .collect(Collectors.groupingBy(
                a -> a.getQuestion().getDifficultyLevel(),
                Collectors.counting()
            ));
        stats.setDifficultyBreakdown(difficultyBreakdown);
        
        // Calculate average response time
        double avgResponseTime = attempts.stream()
            .filter(a -> a.getResponseTime() != null)
            .mapToInt(QuestionAttempt::getResponseTime)
            .average()
            .orElse(0.0);
        stats.setAverageResponseTime(avgResponseTime);
        
        return stats;
    }
    
    /**
     * Select questions for a session based on type
     */
    private List<Question> selectQuestionsForSession(Long userId, SessionType type, Long topicId, int count) {
        List<Question> questions = new ArrayList<>();
        
        switch (type) {
            case REVIEW:
                // Use spaced repetition algorithm
                for (int i = 0; i < count; i++) {
                    Question q = spacedRepetitionService.getNextQuestion(userId, topicId);
                    if (q != null && !questions.contains(q)) {
                        questions.add(q);
                    }
                }
                break;
                
            case WEAKNESS_TRAINING:
                // Focus on questions with low success rate
                questions = getWeakQuestions(userId, topicId, count);
                break;
                
            case EXAM:
                // Get balanced mix of difficulties
                questions = getExamQuestions(topicId, count);
                break;
                
            case QUICK_TEST:
                // Random questions
                questions = getRandomQuestions(topicId, Math.min(count, 10));
                break;
                
            case PRACTICE:
            default:
                // Standard practice questions
                if (topicId != null) {
                    questions = questionRepository.findRandomQuestionsByTopic(topicId, count);
                } else {
                    questions = questionRepository.findRandomQuestions(PageRequest.of(0, count))
                        .getContent();
                }
                break;
        }
        
        // Fill with random questions if not enough
        while (questions.size() < count) {
            List<Question> additionalQuestions;
            if (topicId != null) {
                additionalQuestions = questionRepository.findRandomQuestionsByTopic(
                    topicId, count - questions.size());
            } else {
                additionalQuestions = questionRepository.findRandomQuestions(
                    PageRequest.of(0, count - questions.size())).getContent();
            }
            
            for (Question q : additionalQuestions) {
                if (!questions.contains(q)) {
                    questions.add(q);
                }
            }
            
            // Prevent infinite loop
            if (additionalQuestions.isEmpty()) {
                break;
            }
        }
        
        return questions;
    }
    
    /**
     * Get questions where user has low success rate
     */
    private List<Question> getWeakQuestions(Long userId, Long topicId, int count) {
        List<UserProgress> weakProgress = userProgressRepository
            .findByUserId(userId)
            .stream()
            .filter(p -> p.getAttempts() > 0)
            .filter(p -> (double) p.getCorrectAttempts() / p.getAttempts() < 0.6)
            .sorted((p1, p2) -> {
                double rate1 = (double) p1.getCorrectAttempts() / p1.getAttempts();
                double rate2 = (double) p2.getCorrectAttempts() / p2.getAttempts();
                return Double.compare(rate1, rate2);
            })
            .limit(count)
            .collect(Collectors.toList());
        
        if (topicId != null) {
            weakProgress = weakProgress.stream()
                .filter(p -> p.getQuestion().getTopic().getId().equals(topicId))
                .collect(Collectors.toList());
        }
        
        return weakProgress.stream()
            .map(UserProgress::getQuestion)
            .collect(Collectors.toList());
    }
    
    /**
     * Get balanced questions for exam
     */
    private List<Question> getExamQuestions(Long topicId, int count) {
        List<Question> questions = new ArrayList<>();
        
        // Distribution: 30% easy, 40% medium, 20% hard, 10% expert
        int easyCount = (int) (count * 0.3);
        int mediumCount = (int) (count * 0.4);
        int hardCount = (int) (count * 0.2);
        int expertCount = count - easyCount - mediumCount - hardCount;
        
        if (topicId != null) {
            questions.addAll(questionRepository.findByTopicAndDifficulty(topicId, 1)
                .stream().limit(easyCount).collect(Collectors.toList()));
            questions.addAll(questionRepository.findByTopicAndDifficulty(topicId, 2)
                .stream().limit(mediumCount).collect(Collectors.toList()));
            questions.addAll(questionRepository.findByTopicAndDifficulty(topicId, 3)
                .stream().limit(hardCount).collect(Collectors.toList()));
            questions.addAll(questionRepository.findByTopicAndDifficulty(topicId, 4)
                .stream().limit(expertCount).collect(Collectors.toList()));
        }
        
        Collections.shuffle(questions);
        return questions;
    }
    
    /**
     * Get random questions
     */
    private List<Question> getRandomQuestions(Long topicId, int count) {
        if (topicId != null) {
            return questionRepository.findRandomQuestionsByTopic(topicId, count);
        } else {
            return questionRepository.findRandomQuestions(PageRequest.of(0, count))
                .getContent();
        }
    }
    
    /**
     * Update user progress after answering
     */
    private void updateUserProgress(Long userId, Long questionId, boolean correct, Integer responseTime) {
        UserProgress progress = userProgressRepository
            .findByUserIdAndQuestionId(userId, questionId)
            .orElseGet(() -> {
                UserProgress newProgress = new UserProgress();
                newProgress.setUser(userRepository.findById(userId).orElse(null));
                newProgress.setQuestion(questionRepository.findById(questionId).orElse(null));
                newProgress.setAttempts(0);
                newProgress.setCorrectAttempts(0);
                return newProgress;
            });
        
        progress.setAttempts(progress.getAttempts() + 1);
        if (correct) {
            progress.setCorrectAttempts(progress.getCorrectAttempts() + 1);
        }
        progress.setLastAttempt(LocalDateTime.now());
        
        // Update confidence level
        double successRate = (double) progress.getCorrectAttempts() / progress.getAttempts();
        progress.setConfidenceLevel(java.math.BigDecimal.valueOf(successRate * 5));
        
        userProgressRepository.save(progress);
    }
    
    /**
     * Calculate session accuracy
     */
    private double calculateAccuracy(LearningSession session) {
        if (session.getAnsweredQuestions() == 0) {
            return 0.0;
        }
        return (double) session.getCorrectAnswers() / session.getAnsweredQuestions() * 100;
    }
    
    /**
     * Calculate session score
     */
    private double calculateScore(LearningSession session) {
        if (session.getTotalQuestions() == 0) {
            return 0.0;
        }
        
        double accuracy = (double) session.getCorrectAnswers() / session.getTotalQuestions();
        double completion = (double) session.getAnsweredQuestions() / session.getTotalQuestions();
        
        // Score = 70% accuracy + 30% completion
        return (accuracy * 0.7 + completion * 0.3) * 100;
    }
    
    /**
     * Identify weak areas from session
     */
    private List<String> identifyWeakAreas(Long sessionId) {
        List<QuestionAttempt> attempts = questionAttemptRepository.findByLearningSessionId(sessionId);
        
        Map<String, List<QuestionAttempt>> topicAttempts = attempts.stream()
            .collect(Collectors.groupingBy(a -> a.getQuestion().getTopic().getName()));
        
        List<String> weakAreas = new ArrayList<>();
        
        topicAttempts.forEach((topic, topicAttemptList) -> {
            long correct = topicAttemptList.stream().filter(QuestionAttempt::isCorrect).count();
            double successRate = (double) correct / topicAttemptList.size();
            
            if (successRate < 0.6) {
                weakAreas.add(topic + " (" + Math.round(successRate * 100) + "% richtig)");
            }
        });
        
        return weakAreas;
    }
    
    /**
     * Get default question count for session type
     */
    private int getDefaultQuestionCount(SessionType type) {
        switch (type) {
            case QUICK_TEST:
                return 10;
            case EXAM:
                return 60;
            case REVIEW:
                return 20;
            case WEAKNESS_TRAINING:
                return 15;
            case PRACTICE:
            default:
                return 25;
        }
    }
    
    // DTOs
    @lombok.Data
    public static class AnswerResult {
        private boolean correct;
        private String correctAnswer;
        private String explanation;
        private String sessionProgress;
        private double accuracyRate;
    }
    
    @lombok.Data
    public static class SessionResult {
        private Long sessionId;
        private int totalQuestions;
        private int answeredQuestions;
        private int correctAnswers;
        private double score;
        private int durationMinutes;
        private double accuracyRate;
        private boolean completed;
        private List<String> weakAreas;
    }
    
    @lombok.Data
    public static class SessionStats {
        private Long sessionId;
        private String sessionType;
        private LocalDateTime startTime;
        private int totalQuestions;
        private int answeredQuestions;
        private int correctAnswers;
        private double accuracyRate;
        private int durationMinutes;
        private Map<Integer, Long> difficultyBreakdown;
        private double averageResponseTime;
    }
}