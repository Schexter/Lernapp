package de.lernapp.service;

import de.lernapp.model.*;
import de.lernapp.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Service für die 60%-Erfolgsstrategie
 */
@Service
@RequiredArgsConstructor
@Transactional
public class StrategyService {
    
    private final QuestionRepository questionRepository;
    private final UserService userService;
    
    /**
     * Aktiviert die 60%-Strategie für einen Benutzer
     */
    public LearningStrategy activateStrategy(User user) {
        LearningStrategy strategy = new LearningStrategy();
        strategy.setUser(user);
        strategy.setName("60% Erfolgsstrategie - Post-RAID");
        strategy.setPriorities(LearningStrategy.getDefaultPriorities());
        strategy.activate();
        
        // Speichere in User-Objekt (falls Feld vorhanden)
        return strategy;
    }
    
    /**
     * Holt Fragen basierend auf der 60%-Strategie
     */
    public List<Question> getStrategyQuestions(User user, int count) {
        List<Question> allQuestions = questionRepository.findAll();
        LearningStrategy strategy = getActiveStrategy(user);
        
        if (strategy == null || !strategy.isActive()) {
            // Normale zufällige Auswahl
            Collections.shuffle(allQuestions);
            return allQuestions.stream().limit(count).collect(Collectors.toList());
        }
        
        // Gewichtete Auswahl basierend auf Strategie
        return selectWeightedQuestions(allQuestions, strategy, count);
    }
    
    /**
     * Gewichtete Fragenauswahl nach Priorität
     */
    private List<Question> selectWeightedQuestions(List<Question> questions, LearningStrategy strategy, int count) {
        Map<String, List<Question>> categoryMap = questions.stream()
            .collect(Collectors.groupingBy(Question::getCategory));
        
        List<Question> selectedQuestions = new ArrayList<>();
        
        // Phase 1: Höchste Priorität (Stufe 1)
        int phase1Count = (int)(count * 0.5); // 50% der Fragen aus Stufe 1
        selectedQuestions.addAll(selectFromPriority(categoryMap, strategy, 1, phase1Count));
        
        // Phase 2: Hohe Priorität (Stufe 2)
        int phase2Count = (int)(count * 0.35); // 35% der Fragen aus Stufe 2
        selectedQuestions.addAll(selectFromPriority(categoryMap, strategy, 2, phase2Count));
        
        // Phase 3: Sicherheitspuffer (Stufe 3)
        int phase3Count = count - selectedQuestions.size(); // Rest aus Stufe 3
        selectedQuestions.addAll(selectFromPriority(categoryMap, strategy, 3, phase3Count));
        
        Collections.shuffle(selectedQuestions);
        return selectedQuestions;
    }
    
    /**
     * Wählt Fragen aus einer bestimmten Prioritätsstufe
     */
    private List<Question> selectFromPriority(Map<String, List<Question>> categoryMap, 
                                              LearningStrategy strategy, 
                                              int priority, 
                                              int count) {
        List<Question> priorityQuestions = new ArrayList<>();
        
        // Hole alle Kategorien dieser Priorität
        List<LearningStrategy.CategoryPriority> priorities = strategy.getPriorities().stream()
            .filter(p -> p.getPriority() == priority)
            .collect(Collectors.toList());
        
        // Verteile die Anzahl gleichmäßig auf die Kategorien
        int perCategory = Math.max(1, count / Math.max(1, priorities.size()));
        
        for (LearningStrategy.CategoryPriority catPriority : priorities) {
            List<Question> catQuestions = categoryMap.get(catPriority.getCategory());
            if (catQuestions != null && !catQuestions.isEmpty()) {
                Collections.shuffle(catQuestions);
                priorityQuestions.addAll(
                    catQuestions.stream()
                        .limit(perCategory)
                        .collect(Collectors.toList())
                );
            }
        }
        
        return priorityQuestions.stream().limit(count).collect(Collectors.toList());
    }
    
    /**
     * Berechnet Lernempfehlungen basierend auf der Strategie
     */
    public LearningRecommendation getRecommendation(User user) {
        LearningStrategy strategy = getActiveStrategy(user);
        if (strategy == null) {
            return getDefaultRecommendation();
        }
        
        LearningRecommendation recommendation = new LearningRecommendation();
        
        // Analysiere Benutzerfortschritt
        Map<String, Integer> userProgress = analyzeUserProgress(user);
        
        // Erstelle Empfehlungen basierend auf Schwächen
        for (LearningStrategy.CategoryPriority priority : strategy.getPriorities()) {
            Integer correctRate = userProgress.get(priority.getCategory());
            if (correctRate == null || correctRate < 60) {
                // Kategorie benötigt Fokus
                recommendation.addFocusArea(priority.getCategory(), 
                    priority.getRecommendedHours(), 
                    priority.getPriority());
            }
        }
        
        recommendation.setEstimatedTimeToGoal(calculateTimeToGoal(recommendation));
        recommendation.setCurrentSuccessRate(calculateOverallRate(userProgress));
        
        return recommendation;
    }
    
    /**
     * Quick-Win Fragen für schnelle Punkte
     */
    public List<Question> getQuickWinQuestions(User user, int count) {
        List<Question> quickWins = questionRepository.findAll().stream()
            .filter(q -> isQuickWin(q))
            .collect(Collectors.toList());
        
        Collections.shuffle(quickWins);
        return quickWins.stream().limit(count).collect(Collectors.toList());
    }
    
    /**
     * Prüft ob eine Frage ein Quick-Win ist (einfach + wichtig)
     */
    private boolean isQuickWin(Question question) {
        // Quick-Wins: Schwierigkeit 1-2 und wichtige Kategorien
        if (question.getDifficulty() > 2) return false;
        
        List<String> quickWinCategories = Arrays.asList(
            "Projektmanagement", // SMART, Projektmerkmale
            "IT-Sicherheit",     // CIA-Triad
            "PowerShell",        // Operatoren
            "OSI-Modell",        // Schichten
            "Datenschutz"        // DSGVO Basics
        );
        
        return quickWinCategories.contains(question.getCategory());
    }
    
    /**
     * Zeitmanagement-Vorschlag für Prüfung
     */
    public ExamTimeStrategy getExamTimeStrategy() {
        ExamTimeStrategy timeStrategy = new ExamTimeStrategy();
        
        timeStrategy.addPhase(1, "Definitionen", 20, 
            Arrays.asList("Projektmerkmale", "SMART", "CIA-Triad"), 25);
            
        timeStrategy.addPhase(2, "Netzplantechnik", 30,
            Arrays.asList("Vorwärts/Rückwärts", "Puffer", "Kritischer Pfad"), 10);
            
        timeStrategy.addPhase(3, "PowerShell + IPv6", 20,
            Arrays.asList("Debugging", "IPv6-Analyse"), 12);
            
        timeStrategy.addPhase(4, "Berechnungen", 15,
            Arrays.asList("Energie", "OSI", "PLY-Speicher"), 15);
            
        timeStrategy.addPhase(5, "Kontrolle", 5,
            Arrays.asList("Überprüfung", "Korrekturen"), 3);
            
        return timeStrategy;
    }
    
    // Hilfsmethoden
    private LearningStrategy getActiveStrategy(User user) {
        // TODO: Aus DB oder User-Objekt laden
        LearningStrategy strategy = new LearningStrategy();
        strategy.setUser(user);
        strategy.setPriorities(LearningStrategy.getDefaultPriorities());
        strategy.setActive(true);
        return strategy;
    }
    
    private LearningRecommendation getDefaultRecommendation() {
        LearningRecommendation rec = new LearningRecommendation();
        rec.addFocusArea("Projektmanagement", 15, 1);
        rec.addFocusArea("PowerShell", 10, 1);
        rec.addFocusArea("Netzwerktechnik", 12, 1);
        rec.setEstimatedTimeToGoal(80);
        return rec;
    }
    
    private Map<String, Integer> analyzeUserProgress(User user) {
        // TODO: Echte Analyse basierend auf beantworteten Fragen
        Map<String, Integer> progress = new HashMap<>();
        progress.put("Projektmanagement", 45);
        progress.put("PowerShell", 60);
        progress.put("Netzwerktechnik", 30);
        progress.put("IT-Sicherheit", 70);
        return progress;
    }
    
    private int calculateTimeToGoal(LearningRecommendation rec) {
        return rec.getFocusAreas().stream()
            .mapToInt(LearningRecommendation.FocusArea::getRecommendedHours)
            .sum();
    }
    
    private double calculateOverallRate(Map<String, Integer> progress) {
        if (progress.isEmpty()) return 0.0;
        return progress.values().stream()
            .mapToInt(Integer::intValue)
            .average()
            .orElse(0.0);
    }
    
    // Hilfsklassen
    public static class LearningRecommendation {
        private List<FocusArea> focusAreas = new ArrayList<>();
        private int estimatedTimeToGoal;
        private double currentSuccessRate;
        
        public void addFocusArea(String category, int hours, int priority) {
            focusAreas.add(new FocusArea(category, hours, priority));
        }
        
        // Getter/Setter
        public List<FocusArea> getFocusAreas() { return focusAreas; }
        public int getEstimatedTimeToGoal() { return estimatedTimeToGoal; }
        public void setEstimatedTimeToGoal(int hours) { this.estimatedTimeToGoal = hours; }
        public double getCurrentSuccessRate() { return currentSuccessRate; }
        public void setCurrentSuccessRate(double rate) { this.currentSuccessRate = rate; }
        
        public static class FocusArea {
            private String category;
            private int recommendedHours;
            private int priority;
            
            public FocusArea(String category, int hours, int priority) {
                this.category = category;
                this.recommendedHours = hours;
                this.priority = priority;
            }
            
            // Getter
            public String getCategory() { return category; }
            public int getRecommendedHours() { return recommendedHours; }
            public int getPriority() { return priority; }
        }
    }
    
    public static class ExamTimeStrategy {
        private List<Phase> phases = new ArrayList<>();
        
        public void addPhase(int number, String name, int minutes, List<String> tasks, int targetPoints) {
            phases.add(new Phase(number, name, minutes, tasks, targetPoints));
        }
        
        public List<Phase> getPhases() { return phases; }
        
        public static class Phase {
            private int number;
            private String name;
            private int minutes;
            private List<String> tasks;
            private int targetPoints;
            
            public Phase(int number, String name, int minutes, List<String> tasks, int targetPoints) {
                this.number = number;
                this.name = name;
                this.minutes = minutes;
                this.tasks = tasks;
                this.targetPoints = targetPoints;
            }
            
            // Getter
            public int getNumber() { return number; }
            public String getName() { return name; }
            public int getMinutes() { return minutes; }
            public List<String> getTasks() { return tasks; }
            public int getTargetPoints() { return targetPoints; }
        }
    }
}