package de.lernapp.service;

import de.lernapp.model.Question;
import de.lernapp.model.User;
import de.lernapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;
import java.util.stream.Collectors;

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
     * Hole eine einzelne zufällige Frage
     */
    public Question getRandomQuestion() {
        List<Question> questions = questionRepository.findRandomQuestions(1);
        return questions.isEmpty() ? null : questions.get(0);
    }
    
    /**
     * Finde Frage nach ID
     */
    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }
    
    /**
     * Hole alle verfügbaren Kategorien
     */
    public List<String> getAllCategories() {
        return questionRepository.findAllCategories();
    }
    
    /**
     * Hole alle Kategorien mit der Anzahl der Fragen
     */
    public Map<String, Long> getCategoriesWithCount() {
        List<Question> allQuestions = questionRepository.findAll();
        return allQuestions.stream()
            .collect(Collectors.groupingBy(
                Question::getCategory,
                Collectors.counting()
            ));
    }
    
    /**
     * Prüfe eine Antwort
     */
    public boolean checkAnswer(Long questionId, String answer) {
        Optional<Question> question = questionRepository.findById(questionId);
        return question.map(q -> q.isCorrect(answer)).orElse(false);
    }
    
    /**
     * Get next question for learning mode
     */
    public Question getNextQuestion(User user, String category, Integer difficulty) {
        List<Question> questions;
        
        if (category != null && difficulty != null) {
            questions = questionRepository.findByCategoryAndDifficulty(category, difficulty);
        } else if (category != null) {
            questions = questionRepository.findByCategory(category);
        } else if (difficulty != null) {
            questions = questionRepository.findByDifficulty(difficulty);
        } else {
            questions = questionRepository.findAll();
        }
        
        // No filtering needed for now
        
        if (questions.isEmpty()) {
            return null;
        }
        
        // Return a random question from the filtered list
        Random random = new Random();
        return questions.get(random.nextInt(questions.size()));
    }
    
    /**
     * Get questions with filters
     */
    public List<Question> getQuestions(String category, Integer difficulty, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        List<Question> questions;
        
        if (category != null && difficulty != null) {
            questions = questionRepository.findByCategoryAndDifficulty(category, difficulty);
        } else if (category != null) {
            questions = questionRepository.findByCategory(category);
        } else if (difficulty != null) {
            questions = questionRepository.findByDifficulty(difficulty);
        } else {
            questions = questionRepository.findAll();
        }
        
        return questions.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    /**
     * Find by ID
     */
    public Optional<Question> findById(Long id) {
        return questionRepository.findById(id);
    }
    
    /**
     * Save question
     */
    public Question save(Question question) {
        return questionRepository.save(question);
    }
    
    /**
     * Get random question for development
     */
    public Question getRandomQuestion() {
        List<Question> allQuestions = questionRepository.findAll();
        
        if (allQuestions.isEmpty()) {
            // Create a demo question if no questions exist
            return createDemoQuestion();
        }
        
        Random random = new Random();
        return allQuestions.get(random.nextInt(allQuestions.size()));
    }
    
    /**
     * Create demo question for development
     */
    private Question createDemoQuestion() {
        Question demo = new Question();
        demo.setQuestionText("Was ist die Hauptfunktion eines Netzplans?");
        demo.setCategory("Projektmanagement");
        demo.setDifficulty(2);
        demo.setOptionA("Kosten berechnen");
        demo.setOptionB("Zeitplanung visualisieren");
        demo.setOptionC("Personal verwalten");
        demo.setOptionD("Qualität sichern");
        demo.setCorrectAnswer("B");
        demo.setExplanation("Ein Netzplan dient hauptsächlich der Zeitplanung und zeigt Abhängigkeiten zwischen Vorgängen auf.");
        demo.setPoints(5);
        demo.setActive(true);
        return demo;
    }
    
    /**
     * Get category statistics for user
     */
    public Map<String, Integer> getCategoryStats(User user) {
        Map<String, Integer> stats = new HashMap<>();
        List<String> categories = getAllCategories();
        
        for (String category : categories) {
            // This is simplified - in a real app, you'd track which questions the user has answered
            stats.put(category, questionRepository.findByCategory(category).size());
        }
        
        return stats;
    }
}
