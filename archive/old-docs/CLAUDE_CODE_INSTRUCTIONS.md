# CLAUDE CODE - Sprint Instructions
## Fachinformatiker Lernapp Java - Next Development Phase

**Project Path:** `C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java`
**Current Status:** Backend functional, Frontend 30% complete, Login working
**Your Mission:** Complete MVP Features for Learning System

---

## 🎯 SPRINT 1: Dashboard & Content Import (Priority: CRITICAL)

### Task 1.1: Fix Dashboard Functionality
**Location:** `lernapp-web/src/main/resources/templates/dashboard/index.html`

1. Create a functional dashboard that shows:
   - User welcome message with stats
   - Quick access buttons (Start Learning, Practice, Take Exam)
   - Recent activity
   - Progress overview widget
   - Available topics grid

2. Implement dashboard controller endpoints:
```java
// In DashboardController.java
@GetMapping("/api/dashboard/stats")
public ResponseEntity<DashboardStatsDTO> getUserStats()

@GetMapping("/api/dashboard/recent-activity")
public ResponseEntity<List<ActivityDTO>> getRecentActivity()
```

### Task 1.2: CSV Import System for Questions
**Location:** Create new package `com.fachinformatiker.lernapp.service.importer`

Create robust CSV import system:
```java
@Service
public class QuestionImportService {
    public ImportResult importFromCsv(MultipartFile file);
    public ValidationResult validateCsvFormat(MultipartFile file);
    public List<Question> parseQuestions(InputStream stream);
}
```

CSV Format to support:
```csv
topic,subtopic,question,answerA,answerB,answerC,answerD,correctAnswer,difficulty,explanation,examDate
```

### Task 1.3: Question Management API
**Location:** `lernapp-web/src/main/java/com/fachinformatiker/lernapp/controller/QuestionController.java`

Implement CRUD operations:
```java
POST   /api/questions/import    // CSV import
GET    /api/questions           // List with pagination
GET    /api/questions/{id}      // Get single question
PUT    /api/questions/{id}      // Update question
DELETE /api/questions/{id}      // Delete question
GET    /api/questions/random    // Get random question for practice
```

---

## 🎯 SPRINT 2: Learning Engine Core (Priority: CRITICAL)

### Task 2.1: Spaced Repetition Algorithm
**Location:** Create `com.fachinformatiker.lernapp.service.learning`

Implement the Leitner System:
```java
@Service
public class SpacedRepetitionService {
    
    public LocalDateTime calculateNextReview(
        Long questionId, 
        Long userId,
        boolean wasCorrect
    ) {
        // Box 1: Review after 1 day
        // Box 2: Review after 3 days
        // Box 3: Review after 7 days
        // Box 4: Review after 14 days
        // Box 5: Review after 30 days
    }
    
    public Question getNextQuestion(Long userId, Long topicId) {
        // Priority:
        // 1. Overdue reviews
        // 2. New questions
        // 3. Questions in lower boxes
    }
}
```

### Task 2.2: Learning Session Management
**Location:** `com.fachinformatiker.lernapp.service.session`

```java
@Service
public class LearningSessionService {
    public LearningSession startSession(Long userId, SessionType type, Long topicId);
    public void submitAnswer(Long sessionId, Long questionId, String answer);
    public SessionResult endSession(Long sessionId);
    public SessionStats getSessionStats(Long sessionId);
}
```

### Task 2.3: Progress Tracking
**Location:** `com.fachinformatiker.lernapp.service.progress`

```java
@Service
public class ProgressTrackingService {
    public UserProgress calculateProgress(Long userId, Long topicId);
    public List<WeakArea> identifyWeakAreas(Long userId);
    public LearningStreak getStreak(Long userId);
    public Map<Topic, Double> getTopicMastery(Long userId);
}
```

---

## 🎯 SPRINT 3: Practice Mode UI (Priority: HIGH)

### Task 3.1: Practice Mode Frontend
**Location:** `lernapp-web/src/main/resources/templates/practice/`

Create interactive practice interface:
- Question display with syntax highlighting for code
- Multiple choice selection
- Timer (optional)
- Instant feedback with explanations
- Next question button
- Progress bar
- Skip option

### Task 3.2: Practice API Endpoints
```java
@RestController
@RequestMapping("/api/practice")
public class PracticeController {
    
    @PostMapping("/start")
    public PracticeSession startPractice(
        @RequestParam Long topicId,
        @RequestParam Integer questionCount
    );
    
    @PostMapping("/answer")
    public AnswerResult submitAnswer(
        @RequestParam Long sessionId,
        @RequestParam Long questionId,
        @RequestParam String answer
    );
    
    @GetMapping("/next")
    public Question getNextQuestion(@RequestParam Long sessionId);
    
    @PostMapping("/end")
    public PracticeResult endPractice(@RequestParam Long sessionId);
}
```

### Task 3.3: Real-time Feedback System
Implement immediate feedback after each answer:
- Show correct answer
- Explain why answer is correct/incorrect
- Show difficulty rating
- Update progress in real-time
- Suggest related topics

---

## 🎯 SPRINT 4: Exam Simulation (Priority: HIGH)

### Task 4.1: Exam Mode Implementation
**Location:** `com.fachinformatiker.lernapp.service.exam`

```java
@Service
public class ExamService {
    public Exam createExam(ExamConfiguration config);
    public void startExam(Long examId, Long userId);
    public void submitExam(Long examId, Map<Long, String> answers);
    public ExamResult gradeExam(Long examId);
    public DetailedReport generateReport(Long examId);
}
```

### Task 4.2: Exam Timer & Auto-Submit
- Implement countdown timer (90 minutes for IHK exam)
- Auto-save every 30 seconds
- Warning at 10 minutes remaining
- Automatic submission when time expires

---

## 🔧 TECHNICAL REQUIREMENTS

### Database Seeds
Create DataInitializer to populate:
- 10 main topics with hierarchy
- 100 sample questions (varied difficulty)
- Test users with different progress levels

### Error Handling
Implement consistent error responses:
```java
@ControllerAdvice
public class GlobalExceptionHandler {
    // Handle all exceptions with proper HTTP status codes
    // Return user-friendly error messages
}
```

### Logging
Add comprehensive logging:
- User actions (learning sessions, answers)
- Performance metrics
- Error tracking
- Import operations

### Testing Requirements
- Unit tests for all services (80% coverage)
- Integration tests for controllers
- End-to-end test for complete learning flow

---

## ⚡ QUICK WINS (Do these first!)

1. **Fix the Dashboard** - Make it show real data
2. **Add 10 Sample Questions** - Hardcode if necessary for testing
3. **Enable Practice Mode** - Even with basic functionality
4. **Show Progress** - Simple percentage display

---

## 📝 DEFINITION OF DONE

Each feature is complete when:
- ✅ Code works and is tested
- ✅ Frontend displays correctly
- ✅ API returns expected data
- ✅ Error cases handled
- ✅ Loading states implemented
- ✅ Mobile responsive
- ✅ Documentation updated

---

## 🚀 DEPLOYMENT CHECKLIST

After completing sprints:
1. Run all tests: `./gradlew test`
2. Check test coverage: `./gradlew jacocoTestReport`
3. Build production JAR: `./gradlew build`
4. Test production build locally
5. Update CHANGELOG.md
6. Commit with meaningful message

---

**IMPORTANT:** Focus on getting a working MVP first. Don't over-engineer!
Start with Task 1.1 (Dashboard) and work sequentially.

Good luck! 🚀