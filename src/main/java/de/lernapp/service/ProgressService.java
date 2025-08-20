package de.lernapp.service;

import de.lernapp.model.Question;
import de.lernapp.model.User;
import de.lernapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgressService {

    private final UserRepository userRepository;
    private final QuestionService questionService;

    @Transactional
    public void recordAnswer(User user, Question question, boolean isCorrect) {
        // Update question statistics
        question.setTimesAnswered(question.getTimesAnswered() + 1);
        if (isCorrect) {
            question.setTimesCorrect(question.getTimesCorrect() + 1);
        }
        questionService.save(question);
        
        // Update user statistics
        user.setTotalQuestionsAnswered(user.getTotalQuestionsAnswered() + 1);
        
        if (isCorrect) {
            user.setCorrectAnswers(user.getCorrectAnswers() + 1);
            user.setCurrentStreak(user.getCurrentStreak() + 1);
            user.setExperiencePoints(user.getExperiencePoints() + question.getPoints());
            
            // Update best streak if needed
            if (user.getCurrentStreak() > user.getBestStreak()) {
                user.setBestStreak(user.getCurrentStreak());
            }
            
            // Level up logic (every 100 XP)
            int newLevel = (user.getExperiencePoints() / 100) + 1;
            if (newLevel > user.getLevel()) {
                user.setLevel(newLevel);
                log.info("User {} leveled up to level {}", user.getUsername(), newLevel);
            }
        } else {
            // Reset streak on wrong answer
            user.setCurrentStreak(0);
        }
        
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Recorded answer for user {}: Question {} - Correct: {}", 
                 user.getUsername(), question.getId(), isCorrect);
    }

    public Map<String, Object> getUserStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        
        // Basic stats
        stats.put("totalQuestionsAnswered", user.getTotalQuestionsAnswered());
        stats.put("correctAnswers", user.getCorrectAnswers());
        stats.put("currentStreak", user.getCurrentStreak());
        stats.put("bestStreak", user.getBestStreak());
        stats.put("experiencePoints", user.getExperiencePoints());
        stats.put("level", user.getLevel());
        
        // Calculate accuracy
        if (user.getTotalQuestionsAnswered() > 0) {
            double accuracy = (double) user.getCorrectAnswers() / user.getTotalQuestionsAnswered() * 100;
            stats.put("accuracy", Math.round(accuracy * 100.0) / 100.0);
        } else {
            stats.put("accuracy", 0.0);
        }
        
        // Progress to next level
        int currentLevelXP = (user.getLevel() - 1) * 100;
        int nextLevelXP = user.getLevel() * 100;
        int progressXP = user.getExperiencePoints() - currentLevelXP;
        stats.put("progressToNextLevel", progressXP);
        stats.put("xpNeededForNextLevel", 100);
        
        // Category breakdown (simplified for now)
        Map<String, Integer> categoryStats = questionService.getCategoryStats(user);
        stats.put("categoryBreakdown", categoryStats);
        
        return stats;
    }
}