package com.fachinformatiker.lernapp.controller;

import com.fachinformatiker.lernapp.dto.*;
import com.fachinformatiker.lernapp.facade.LearningServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * REST Controller für Learning Management
 * KONSISTENT mit LearningServiceFacade
 * 
 * @author Hans Hahn
 */
@RestController
@RequestMapping("/api/v1/learning")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Learning Management", description = "Endpoints für Lernverwaltung und Progress Tracking")
public class LearningController {

    private final LearningServiceFacade learningService;

    @Operation(summary = "Erstelle neue Lernsession")
    @PostMapping("/sessions")
    public ResponseEntity<LearningSessionDTO> createLearningSession(
            @RequestParam Long userId,
            @RequestParam Long topicId,
            @RequestParam(defaultValue = "10") int questionCount) {
        log.info("Creating learning session for user {} on topic {}", userId, topicId);
        LearningSessionDTO session = learningService.createLearningSession(userId, topicId, questionCount);
        return ResponseEntity.status(HttpStatus.CREATED).body(session);
    }

    @Operation(summary = "Hole aktive Lernsession")
    @GetMapping("/sessions/active")
    public ResponseEntity<LearningSessionDTO> getActiveSession(@RequestParam Long userId) {
        log.debug("Fetching active session for user: {}", userId);
        return learningService.getActiveSession(userId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Beende Lernsession")
    @PostMapping("/sessions/{sessionId}/complete")
    public ResponseEntity<LearningSessionDTO> completeSession(@PathVariable Long sessionId) {
        log.info("Completing session: {}", sessionId);
        LearningSessionDTO result = learningService.completeSession(sessionId);
        return ResponseEntity.ok(result);
    }

    @Operation(summary = "Sende Antwort")
    @PostMapping("/answer")
    public ResponseEntity<AnswerSubmissionDTO> submitAnswer(@Valid @RequestBody AnswerSubmissionDTO submission) {
        log.debug("Processing answer submission");
        AnswerSubmissionDTO response = learningService.submitAnswer(submission);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Hole User Progress")
    @GetMapping("/progress/{userId}")
    public ResponseEntity<UserProgressDTO> getUserProgress(
            @PathVariable Long userId,
            @RequestParam(required = false) Long topicId) {
        log.debug("Fetching progress for user: {} on topic: {}", userId, topicId);
        UserProgressDTO progress = topicId != null 
            ? learningService.getUserProgressByTopic(userId, topicId)
            : learningService.getUserProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @Operation(summary = "Hole detaillierten Progress Report")
    @GetMapping("/progress/{userId}/detailed")
    public ResponseEntity<DetailedProgressDTO> getDetailedProgress(
            @PathVariable Long userId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate) {
        log.debug("Fetching detailed progress for user: {}", userId);
        DetailedProgressDTO progress = learningService.getDetailedProgress(userId, startDate, endDate);
        return ResponseEntity.ok(progress);
    }

    @Operation(summary = "Hole Fragen für Review")
    @GetMapping("/review/{userId}")
    public ResponseEntity<List<QuestionDTO>> getReviewQuestions(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Fetching review questions for user: {}", userId);
        List<QuestionDTO> questions = learningService.getQuestionsForReview(userId, limit);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Hole Learning Streak")
    @GetMapping("/streak/{userId}")
    public ResponseEntity<StreakDTO> getLearningStreak(@PathVariable Long userId) {
        log.debug("Fetching learning streak for user: {}", userId);
        StreakDTO streak = learningService.getLearningStreak(userId);
        return ResponseEntity.ok(streak);
    }

    @Operation(summary = "Hole schwache Themen")
    @GetMapping("/weak-topics/{userId}")
    public ResponseEntity<List<TopicProgressDTO>> getWeakTopics(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0.5") double confidenceThreshold) {
        log.debug("Fetching weak topics for user: {} with threshold: {}", userId, confidenceThreshold);
        List<TopicProgressDTO> weakTopics = learningService.getWeakTopics(userId, confidenceThreshold);
        return ResponseEntity.ok(weakTopics);
    }

    @Operation(summary = "Hole Lernempfehlungen")
    @GetMapping("/recommendations/{userId}")
    public ResponseEntity<RecommendationDTO> getLearningRecommendations(@PathVariable Long userId) {
        log.debug("Generating recommendations for user: {}", userId);
        RecommendationDTO recommendations = learningService.generateRecommendations(userId);
        return ResponseEntity.ok(recommendations);
    }

    @Operation(summary = "Hole Lernstatistiken")
    @GetMapping("/statistics/{userId}")
    public ResponseEntity<LearningStatisticsDTO> getLearningStatistics(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {
        log.debug("Fetching learning statistics for user: {} for last {} days", userId, days);
        LearningStatisticsDTO stats = learningService.getLearningStatistics(userId, days);
        return ResponseEntity.ok(stats);
    }

    @Operation(summary = "Hole tägliche Aktivität")
    @GetMapping("/activity/{userId}/daily")
    public ResponseEntity<Map<LocalDate, Integer>> getDailyActivity(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {
        log.debug("Fetching daily activity for user: {} for last {} days", userId, days);
        Map<LocalDate, Integer> activity = learningService.getDailyActivity(userId, days);
        return ResponseEntity.ok(activity);
    }

    @Operation(summary = "Setze Lernziel")
    @PostMapping("/goals")
    public ResponseEntity<LearningGoalDTO> setLearningGoal(@Valid @RequestBody LearningGoalDTO goalDTO) {
        log.info("Setting learning goal for user: {}", goalDTO.getUserId());
        LearningGoalDTO created = learningService.setLearningGoal(goalDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Hole Lernziele")
    @GetMapping("/goals/{userId}")
    public ResponseEntity<List<LearningGoalDTO>> getLearningGoals(@PathVariable Long userId) {
        log.debug("Fetching learning goals for user: {}", userId);
        List<LearningGoalDTO> goals = learningService.getLearningGoals(userId);
        return ResponseEntity.ok(goals);
    }

    @Operation(summary = "Hole Progress für alle Topics")
    @GetMapping("/progress/{userId}/topics")
    public ResponseEntity<List<TopicProgressDTO>> getAllTopicProgress(@PathVariable Long userId) {
        log.debug("Fetching progress for all topics for user: {}", userId);
        List<TopicProgressDTO> progress = learningService.getAllTopicProgress(userId);
        return ResponseEntity.ok(progress);
    }

    @Operation(summary = "Reset Progress für Topic")
    @DeleteMapping("/progress/{userId}/topic/{topicId}")
    public ResponseEntity<Void> resetTopicProgress(
            @PathVariable Long userId,
            @PathVariable Long topicId) {
        log.warn("Resetting progress for user {} on topic {}", userId, topicId);
        learningService.resetTopicProgress(userId, topicId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Hole Leaderboard")
    @GetMapping("/leaderboard")
    public ResponseEntity<List<LeaderboardEntryDTO>> getLeaderboard(
            @RequestParam(required = false) Long topicId,
            @RequestParam(defaultValue = "10") int limit) {
        log.debug("Fetching leaderboard for topic: {} with limit: {}", topicId, limit);
        List<LeaderboardEntryDTO> leaderboard = learningService.getLeaderboard(topicId, limit);
        return ResponseEntity.ok(leaderboard);
    }

    @Operation(summary = "Hole Performance Trend")
    @GetMapping("/performance/{userId}/trend")
    public ResponseEntity<PerformanceTrendDTO> getPerformanceTrend(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "30") int days) {
        log.debug("Fetching performance trend for user: {} for last {} days", userId, days);
        PerformanceTrendDTO trend = learningService.getPerformanceTrend(userId, days);
        return ResponseEntity.ok(trend);
    }
}
