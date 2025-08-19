package de.lernapp.service;

import de.lernapp.model.Question;
import de.lernapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service-Klasse für die Geschäftslogik rund um Questions
 * 
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Service
@RequiredArgsConstructor
@Transactional
public class QuestionService {
    
    private final QuestionRepository questionRepository;
    
    /**
     * Hole alle Fragen
     */
    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }
    
    /**
     * Hole eine Frage nach ID
     */
    public Optional<Question> getQuestionById(Long id) {
        return questionRepository.findById(id);
    }
    
    /**
     * Speichere eine neue Frage
     */
    public Question saveQuestion(Question question) {
        return questionRepository.save(question);
    }
    
    /**
     * Aktualisiere eine existierende Frage
     */
    public Question updateQuestion(Long id, Question questionDetails) {
        Question question = questionRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Frage nicht gefunden mit ID: " + id));
        
        question.setQuestionText(questionDetails.getQuestionText());
        question.setCategory(questionDetails.getCategory());
        question.setDifficulty(questionDetails.getDifficulty());
        question.setOptionA(questionDetails.getOptionA());
        question.setOptionB(questionDetails.getOptionB());
        question.setOptionC(questionDetails.getOptionC());
        question.setOptionD(questionDetails.getOptionD());
        question.setCorrectAnswer(questionDetails.getCorrectAnswer());
        question.setExplanation(questionDetails.getExplanation());
        question.setPoints(questionDetails.getPoints());
        
        return questionRepository.save(question);
    }
    
    /**
     * Lösche eine Frage
     */
    public void deleteQuestion(Long id) {
        questionRepository.deleteById(id);
    }
    
    /**
     * Hole Fragen nach Kategorie
     */
    public List<Question> getQuestionsByCategory(String category) {
        return questionRepository.findByCategory(category);
    }
    
    /**
     * Hole Fragen nach Schwierigkeitsgrad
     */
    public List<Question> getQuestionsByDifficulty(Integer difficulty) {
        return questionRepository.findByDifficulty(difficulty);
    }
    
    /**
     * Hole zufällige Fragen für einen Test
     */
    public List<Question> getRandomQuestions(int count) {
        return questionRepository.findRandomQuestions(count);
    }
    
    /**
     * Hole alle verfügbaren Kategorien
     */
    public List<String> getAllCategories() {
        return questionRepository.findAllCategories();
    }
    
    /**
     * Prüfe eine Antwort
     */
    public boolean checkAnswer(Long questionId, String answer) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.map(q -> q.isCorrect(answer)).orElse(false);
    }
}
