package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final TopicRepository topicRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    
    @Cacheable(value = "questions", key = "#id")
    public Optional<Question> findById(Long id) {
        log.debug("Finding question by id: {}", id);
        return questionRepository.findByIdWithAnswers(id);
    }
    
    public List<Question> findByIds(List<Long> ids) {
        log.debug("Finding questions by ids: {}", ids);
        return questionRepository.findByIdsWithAnswers(ids);
    }
    
    public Page<Question> findAll(Pageable pageable) {
        log.debug("Finding all questions with pagination");
        return questionRepository.findAll(pageable);
    }
    
    public List<Question> findByTopic(Long topicId) {
        log.debug("Finding questions by topic: {}", topicId);
        return questionRepository.findByTopicId(topicId);
    }
    
    public Page<Question> findByTopic(Long topicId, Pageable pageable) {
        log.debug("Finding questions by topic with pagination: {}", topicId);
        return questionRepository.findByTopicId(topicId, pageable);
    }
    
    public List<Question> findByTopicAndDifficulty(Long topicId, Integer difficulty) {
        log.debug("Finding questions by topic {} and difficulty {}", topicId, difficulty);
        return questionRepository.findByTopicAndDifficulty(topicId, difficulty);
    }
    
    public List<Question> findByTopicAndDifficultyRange(Long topicId, Integer minLevel, Integer maxLevel) {
        log.debug("Finding questions by topic {} and difficulty range {}-{}", topicId, minLevel, maxLevel);
        return questionRepository.findByTopicAndDifficultyRange(topicId, minLevel, maxLevel);
    }
    
    public Page<Question> searchQuestions(String searchTerm, Pageable pageable) {
        log.debug("Searching questions with term: {}", searchTerm);
        return questionRepository.searchByQuestionText(searchTerm, pageable);
    }
    
    @CacheEvict(value = "questions", allEntries = true)
    public Question createQuestion(Question question, Long creatorId) {
        log.info("Creating new question");
        
        validateQuestion(question);
        
        // Set creator
        User creator = userRepository.findById(creatorId)
            .orElseThrow(() -> new RuntimeException("Creator not found"));
        question.setCreatedBy(creator);
        
        // Set topic
        Topic topic = topicRepository.findById(question.getTopic().getId())
            .orElseThrow(() -> new RuntimeException("Topic not found"));
        question.setTopic(topic);
        
        // Set defaults
        question.setActive(true);
        question.setCreatedAt(LocalDateTime.now());
        question.setUpdatedAt(LocalDateTime.now());
        
        // Process tags
        if (question.getTags() != null && !question.getTags().isEmpty()) {
            question.setTags(processTags(question.getTags()));
        }
        
        // Save question
        Question savedQuestion = questionRepository.save(question);
        
        // Save answers
        if (question.getAnswers() != null && !question.getAnswers().isEmpty()) {
            for (Answer answer : question.getAnswers()) {
                answer.setQuestion(savedQuestion);
                answer.setActive(true);
                answerRepository.save(answer);
            }
        }
        
        log.info("Question created successfully with id: {}", savedQuestion.getId());
        return savedQuestion;
    }
    
    @CacheEvict(value = "questions", key = "#question.id")
    public Question updateQuestion(Question question) {
        log.info("Updating question: {}", question.getId());
        
        Question existingQuestion = questionRepository.findById(question.getId())
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        // Update fields
        existingQuestion.setQuestionText(question.getQuestionText());
        existingQuestion.setQuestionCode(question.getQuestionCode());
        existingQuestion.setImageUrl(question.getImageUrl());
        existingQuestion.setQuestionType(question.getQuestionType());
        existingQuestion.setDifficultyLevel(question.getDifficultyLevel());
        existingQuestion.setPoints(question.getPoints());
        existingQuestion.setTimeLimitSeconds(question.getTimeLimitSeconds());
        existingQuestion.setExplanation(question.getExplanation());
        existingQuestion.setHint(question.getHint());
        existingQuestion.setReferenceUrl(question.getReferenceUrl());
        // TODO: Implement metadata handling for String field
        // existingQuestion.setMetadata(question.getMetadata());
        existingQuestion.setUpdatedAt(LocalDateTime.now());
        
        // Update tags
        if (question.getTags() != null) {
            existingQuestion.setTags(processTags(question.getTags()));
        }
        
        return questionRepository.save(existingQuestion);
    }
    
    @CacheEvict(value = "questions", key = "#questionId")
    public void deleteQuestion(Long questionId) {
        log.warn("Deleting question: {}", questionId);
        
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        question.setActive(false);
        question.setUpdatedAt(LocalDateTime.now());
        questionRepository.save(question);
        
        log.info("Question soft deleted: {}", questionId);
    }
    
    // Answer management
    public Answer addAnswer(Long questionId, Answer answer) {
        log.info("Adding answer to question: {}", questionId);
        
        Question question = questionRepository.findById(questionId)
            .orElseThrow(() -> new RuntimeException("Question not found"));
        
        answer.setQuestion(question);
        answer.setActive(true);
        answer.setCreatedAt(LocalDateTime.now());
        
        Answer savedAnswer = answerRepository.save(answer);
        log.info("Answer added successfully: {}", savedAnswer.getId());
        
        return savedAnswer;
    }
    
    public Answer updateAnswer(Answer answer) {
        log.info("Updating answer: {}", answer.getId());
        
        Answer existingAnswer = answerRepository.findById(answer.getId())
            .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        existingAnswer.setAnswerText(answer.getAnswerText());
        existingAnswer.setAnswerCode(answer.getAnswerCode());
        existingAnswer.setImageUrl(answer.getImageUrl());
        existingAnswer.setIsCorrect(answer.getIsCorrect());
        existingAnswer.setExplanation(answer.getExplanation());
        existingAnswer.setSortOrder(answer.getSortOrder());
        existingAnswer.setUpdatedAt(LocalDateTime.now());
        
        return answerRepository.save(existingAnswer);
    }
    
    public void deleteAnswer(Long answerId) {
        log.warn("Deleting answer: {}", answerId);
        
        Answer answer = answerRepository.findById(answerId)
            .orElseThrow(() -> new RuntimeException("Answer not found"));
        
        answer.setActive(false);
        answer.setUpdatedAt(LocalDateTime.now());
        answerRepository.save(answer);
        
        log.info("Answer soft deleted: {}", answerId);
    }
    
    // Random questions
    public List<Question> getRandomQuestions(int count) {
        log.debug("Getting {} random questions", count);
        PageRequest pageRequest = PageRequest.of(0, count);
        return questionRepository.findRandomQuestions(pageRequest).getContent();
    }
    
    public List<Question> getRandomQuestionsByTopic(Long topicId, int count) {
        log.debug("Getting {} random questions for topic {}", count, topicId);
        return questionRepository.findRandomQuestionsByTopic(topicId, count);
    }
    
    // Learning support
    public List<Question> getUnansweredQuestions(Long userId, Long topicId) {
        log.debug("Getting unanswered questions for user {} in topic {}", userId, topicId);
        return questionRepository.findUnansweredQuestionsByUserAndTopic(userId, topicId);
    }
    
    public List<Question> getQuestionsForReview(Long userId) {
        log.debug("Getting questions for review for user {}", userId);
        return questionRepository.findQuestionsForReview(userId);
    }
    
    // Statistics
    public Long countQuestionsByTopic(Long topicId) {
        return questionRepository.countByTopicId(topicId);
    }
    
    public Long countActiveQuestionsByTopic(Long topicId) {
        return questionRepository.countActiveByTopicId(topicId);
    }
    
    public Double getAverageDifficultyByTopic(Long topicId) {
        return questionRepository.getAverageDifficultyByTopic(topicId);
    }
    
    public Map<Question.QuestionType, Long> getQuestionTypeDistribution(Long topicId) {
        List<Object[]> results = questionRepository.getQuestionTypeDistributionByTopic(topicId);
        
        return results.stream()
            .collect(Collectors.toMap(
                r -> (Question.QuestionType) r[0],
                r -> (Long) r[1]
            ));
    }
    
    // Validation and quality checks
    public List<Question> findQuestionsWithoutExplanation() {
        log.debug("Finding questions without explanation");
        return questionRepository.findQuestionsWithoutExplanation();
    }
    
    public List<Question> findQuestionsWithInsufficientAnswers() {
        log.debug("Finding questions with insufficient answers");
        return questionRepository.findQuestionsWithInsufficientAnswers();
    }
    
    // Helper methods
    private void validateQuestion(Question question) {
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            throw new RuntimeException("Question text is required");
        }
        
        if (question.getTopic() == null || question.getTopic().getId() == null) {
            throw new RuntimeException("Topic is required");
        }
        
        if (question.getQuestionType() == null) {
            throw new RuntimeException("Question type is required");
        }
        
        if (question.getDifficultyLevel() == null || question.getDifficultyLevel() < 1 || question.getDifficultyLevel() > 5) {
            throw new RuntimeException("Difficulty level must be between 1 and 5");
        }
        
        // Validate answers
        if (question.getAnswers() == null || question.getAnswers().isEmpty()) {
            throw new RuntimeException("At least one answer is required");
        }
        
        boolean hasCorrectAnswer = question.getAnswers().stream()
            .anyMatch(Answer::getIsCorrect);
        
        if (!hasCorrectAnswer) {
            throw new RuntimeException("At least one correct answer is required");
        }
    }
    
    private List<Tag> processTags(List<Tag> tags) {
        List<Tag> processedTags = new ArrayList<>();
        
        for (Tag tag : tags) {
            Tag existingTag = tagRepository.findByName(tag.getName())
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tag.getName());
                    newTag.setDescription(tag.getDescription());
                    newTag.setColor(tag.getColor());
                    newTag.setActive(true);
                    return tagRepository.save(newTag);
                });
            
            processedTags.add(existingTag);
        }
        
        return processedTags;
    }
}