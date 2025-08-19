package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.Topic;
import com.fachinformatiker.lernapp.model.Question;
import com.fachinformatiker.lernapp.repository.TopicRepository;
import com.fachinformatiker.lernapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class TopicService {
    
    private final TopicRepository topicRepository;
    private final QuestionRepository questionRepository;
    
    @Cacheable(value = "topics", key = "#id")
    public Optional<Topic> findById(Long id) {
        log.debug("Finding topic by id: {}", id);
        return topicRepository.findById(id);
    }
    
    public Optional<Topic> findByIdWithChildren(Long id) {
        log.debug("Finding topic with children by id: {}", id);
        return topicRepository.findByIdWithChildren(id);
    }
    
    public Optional<Topic> findByIdWithQuestions(Long id) {
        log.debug("Finding topic with questions by id: {}", id);
        return topicRepository.findByIdWithQuestions(id);
    }
    
    public Optional<Topic> findByName(String name) {
        log.debug("Finding topic by name: {}", name);
        return topicRepository.findByName(name);
    }
    
    public List<Topic> findAll() {
        log.debug("Finding all topics");
        return topicRepository.findAll();
    }
    
    public List<Topic> findAllActive() {
        log.debug("Finding all active topics");
        return topicRepository.findAllActive();
    }
    
    public List<Topic> findRootTopics() {
        log.debug("Finding root topics");
        return topicRepository.findByParentIsNullOrderBySortOrderAsc();
    }
    
    public List<Topic> findRootTopicsActive() {
        log.debug("Finding active root topics");
        return topicRepository.findRootTopicsActive();
    }
    
    public List<Topic> findByParentId(Long parentId) {
        log.debug("Finding topics by parent id: {}", parentId);
        return topicRepository.findByParentIdOrderBySortOrderAsc(parentId);
    }
    
    @CacheEvict(value = "topics", allEntries = true)
    public Topic createTopic(Topic topic) {
        log.info("Creating new topic: {}", topic.getName());
        
        validateTopic(topic);
        
        // Set defaults
        topic.setActive(true);
        topic.setSortOrder(topic.getSortOrder() != null ? topic.getSortOrder() : 0);
        topic.setDifficultyLevel(topic.getDifficultyLevel() != null ? topic.getDifficultyLevel() : 1);
        topic.setCreatedAt(LocalDateTime.now());
        topic.setUpdatedAt(LocalDateTime.now());
        
        // Set parent if provided
        if (topic.getParent() != null && topic.getParent().getId() != null) {
            Topic parent = topicRepository.findById(topic.getParent().getId())
                .orElseThrow(() -> new RuntimeException("Parent topic not found"));
            topic.setParent(parent);
        }
        
        Topic savedTopic = topicRepository.save(topic);
        log.info("Topic created successfully: {}", savedTopic.getId());
        
        return savedTopic;
    }
    
    @CacheEvict(value = "topics", key = "#topic.id")
    public Topic updateTopic(Topic topic) {
        log.info("Updating topic: {}", topic.getId());
        
        Topic existingTopic = topicRepository.findById(topic.getId())
            .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        // Update fields
        existingTopic.setName(topic.getName());
        existingTopic.setDescription(topic.getDescription());
        existingTopic.setIconUrl(topic.getIconUrl());
        existingTopic.setColorCode(topic.getColorCode());
        existingTopic.setSortOrder(topic.getSortOrder());
        existingTopic.setDifficultyLevel(topic.getDifficultyLevel());
        existingTopic.setEstimatedHours(topic.getEstimatedHours());
        existingTopic.setUpdatedAt(LocalDateTime.now());
        
        // Update parent if changed
        if (topic.getParent() != null && topic.getParent().getId() != null) {
            if (!topic.getParent().getId().equals(topic.getId())) {
                Topic parent = topicRepository.findById(topic.getParent().getId())
                    .orElseThrow(() -> new RuntimeException("Parent topic not found"));
                existingTopic.setParent(parent);
            } else {
                throw new RuntimeException("Topic cannot be its own parent");
            }
        } else {
            existingTopic.setParent(null);
        }
        
        return topicRepository.save(existingTopic);
    }
    
    @CacheEvict(value = "topics", key = "#topicId")
    public void deleteTopic(Long topicId) {
        log.warn("Deleting topic: {}", topicId);
        
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        // Check if topic has questions
        Long questionCount = questionRepository.countByTopicId(topicId);
        if (questionCount > 0) {
            throw new RuntimeException("Cannot delete topic with existing questions");
        }
        
        // Check if topic has children
        if (!topic.getChildren().isEmpty()) {
            throw new RuntimeException("Cannot delete topic with child topics");
        }
        
        topic.setActive(false);
        topic.setUpdatedAt(LocalDateTime.now());
        topicRepository.save(topic);
        
        log.info("Topic soft deleted: {}", topicId);
    }
    
    public List<Topic> searchTopics(String searchTerm) {
        log.debug("Searching topics with term: {}", searchTerm);
        return topicRepository.searchByName(searchTerm);
    }
    
    public List<Topic> findByDifficultyLevel(Integer level) {
        log.debug("Finding topics by difficulty level: {}", level);
        return topicRepository.findByDifficultyLevel(level);
    }
    
    public List<Topic> findByDifficultyRange(Integer minLevel, Integer maxLevel) {
        log.debug("Finding topics by difficulty range: {}-{}", minLevel, maxLevel);
        return topicRepository.findByDifficultyRange(minLevel, maxLevel);
    }
    
    // Hierarchical operations
    public List<Topic> getTopicHierarchy() {
        log.debug("Building topic hierarchy");
        
        List<Topic> rootTopics = topicRepository.findByParentIsNullOrderBySortOrderAsc();
        
        // Build hierarchy recursively
        for (Topic root : rootTopics) {
            loadChildren(root);
        }
        
        return rootTopics;
    }
    
    private void loadChildren(Topic parent) {
        List<Topic> children = topicRepository.findByParentIdOrderBySortOrderAsc(parent.getId());
        parent.setChildren(children);
        
        for (Topic child : children) {
            loadChildren(child);
        }
    }
    
    public List<Topic> getAllChildrenRecursive(Long topicId) {
        log.debug("Getting all children recursively for topic: {}", topicId);
        
        List<Topic> allChildren = new ArrayList<>();
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        collectChildrenRecursive(topic, allChildren);
        return allChildren;
    }
    
    private void collectChildrenRecursive(Topic parent, List<Topic> collection) {
        List<Topic> children = topicRepository.findByParentId(parent.getId());
        collection.addAll(children);
        
        for (Topic child : children) {
            collectChildrenRecursive(child, collection);
        }
    }
    
    public List<Topic> getBreadcrumb(Long topicId) {
        log.debug("Getting breadcrumb for topic: {}", topicId);
        
        List<Topic> breadcrumb = new ArrayList<>();
        Topic current = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        while (current != null) {
            breadcrumb.add(0, current);
            current = current.getParent();
        }
        
        return breadcrumb;
    }
    
    // Statistics
    public Map<String, Object> getTopicStatistics(Long topicId) {
        log.debug("Getting statistics for topic: {}", topicId);
        
        Topic topic = topicRepository.findById(topicId)
            .orElseThrow(() -> new RuntimeException("Topic not found"));
        
        Map<String, Object> stats = new HashMap<>();
        
        // Basic stats
        stats.put("topicId", topic.getId());
        stats.put("topicName", topic.getName());
        stats.put("difficultyLevel", topic.getDifficultyLevel());
        stats.put("estimatedHours", topic.getEstimatedHours());
        
        // Question count
        Long directQuestions = questionRepository.countByTopicId(topicId);
        stats.put("directQuestions", directQuestions);
        
        // Total questions including children
        int totalQuestions = topic.getTotalQuestions();
        stats.put("totalQuestions", totalQuestions);
        
        // Child topics count
        List<Topic> children = topicRepository.findByParentId(topicId);
        stats.put("childTopicsCount", children.size());
        
        // All descendants count
        List<Topic> allDescendants = getAllChildrenRecursive(topicId);
        stats.put("totalDescendants", allDescendants.size());
        
        // Question difficulty distribution
        if (directQuestions > 0) {
            Double avgDifficulty = questionRepository.getAverageDifficultyByTopic(topicId);
            stats.put("averageQuestionDifficulty", avgDifficulty);
            
            Map<Question.QuestionType, Long> typeDistribution = 
                questionRepository.getQuestionTypeDistributionByTopic(topicId).stream()
                    .collect(Collectors.toMap(
                        r -> (Question.QuestionType) r[0],
                        r -> (Long) r[1]
                    ));
            stats.put("questionTypeDistribution", typeDistribution);
        }
        
        return stats;
    }
    
    public List<Map<String, Object>> getTopicsWithQuestionCount() {
        log.debug("Getting topics with question count");
        
        List<Object[]> results = topicRepository.getTopicsWithQuestionCount();
        List<Map<String, Object>> topicsList = new ArrayList<>();
        
        for (Object[] row : results) {
            Map<String, Object> topicInfo = new HashMap<>();
            topicInfo.put("id", row[0]);
            topicInfo.put("name", row[1]);
            topicInfo.put("questionCount", row[2]);
            topicsList.add(topicInfo);
        }
        
        return topicsList;
    }
    
    public List<Topic> findEmptyTopics() {
        log.debug("Finding empty leaf topics");
        return topicRepository.findEmptyLeafTopics();
    }
    
    // Validation
    private void validateTopic(Topic topic) {
        if (topic.getName() == null || topic.getName().trim().isEmpty()) {
            throw new RuntimeException("Topic name is required");
        }
        
        if (topicRepository.existsByName(topic.getName())) {
            throw new RuntimeException("Topic with this name already exists");
        }
        
        if (topic.getDifficultyLevel() != null && 
            (topic.getDifficultyLevel() < 1 || topic.getDifficultyLevel() > 5)) {
            throw new RuntimeException("Difficulty level must be between 1 and 5");
        }
    }
}