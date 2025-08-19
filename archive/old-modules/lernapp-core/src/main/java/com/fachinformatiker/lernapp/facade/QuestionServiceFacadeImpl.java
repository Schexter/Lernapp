package com.fachinformatiker.lernapp.facade;

import com.fachinformatiker.lernapp.dto.*;
import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.service.QuestionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Implementation of QuestionServiceFacade
 * 
 * @author Hans Hahn
 */
@Service("questionServiceFacade")
@RequiredArgsConstructor
@Slf4j
public class QuestionServiceFacadeImpl implements QuestionServiceFacade {
    
    private final QuestionService questionService;
    
    @Override
    public QuestionDTO createQuestion(QuestionDTO dto) {
        Question question = convertToEntity(dto);
        Question saved = questionService.createQuestion(question, 1L); // TODO: Get current user ID
        return convertToDTO(saved);
    }
    
    @Override
    public Optional<QuestionDTO> getQuestionById(Long id) {
        return questionService.findById(id).map(this::convertToDTO);
    }
    
    @Override
    public QuestionDTO updateQuestion(Long id, QuestionDTO dto) {
        Question question = convertToEntity(dto);
        question.setId(id);
        Question updated = questionService.updateQuestion(question);
        return convertToDTO(updated);
    }
    
    @Override
    public boolean deleteQuestion(Long id) {
        try {
            questionService.deleteQuestion(id);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete question: {}", e.getMessage());
            return false;
        }
    }
    
    @Override
    public Page<QuestionDTO> getQuestions(Pageable pageable, Long topicId, Integer difficulty, Question.QuestionType type) {
        Page<Question> questions;
        
        if (topicId != null) {
            questions = questionService.findByTopic(topicId, pageable);
        } else {
            questions = questionService.findAll(pageable);
        }
        
        // Filter by difficulty and type if provided
        List<QuestionDTO> filtered = questions.getContent().stream()
            .filter(q -> difficulty == null || q.getDifficultyLevel().equals(difficulty))
            .filter(q -> type == null || q.getQuestionType() == type)
            .map(this::convertToDTO)
            .collect(Collectors.toList());
            
        return new PageImpl<>(filtered, pageable, questions.getTotalElements());
    }
    
    @Override
    public List<QuestionDTO> getQuestionsByTopic(Long topicId) {
        return questionService.findByTopic(topicId).stream()
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<QuestionDTO> getQuestionsByDifficulty(Integer level) {
        return questionService.findAll(Pageable.unpaged()).getContent().stream()
            .filter(q -> q.getDifficultyLevel().equals(level))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<QuestionDTO> searchQuestions(String query, Long topicId) {
        return questionService.searchQuestions(query, Pageable.unpaged()).getContent().stream()
            .filter(q -> topicId == null || q.getTopic().getId().equals(topicId))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<QuestionDTO> getRandomQuestions(int count, Long topicId, Integer difficulty) {
        List<Question> questions;
        
        if (topicId != null) {
            questions = questionService.getRandomQuestionsByTopic(topicId, count);
        } else {
            questions = questionService.getRandomQuestions(count);
        }
        
        return questions.stream()
            .filter(q -> difficulty == null || q.getDifficultyLevel().equals(difficulty))
            .map(this::convertToDTO)
            .collect(Collectors.toList());
    }
    
    @Override
    public AnswerValidationDTO validateAnswer(Long questionId, AnswerSubmissionDTO submission) {
        Optional<Question> questionOpt = questionService.findById(questionId);
        
        if (questionOpt.isEmpty()) {
            throw new RuntimeException("Question not found");
        }
        
        Question question = questionOpt.get();
        boolean correct = false;
        String explanation = question.getExplanation();
        
        if (submission.getAnswerId() != null) {
            Answer answer = question.getAnswers().stream()
                .filter(a -> a.getId().equals(submission.getAnswerId()))
                .findFirst()
                .orElse(null);
                
            if (answer != null) {
                correct = answer.getIsCorrect();
                if (answer.getExplanation() != null) {
                    explanation = answer.getExplanation();
                }
            }
        }
        
        return AnswerValidationDTO.builder()
            .correct(correct)
            .explanation(explanation)
            .build();
    }
    
    @Override
    public ImportResultDTO importQuestions(MultipartFile file, Long topicId) throws Exception {
        // TODO: Implement import logic
        return ImportResultDTO.builder()
            .totalQuestions(0)
            .successfulImports(0)
            .failedImports(0)
            .errors(new ArrayList<>())
            .build();
    }
    
    @Override
    public byte[] exportQuestions(Long topicId, String format) throws Exception {
        // TODO: Implement export logic
        return new byte[0];
    }
    
    @Override
    public Optional<QuestionStatisticsDTO> getQuestionStatistics(Long id) {
        // TODO: Implement statistics
        return Optional.empty();
    }
    
    @Override
    public AnswerDTO addAnswer(Long questionId, AnswerDTO dto) {
        Answer answer = new Answer();
        answer.setAnswerText(dto.getAnswerText());
        answer.setIsCorrect(dto.isCorrect());
        answer.setExplanation(dto.getExplanation());
        answer.setSortOrder(dto.getSortOrder());
        
        Answer saved = questionService.addAnswer(questionId, answer);
        return convertAnswerToDTO(saved);
    }
    
    @Override
    public AnswerDTO updateAnswer(Long questionId, Long answerId, AnswerDTO dto) {
        Answer answer = new Answer();
        answer.setId(answerId);
        answer.setAnswerText(dto.getAnswerText());
        answer.setIsCorrect(dto.isCorrect());
        answer.setExplanation(dto.getExplanation());
        answer.setSortOrder(dto.getSortOrder());
        
        Answer updated = questionService.updateAnswer(answer);
        return convertAnswerToDTO(updated);
    }
    
    @Override
    public boolean deleteAnswer(Long questionId, Long answerId) {
        try {
            questionService.deleteAnswer(answerId);
            return true;
        } catch (Exception e) {
            log.error("Failed to delete answer: {}", e.getMessage());
            return false;
        }
    }
    
    // Conversion methods
    private QuestionDTO convertToDTO(Question question) {
        return QuestionDTO.builder()
            .id(question.getId())
            .topicId(question.getTopic() != null ? question.getTopic().getId() : null)
            .questionText(question.getQuestionText())
            .questionType(question.getQuestionType())
            .difficultyLevel(question.getDifficultyLevel())
            .explanation(question.getExplanation())
            .imageUrl(question.getImageUrl())
            .codeSnippet(question.getQuestionCode())
            .answers(question.getAnswers() != null ? 
                question.getAnswers().stream()
                    .map(this::convertAnswerToDTO)
                    .collect(Collectors.toSet()) : null)
            .createdAt(question.getCreatedAt())
            .createdBy(question.getCreatedBy() != null ? question.getCreatedBy().getId() : null)
            .build();
    }
    
    private Question convertToEntity(QuestionDTO dto) {
        Question question = new Question();
        question.setQuestionText(dto.getQuestionText());
        question.setQuestionType(dto.getQuestionType());
        question.setDifficultyLevel(dto.getDifficultyLevel());
        question.setExplanation(dto.getExplanation());
        question.setImageUrl(dto.getImageUrl());
        question.setQuestionCode(dto.getCodeSnippet());
        
        if (dto.getTopicId() != null) {
            Topic topic = new Topic();
            topic.setId(dto.getTopicId());
            question.setTopic(topic);
        }
        
        if (dto.getAnswers() != null) {
            List<Answer> answers = dto.getAnswers().stream()
                .map(this::convertAnswerToEntity)
                .collect(Collectors.toList());
            question.setAnswers(answers);
        }
        
        return question;
    }
    
    private AnswerDTO convertAnswerToDTO(Answer answer) {
        return AnswerDTO.builder()
            .id(answer.getId())
            .answerText(answer.getAnswerText())
            .correct(answer.getIsCorrect())
            .explanation(answer.getExplanation())
            .sortOrder(answer.getSortOrder())
            .build();
    }
    
    private Answer convertAnswerToEntity(AnswerDTO dto) {
        Answer answer = new Answer();
        answer.setAnswerText(dto.getAnswerText());
        answer.setIsCorrect(dto.isCorrect());
        answer.setExplanation(dto.getExplanation());
        answer.setSortOrder(dto.getSortOrder());
        return answer;
    }
}
