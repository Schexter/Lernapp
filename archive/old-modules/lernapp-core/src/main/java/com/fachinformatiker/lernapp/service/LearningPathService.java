package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class LearningPathService {
    
    private final LearningPathRepository learningPathRepository;
    private final TopicRepository topicRepository;
    private final UserRepository userRepository;
    private final UserProgressRepository userProgressRepository;
    private final QuestionRepository questionRepository;
    
    public Optional<LearningPath> findById(Long id) {
        log.debug("Finding learning path by id: {}", id);
        return learningPathRepository.findById(id);
    }
    
    public Optional<LearningPath> findByIdWithTopics(Long id) {
        log.debug("Finding learning path with topics by id: {}", id);
        return learningPathRepository.findByIdWithTopics(id);
    }
    
    public Optional<LearningPath> findByName(String name) {
        log.debug("Finding learning path by name: {}", name);
        return learningPathRepository.findByName(name);
    }
    
    public Page<LearningPath> findAll(Pageable pageable) {
        log.debug("Finding all learning paths with pagination");
        return learningPathRepository.findAll(pageable);
    }
    
    public Page<LearningPath> findAllActive(Pageable pageable) {
        log.debug("Finding all active learning paths");
        return learningPathRepository.findByActiveTrue(pageable);
    }
    
    public List<LearningPath> findByDifficultyLevel(Integer level) {
        log.debug("Finding learning paths by difficulty level: {}", level);
        return learningPathRepository.findByDifficultyLevel(level);
    }
    
    public Page<LearningPath> searchLearningPaths(String searchTerm, Pageable pageable) {
        log.debug("Searching learning paths with term: {}", searchTerm);
        return learningPathRepository.searchPaths(searchTerm, pageable);
    }
    
    public LearningPath createLearningPath(LearningPath learningPath, Long creatorId) {
        log.info("Creating new learning path: {}", learningPath.getName());
        
        validateLearningPath(learningPath);
        
        // Set creator
        User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new RuntimeException("Creator not found"));
        learningPath.setCreatedBy(creator);
        
        // Set defaults
        learningPath.setActive(true);
        learningPath.setSortOrder(learningPath.getSortOrder() != null ? learningPath.getSortOrder() : 0);
        learningPath.setDifficultyLevel(learningPath.getDifficultyLevel() != null ? learningPath.getDifficultyLevel() : 1);
        learningPath.setCertificationAvailable(learningPath.getCertificationAvailable() != null ? 
            learningPath.getCertificationAvailable() : false);
        learningPath.setCreatedAt(LocalDateTime.now());
        learningPath.setUpdatedAt(LocalDateTime.now());
        
        // Process topics
        if (learningPath.getTopics() != null && !learningPath.getTopics().isEmpty()) {
            List<Topic> topics = new ArrayList<>();
            for (Topic topic : learningPath.getTopics()) {
                Topic existingTopic = topicRepository.findById(topic.getId())
                    .orElseThrow(() -> new RuntimeException("Topic not found: " + topic.getId()));
                topics.add(existingTopic);
            }
            learningPath.setTopics(topics);
        }
        
        // Calculate estimated hours if not provided
        if (learningPath.getEstimatedHours() == null) {
            learningPath.setEstimatedHours(calculateEstimatedHours(learningPath));
        }
        
        LearningPath savedPath = learningPathRepository.save(learningPath);
        log.info("Learning path created successfully: {}", savedPath.getId());
        
        return savedPath;
    }
    
    public LearningPath updateLearningPath(LearningPath learningPath) {
        log.info("Updating learning path: {}", learningPath.getId());
        
        LearningPath existingPath = learningPathRepository.findById(learningPath.getId())
            .orElseThrow(() -> new RuntimeException("Learning path not found"));
        
        // Update fields
        existingPath.setName(learningPath.getName());
        existingPath.setDescription(learningPath.getDescription());
        existingPath.setShortDescription(learningPath.getShortDescription());
        existingPath.setIconUrl(learningPath.getIconUrl());
        existingPath.setBannerUrl(learningPath.getBannerUrl());
        existingPath.setDifficultyLevel(learningPath.getDifficultyLevel());
        existingPath.setEstimatedHours(learningPath.getEstimatedHours());
        existingPath.setPrerequisites(learningPath.getPrerequisites());
        existingPath.setLearningObjectives(learningPath.getLearningObjectives());
        existingPath.setTargetAudience(learningPath.getTargetAudience());
        existingPath.setCertificationAvailable(learningPath.getCertificationAvailable());
        existingPath.setSortOrder(learningPath.getSortOrder());
        existingPath.setUpdatedAt(LocalDateTime.now());
        
        // Update topics if provided
        if (learningPath.getTopics() != null) {
            List<Topic> topics = new ArrayList<>();
            for (Topic topic : learningPath.getTopics()) {
                Topic existingTopic = topicRepository.findById(topic.getId())
                    .orElseThrow(() -> new RuntimeException("Topic not found: " + topic.getId()));
                topics.add(existingTopic);
            }
            existingPath.setTopics(topics);
        }
        
        return learningPathRepository.save(existingPath);
    }
    
    public void deleteLearningPath(Long pathId) {
        log.warn("Deleting learning path: {}", pathId);
        
        LearningPath path = learningPathRepository.findById(pathId)
            .orElseThrow(() -> new RuntimeException("Learning path not found"));
        
        // Check if there are enrolled users
        Long enrolledCount = learningPathRepository.countEnrolledUsers(pathId);
        if (enrolledCount > 0) {
            throw new RuntimeException("Cannot delete learning path with enrolled users");
        }
        
        path.setActive(false);
        path.setUpdatedAt(LocalDateTime.now());
        learningPathRepository.save(path);
        
        log.info("Learning path soft deleted: {}", pathId);
    }
    
    // User enrollment
    public void enrollUser(Long userId, Long pathId) {
        log.info("Enrolling user {} in learning path {}", userId, pathId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        LearningPath path = learningPathRepository.findById(pathId)
            .orElseThrow(() -> new RuntimeException("Learning path not found"));
        
        if (user.getEnrolledPaths().contains(path)) {
            throw new RuntimeException("User already enrolled in this path");
        }
        
        user.getEnrolledPaths().add(path);
        userRepository.save(user);
        
        log.info("User enrolled successfully");
    }
    
    public void unenrollUser(Long userId, Long pathId) {
        log.info("Unenrolling user {} from learning path {}", userId, pathId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        LearningPath path = learningPathRepository.findById(pathId)
            .orElseThrow(() -> new RuntimeException("Learning path not found"));
        
        user.getEnrolledPaths().remove(path);
        userRepository.save(user);
        
        log.info("User unenrolled successfully");
    }
    
    public List<LearningPath> getUserEnrolledPaths(Long userId) {
        log.debug("Getting enrolled paths for user: {}", userId);
        return learningPathRepository.findEnrolledPathsByUser(userId);
    }
    
    public List<LearningPath> getAvailablePathsForUser(Long userId) {
        log.debug("Getting available paths for user: {}", userId);
        return learningPathRepository.findAvailablePathsForUser(userId);
    }
    
    // Progress tracking
    public Map<String, Object> getPathProgress(Long userId, Long pathId) {
        log.debug("Getting progress for user {} in path {}", userId, pathId);
        
        LearningPath path = learningPathRepository.findByIdWithTopics(pathId)
            .orElseThrow(() -> new RuntimeException("Learning path not found"));
        
        Map<String, Object> progress = new HashMap<>();
        progress.put("pathId", path.getId());
        progress.put("pathName", path.getName());
        progress.put("totalTopics", path.getTopics().size());
        
        // Calculate total questions in path
        int totalQuestions = path.getTotalQuestions();
        progress.put("totalQuestions", totalQuestions);
        
        // Get user progress for all topics in path
        int questionsAttempted = 0;
        int questionsMastered = 0;
        double totalConfidence = 0.0;
        int topicsWithProgress = 0;
        
        List<Map<String, Object>> topicProgressList = new ArrayList<>();
        
        for (Topic topic : path.getTopics()) {
            Map<String, Object> topicProgress = new HashMap<>();
            topicProgress.put("topicId", topic.getId());
            topicProgress.put("topicName", topic.getName());
            
            List<UserProgress> userProgress = userProgressRepository.findByUserIdAndTopicId(userId, topic.getId());
            
            if (!userProgress.isEmpty()) {
                topicsWithProgress++;
                questionsAttempted += userProgress.size();
                
                long mastered = userProgress.stream()
                    .filter(p -> p.getMasteryLevel() == UserProgress.MasteryLevel.MASTERED)
                    .count();
                questionsMastered += mastered;
                
                double avgConfidence = userProgress.stream()
                    .mapToDouble(p -> p.getConfidenceLevel().doubleValue())
                    .average()
                    .orElse(0.0);
                
                totalConfidence += avgConfidence;
                
                topicProgress.put("questionsAttempted", userProgress.size());
                topicProgress.put("questionsMastered", mastered);
                topicProgress.put("averageConfidence", avgConfidence);
            } else {
                topicProgress.put("questionsAttempted", 0);
                topicProgress.put("questionsMastered", 0);
                topicProgress.put("averageConfidence", 0.0);
            }
            
            Long topicQuestionCount = questionRepository.countActiveByTopicId(topic.getId());
            topicProgress.put("totalQuestions", topicQuestionCount);
            
            double completionPercentage = topicQuestionCount > 0 ? 
                (userProgress.size() * 100.0 / topicQuestionCount) : 0.0;
            topicProgress.put("completionPercentage", completionPercentage);
            
            topicProgressList.add(topicProgress);
        }
        
        progress.put("topicProgress", topicProgressList);
        progress.put("questionsAttempted", questionsAttempted);
        progress.put("questionsMastered", questionsMastered);
        progress.put("topicsStarted", topicsWithProgress);
        
        // Calculate overall metrics
        double overallCompletion = totalQuestions > 0 ? 
            (questionsAttempted * 100.0 / totalQuestions) : 0.0;
        progress.put("overallCompletion", overallCompletion);
        
        double masteryPercentage = totalQuestions > 0 ? 
            (questionsMastered * 100.0 / totalQuestions) : 0.0;
        progress.put("masteryPercentage", masteryPercentage);
        
        double averageConfidence = topicsWithProgress > 0 ? 
            (totalConfidence / topicsWithProgress) : 0.0;
        progress.put("averageConfidence", averageConfidence);
        
        // Estimate time to complete
        if (path.getEstimatedHours() != null && overallCompletion > 0) {
            double hoursRemaining = path.getEstimatedHours() * (1 - overallCompletion / 100);
            progress.put("estimatedHoursRemaining", hoursRemaining);
        }
        
        return progress;
    }
    
    // Statistics
    public Page<Object[]> getMostPopularPaths(Pageable pageable) {
        log.debug("Getting most popular learning paths");
        return learningPathRepository.findMostPopularPaths(pageable);
    }
    
    public Page<LearningPath> getNewestPaths(Pageable pageable) {
        log.debug("Getting newest learning paths");
        return learningPathRepository.findNewestPaths(pageable);
    }
    
    public List<LearningPath> getRecommendedPaths(Long userId) {
        log.debug("Getting recommended paths for user: {}", userId);
        
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Get user's preferred topics
        Set<Topic> preferredTopics = user.getPreferredTopics();
        
        if (preferredTopics.isEmpty()) {
            // Return popular paths if no preferences
            return learningPathRepository.findMostPopularPaths(Pageable.ofSize(5))
                .map(result -> (LearningPath) result[0])
                .getContent();
        }
        
        // Find paths containing preferred topics
        Set<LearningPath> recommendedPaths = new HashSet<>();
        
        for (Topic topic : preferredTopics) {
            List<LearningPath> pathsWithTopic = learningPathRepository.findByTopicId(topic.getId());
            recommendedPaths.addAll(pathsWithTopic);
        }
        
        // Remove already enrolled paths
        List<LearningPath> enrolledPaths = learningPathRepository.findEnrolledPathsByUser(userId);
        recommendedPaths.removeAll(enrolledPaths);
        
        // Sort by relevance (number of matching topics)
        return recommendedPaths.stream()
            .sorted((p1, p2) -> {
                long p1Matches = p1.getTopics().stream()
                    .filter(preferredTopics::contains)
                    .count();
                long p2Matches = p2.getTopics().stream()
                    .filter(preferredTopics::contains)
                    .count();
                return Long.compare(p2Matches, p1Matches);
            })
            .limit(10)
            .collect(Collectors.toList());
    }
    
    // Helper methods
    private void validateLearningPath(LearningPath path) {
        if (path.getName() == null || path.getName().trim().isEmpty()) {
            throw new RuntimeException("Learning path name is required");
        }
        
        if (learningPathRepository.existsByName(path.getName())) {
            throw new RuntimeException("Learning path with this name already exists");
        }
        
        if (path.getDifficultyLevel() != null && 
            (path.getDifficultyLevel() < 1 || path.getDifficultyLevel() > 5)) {
            throw new RuntimeException("Difficulty level must be between 1 and 5");
        }
    }
    
    private Integer calculateEstimatedHours(LearningPath path) {
        if (path.getTopics() == null || path.getTopics().isEmpty()) {
            return 0;
        }
        
        return path.getTopics().stream()
            .mapToInt(topic -> topic.getEstimatedHours() != null ? topic.getEstimatedHours() : 5)
            .sum();
    }
}