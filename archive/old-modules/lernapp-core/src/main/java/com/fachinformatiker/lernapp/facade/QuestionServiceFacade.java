package com.fachinformatiker.lernapp.facade;

import com.fachinformatiker.lernapp.dto.*;
import com.fachinformatiker.lernapp.model.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * Service Facade for Question Management
 * Bridges DTOs with Service Layer
 * 
 * @author Hans Hahn
 */
public interface QuestionServiceFacade {
    
    QuestionDTO createQuestion(QuestionDTO questionDTO);
    
    Optional<QuestionDTO> getQuestionById(Long id);
    
    QuestionDTO updateQuestion(Long id, QuestionDTO questionDTO);
    
    boolean deleteQuestion(Long id);
    
    Page<QuestionDTO> getQuestions(Pageable pageable, Long topicId, Integer difficulty, Question.QuestionType type);
    
    List<QuestionDTO> getQuestionsByTopic(Long topicId);
    
    List<QuestionDTO> getQuestionsByDifficulty(Integer level);
    
    List<QuestionDTO> searchQuestions(String query, Long topicId);
    
    List<QuestionDTO> getRandomQuestions(int count, Long topicId, Integer difficulty);
    
    AnswerValidationDTO validateAnswer(Long questionId, AnswerSubmissionDTO submission);
    
    ImportResultDTO importQuestions(MultipartFile file, Long topicId) throws Exception;
    
    byte[] exportQuestions(Long topicId, String format) throws Exception;
    
    Optional<QuestionStatisticsDTO> getQuestionStatistics(Long id);
    
    AnswerDTO addAnswer(Long questionId, AnswerDTO answerDTO);
    
    AnswerDTO updateAnswer(Long questionId, Long answerId, AnswerDTO answerDTO);
    
    boolean deleteAnswer(Long questionId, Long answerId);
}
