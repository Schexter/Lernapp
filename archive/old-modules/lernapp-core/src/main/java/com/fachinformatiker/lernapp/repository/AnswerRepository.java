package com.fachinformatiker.lernapp.repository;

import com.fachinformatiker.lernapp.model.Answer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AnswerRepository extends JpaRepository<Answer, Long> {
    
    List<Answer> findByQuestionId(Long questionId);
    
    List<Answer> findByQuestionIdOrderBySortOrderAsc(Long questionId);
    
    List<Answer> findByQuestionIdAndIsCorrect(Long questionId, Boolean isCorrect);
    
    @Query("SELECT a FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true")
    List<Answer> findCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
    
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId")
    Long countByQuestionId(@Param("questionId") Long questionId);
    
    @Query("SELECT COUNT(a) FROM Answer a WHERE a.question.id = :questionId AND a.isCorrect = true")
    Long countCorrectAnswersByQuestionId(@Param("questionId") Long questionId);
    
    void deleteByQuestionId(Long questionId);
}