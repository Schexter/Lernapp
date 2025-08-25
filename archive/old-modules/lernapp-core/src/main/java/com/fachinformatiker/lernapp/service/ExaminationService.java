package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.repository.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class ExaminationService {
    
    private final ExaminationSessionRepository sessionRepository;
    private final SessionAnswerRepository sessionAnswerRepository;
    private final QuestionRepository questionRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final LearningService learningService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    public ExaminationSession createSession(
        Long userId,
        ExaminationSession.SessionType sessionType,
        List<Long> topicIds,
        Integer questionCount,
        Integer timeLimitMinutes
    ) {
        log.info("Creating examination session for user: {}", userId);
        
        // Check for existing active session
        Optional<ExaminationSession> activeSession = sessionRepository.findActiveSessionByUser(userId);
        if (activeSession.isPresent()) {
            throw new RuntimeException("User already has an active session");
        }
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get questions based on topics
        List<Question> questions = getQuestionsForSession(topicIds, questionCount, sessionType);
        
        if (questions.isEmpty()) {
            throw new RuntimeException("No questions available for selected topics");
        }
        
        // Create session
        ExaminationSession session = new ExaminationSession();
        session.setUser(user);
        session.setSessionType(sessionType);
        session.setTopicIds(topicIds.stream()
            .map(String::valueOf)
            .collect(Collectors.joining(",")));
        session.setTotalQuestions(questions.size());
        session.setAnsweredQuestions(0);
        session.setCorrectAnswers(0);
        session.setScore(BigDecimal.ZERO);
        session.setTimeLimitMinutes(timeLimitMinutes);
        session.setTimeSpentSeconds(0L);
        session.setStatus(ExaminationSession.SessionStatus.NOT_STARTED);
        session.setActive(true);
        
        // Store question order in session data as JSON string
        Map<String, Object> sessionData = new HashMap<>();
        sessionData.put("questionIds", questions.stream()
            .map(Question::getId)
            .collect(Collectors.toList()));
        sessionData.put("currentQuestionIndex", 0);
        
        try {
            session.setSessionData(objectMapper.writeValueAsString(sessionData));
        } catch (JsonProcessingException e) {
            log.error("Error serializing session data", e);
            session.setSessionData("{}");
        }
        
        ExaminationSession savedSession = sessionRepository.save(session);
        
        log.info("Examination session created: {}", savedSession.getId());
        return savedSession;
    }
    
    public ExaminationSession startSession(Long sessionId) {
        log.info("Starting examination session: {}", sessionId);
        
        ExaminationSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (session.getStatus() != ExaminationSession.SessionStatus.NOT_STARTED) {
            throw new RuntimeException("Session already started");
        }
        
        session.startSession();
        
        return sessionRepository.save(session);
    }
    
    public ExaminationSession pauseSession(Long sessionId) {
        log.info("Pausing examination session: {}", sessionId);
        
        ExaminationSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (session.getStatus() != ExaminationSession.SessionStatus.IN_PROGRESS) {
            throw new RuntimeException("Session is not in progress");
        }
        
        // Update time spent
        updateTimeSpent(session);
        
        session.pauseSession();
        
        return sessionRepository.save(session);
    }
    
    public ExaminationSession resumeSession(Long sessionId) {
        log.info("Resuming examination session: {}", sessionId);
        
        ExaminationSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (session.getStatus() != ExaminationSession.SessionStatus.PAUSED) {
            throw new RuntimeException("Session is not paused");
        }
        
        // Check if time has expired
        if (session.isTimeExpired()) {
            session.setStatus(ExaminationSession.SessionStatus.TIMED_OUT);
            return completeSession(sessionId);
        }
        
        session.resumeSession();
        
        return sessionRepository.save(session);
    }
    
    public SessionAnswer submitAnswer(
        Long sessionId,
        Long questionId,
        Long answerId,
        String textAnswer,
        Integer confidenceLevel
    ) {
        log.info("Submitting answer for session {} question {}", sessionId, questionId);
        
        ExaminationSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (!session.isActive()) {
            throw new RuntimeException("Session is not active");
        }
        
        // Check if already answered
        Optional<SessionAnswer> existingAnswer = sessionAnswerRepository
            .findBySessionIdAndQuestionId(sessionId, questionId);
        
        SessionAnswer sessionAnswer;
        if (existingAnswer.isPresent()) {
            sessionAnswer = existingAnswer.get();
        } else {
            sessionAnswer = new SessionAnswer();
            sessionAnswer.setSession(session);
            
            Question question = questionRepository.findById(questionId)
                .orElseThrow(() -> new RuntimeException("Question not found"));
            sessionAnswer.setQuestion(question);
            
            session.setAnsweredQuestions(session.getAnsweredQuestions() + 1);
        }
        
        // Set answer details
        if (answerId != null) {
            Answer answer = session.getSessionAnswers().stream()
                .filter(sa -> sa.getQuestion().getId().equals(questionId))
                .findFirst()
                .map(SessionAnswer::getSelectedAnswer)
                .orElse(null);
            
            sessionAnswer.setSelectedAnswer(answer);
        }
        
        sessionAnswer.setTextAnswer(textAnswer);
        sessionAnswer.setConfidenceLevel(confidenceLevel);
        sessionAnswer.setAnsweredAt(LocalDateTime.now());
        
        // Calculate time spent on this question
        if (sessionAnswer.getTimeSpentSeconds() == null) {
            // Calculate based on previous answer or session start
            LocalDateTime lastTime = session.getSessionAnswers().stream()
                .map(SessionAnswer::getAnsweredAt)
                .max(LocalDateTime::compareTo)
                .orElse(session.getStartedAt());
            
            long secondsSpent = ChronoUnit.SECONDS.between(lastTime, LocalDateTime.now());
            sessionAnswer.setTimeSpentSeconds((int) secondsSpent);
        }
        
        // Evaluate answer
        sessionAnswer.evaluateAnswer();
        
        if (sessionAnswer.getIsCorrect()) {
            session.setCorrectAnswers(session.getCorrectAnswers() + 1);
        }
        
        // Save answer
        SessionAnswer savedAnswer = sessionAnswerRepository.save(sessionAnswer);
        
        // Update session
        sessionRepository.save(session);
        
        // Also record in learning progress (for spaced repetition)
        if (session.getSessionType() != ExaminationSession.SessionType.EXAMINATION) {
            learningService.recordAnswer(
                session.getUser().getId(),
                questionId,
                sessionAnswer.getIsCorrect(),
                sessionAnswer.getTimeSpentSeconds()
            );
        }
        
        log.info("Answer submitted: {}", sessionAnswer.getIsCorrect() ? "Correct" : "Incorrect");
        return savedAnswer;
    }
    
    public ExaminationSession completeSession(Long sessionId) {
        log.info("Completing examination session: {}", sessionId);
        
        ExaminationSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        if (session.getStatus() == ExaminationSession.SessionStatus.COMPLETED) {
            return session;
        }
        
        // Update time spent
        updateTimeSpent(session);
        
        // Complete session
        session.completeSession();
        
        // Calculate detailed results
        Map<String, Object> results = calculateResults(session);
        try {
            session.setResults(objectMapper.writeValueAsString(results));
        } catch (JsonProcessingException e) {
            log.error("Error serializing results", e);
            session.setResults("{}");
        }
        
        // Award points based on performance
        int pointsEarned = calculatePoints(session);
        userService.addPoints(session.getUser().getId(), pointsEarned);
        
        ExaminationSession savedSession = sessionRepository.save(session);
        
        log.info("Session completed. Score: {}%, Points earned: {}", 
            session.getScore(), pointsEarned);
        
        return savedSession;
    }
    
    public ExaminationSession abandonSession(Long sessionId) {
        log.warn("Abandoning examination session: {}", sessionId);
        
        ExaminationSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        updateTimeSpent(session);
        session.setStatus(ExaminationSession.SessionStatus.ABANDONED);
        
        return sessionRepository.save(session);
    }
    
    // Get session details
    public ExaminationSession getSession(Long sessionId) {
        return sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
    }
    
    public Page<ExaminationSession> getUserSessions(Long userId, Pageable pageable) {
        return sessionRepository.findByUserId(userId, pageable);
    }
    
    public List<SessionAnswer> getSessionAnswers(Long sessionId) {
        return sessionAnswerRepository.findBySessionIdOrderByAnsweredAtAsc(sessionId);
    }
    
    // Statistics
    public Map<String, Object> getSessionStatistics(Long sessionId) {
        log.debug("Getting statistics for session: {}", sessionId);
        
        ExaminationSession session = sessionRepository.findById(sessionId)
            .orElseThrow(() -> new RuntimeException("Session not found"));
        
        Map<String, Object> stats = new HashMap<>();
        
        // Basic stats
        stats.put("totalQuestions", session.getTotalQuestions());
        stats.put("answeredQuestions", session.getAnsweredQuestions());
        stats.put("correctAnswers", session.getCorrectAnswers());
        stats.put("score", session.getScore());
        stats.put("timeSpentSeconds", session.getTimeSpentSeconds());
        
        // Answer statistics
        List<SessionAnswer> answers = sessionAnswerRepository.findBySessionId(sessionId);
        
        if (!answers.isEmpty()) {
            // Time statistics
            Double avgTimePerQuestion = sessionAnswerRepository.getAverageTimeSpentBySessionId(sessionId);
            stats.put("averageTimePerQuestion", avgTimePerQuestion);
            
            int maxTime = answers.stream()
                .mapToInt(SessionAnswer::getTimeSpentSeconds)
                .max()
                .orElse(0);
            stats.put("maxTimePerQuestion", maxTime);
            
            int minTime = answers.stream()
                .mapToInt(SessionAnswer::getTimeSpentSeconds)
                .min()
                .orElse(0);
            stats.put("minTimePerQuestion", minTime);
            
            // Confidence statistics
            double avgConfidence = answers.stream()
                .filter(a -> a.getConfidenceLevel() != null)
                .mapToInt(SessionAnswer::getConfidenceLevel)
                .average()
                .orElse(0.0);
            stats.put("averageConfidence", avgConfidence);
            
            // Topic breakdown
            List<Object[]> topicStats = sessionAnswerRepository.getStatisticsByTopicForSession(sessionId);
            List<Map<String, Object>> topicBreakdown = new ArrayList<>();
            
            for (Object[] row : topicStats) {
                Map<String, Object> topicStat = new HashMap<>();
                topicStat.put("topicId", row[0]);
                topicStat.put("totalQuestions", row[1]);
                topicStat.put("correctAnswers", row[2]);
                
                Long total = (Long) row[1];
                Long correct = (Long) row[2];
                double accuracy = total > 0 ? (correct * 100.0 / total) : 0.0;
                topicStat.put("accuracy", accuracy);
                
                topicBreakdown.add(topicStat);
            }
            
            stats.put("topicBreakdown", topicBreakdown);
        }
        
        return stats;
    }
    
    public Map<String, Object> getUserExaminationStatistics(Long userId) {
        log.debug("Getting examination statistics for user: {}", userId);
        
        Map<String, Object> stats = new HashMap<>();
        
        // Overall statistics
        Long totalSessions = sessionRepository.countCompletedSessionsByUser(userId);
        stats.put("totalCompletedSessions", totalSessions);
        
        Double avgScore = sessionRepository.getAverageScoreByUser(userId);
        stats.put("averageScore", avgScore != null ? avgScore : 0.0);
        
        // Statistics by session type
        List<Object[]> typeStats = sessionRepository.getSessionStatisticsByType(userId);
        Map<String, Map<String, Object>> typeStatsMap = new HashMap<>();
        
        for (Object[] row : typeStats) {
            Map<String, Object> typeStat = new HashMap<>();
            typeStat.put("count", row[1]);
            typeStat.put("averageScore", row[2]);
            
            typeStatsMap.put(((ExaminationSession.SessionType) row[0]).name(), typeStat);
        }
        
        stats.put("statisticsByType", typeStatsMap);
        
        // Recent sessions
        Page<ExaminationSession> recentSessions = sessionRepository.findRecentSessions(
            userId, Pageable.ofSize(5)
        );
        stats.put("recentSessions", recentSessions.getContent());
        
        // High scores
        List<ExaminationSession> highScores = sessionRepository.findHighScoreSessions(userId, 80.0);
        stats.put("highScoreSessions", highScores.size());
        
        return stats;
    }
    
    // Helper methods for JSON parsing
    public Map<String, Object> parseSessionData(String sessionData) {
        try {
            return objectMapper.readValue(sessionData, Map.class);
        } catch (Exception e) {
            log.error("Error parsing session data", e);
            return new HashMap<>();
        }
    }
    
    public Map<String, Object> parseResults(String results) {
        try {
            return objectMapper.readValue(results, Map.class);
        } catch (Exception e) {
            log.error("Error parsing results", e);
            return new HashMap<>();
        }
    }
    
    // Helper methods
    private List<Question> getQuestionsForSession(
        List<Long> topicIds,
        Integer questionCount,
        ExaminationSession.SessionType sessionType
    ) {
        List<Question> allQuestions = new ArrayList<>();
        
        for (Long topicId : topicIds) {
            List<Question> topicQuestions = questionRepository.findByTopicIdAndActive(topicId, true);
            allQuestions.addAll(topicQuestions);
        }
        
        // Shuffle and limit
        Collections.shuffle(allQuestions);
        
        int limit = questionCount != null ? 
            Math.min(questionCount, allQuestions.size()) : 
            allQuestions.size();
        
        return allQuestions.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    private void updateTimeSpent(ExaminationSession session) {
        if (session.getStartedAt() != null) {
            long secondsSpent = ChronoUnit.SECONDS.between(
                session.getStartedAt(), 
                LocalDateTime.now()
            );
            session.setTimeSpentSeconds(secondsSpent);
        }
    }
    
    private Map<String, Object> calculateResults(ExaminationSession session) {
        Map<String, Object> results = new HashMap<>();
        
        // Calculate percentage score
        if (session.getTotalQuestions() > 0) {
            double percentage = (session.getCorrectAnswers() * 100.0) / session.getTotalQuestions();
            results.put("percentageScore", percentage);
            
            // Grade
            String grade;
            if (percentage >= 90) grade = "A";
            else if (percentage >= 80) grade = "B";
            else if (percentage >= 70) grade = "C";
            else if (percentage >= 60) grade = "D";
            else grade = "F";
            
            results.put("grade", grade);
        }
        
        // Time analysis
        results.put("totalTimeSeconds", session.getTimeSpentSeconds());
        results.put("averageTimePerQuestion", 
            session.getTotalQuestions() > 0 ? 
            session.getTimeSpentSeconds() / session.getTotalQuestions() : 0);
        
        return results;
    }
    
    private int calculatePoints(ExaminationSession session) {
        int basePoints = session.getCorrectAnswers() * 10;
        
        // Bonus for high accuracy
        if (session.getScore().compareTo(new BigDecimal("90")) >= 0) {
            basePoints += 100;
        } else if (session.getScore().compareTo(new BigDecimal("80")) >= 0) {
            basePoints += 50;
        }
        
        // Bonus for completing examination
        if (session.getSessionType() == ExaminationSession.SessionType.EXAMINATION) {
            basePoints *= 2;
        }
        
        return basePoints;
    }
    
    // Cleanup timed out sessions
    public void cleanupTimedOutSessions() {
        log.info("Cleaning up timed out sessions");
        
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusHours(24);
        List<ExaminationSession> timedOutSessions = sessionRepository.findTimedOutSessions(timeoutThreshold);
        
        for (ExaminationSession session : timedOutSessions) {
            session.setStatus(ExaminationSession.SessionStatus.TIMED_OUT);
            completeSession(session.getId());
        }
        
        log.info("Cleaned up {} timed out sessions", timedOutSessions.size());
    }
}