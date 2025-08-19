package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.Question;
import com.fachinformatiker.lernapp.model.Question.Difficulty;
import com.fachinformatiker.lernapp.repository.QuestionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service für Fragen-Verwaltung
 * Bietet Business-Logic für Questions
 */
@Service
@Transactional
public class QuestionService {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    /**
     * Hole zufällige Fragen für eine Übung
     */
    public List<Question> getRandomQuestions(String category, String difficulty, int count) {
        // Wenn "Alle Themen" gewählt wurde
        if (category == null || category.equals("all") || category.isEmpty()) {
            return getRandomQuestionsAllCategories(difficulty, count);
        }
        
        // Wenn spezifische Kategorie
        if (difficulty == null || difficulty.equals("all") || difficulty.isEmpty()) {
            return questionRepository.findRandomQuestionsByCategory(category, count);
        } else {
            return questionRepository.findRandomQuestionsByCategoryAndDifficulty(
                category, 
                difficulty.toUpperCase(), 
                count
            );
        }
    }
    
    /**
     * Hole zufällige Fragen aus allen Kategorien
     */
    private List<Question> getRandomQuestionsAllCategories(String difficulty, int count) {
        List<Question> allQuestions;
        
        if (difficulty == null || difficulty.equals("all") || difficulty.isEmpty()) {
            allQuestions = questionRepository.findAll();
        } else {
            Difficulty diff = Difficulty.valueOf(difficulty.toUpperCase());
            allQuestions = questionRepository.findByDifficulty(diff);
        }
        
        // Mische und nimm die gewünschte Anzahl
        Collections.shuffle(allQuestions);
        return allQuestions.stream()
            .limit(count)
            .collect(Collectors.toList());
    }
    
    /**
     * Prüfe eine Antwort
     */
    public boolean checkAnswer(Long questionId, String userAnswer) {
        Optional<Question> question = questionRepository.findById(questionId);
        if (question.isPresent()) {
            return question.get().getCorrectAnswer().equals(userAnswer);
        }
        return false;
    }
    
    /**
     * Hole eine Frage nach ID
     */
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }
    
    /**
     * Hole alle Kategorien
     */
    public List<String> getAllCategories() {
        return questionRepository.findAllCategories();
    }
    
    /**
     * Erstelle eine neue Frage
     */
    public Question createQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    /**
     * Lösche eine Frage
     */
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
    
    /**
     * Update eine Frage
     */
    public Question updateQuestion(Long id, Question updatedQuestion) {
        Optional<Question> existing = questionRepository.findById(id);
        if (existing.isPresent()) {
            Question question = existing.get();
            question.setCategory(updatedQuestion.getCategory());
            question.setSubcategory(updatedQuestion.getSubcategory());
            question.setQuestionText(updatedQuestion.getQuestionText());
            question.setOptions(updatedQuestion.getOptions());
            question.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
            question.setExplanation(updatedQuestion.getExplanation());
            question.setDifficulty(updatedQuestion.getDifficulty());
            question.setPoints(updatedQuestion.getPoints());
            return questionRepository.save(question);
        }
        throw new RuntimeException("Question not found with id: " + id);
    }
    
    /**
     * Hole Statistiken
     */
    public Map<String, Long> getQuestionStatistics() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> categoryCount = questionRepository.countQuestionsByCategory();
        
        for (Object[] row : categoryCount) {
            stats.put((String) row[0], (Long) row[1]);
        }
        
        stats.put("total", questionRepository.count());
        return stats;
    }
}
