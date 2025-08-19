# Java Spring Boot Fachinformatiker-Lernapp - Projektfahrplan
*Erstellt von Hans Hahn - Alle Rechte vorbehalten*

## 🎯 Projekt-Vision

**Eine hochperformante, skalierbare Web-basierte Lernplattform für Fachinformatiker-Azubis mit Enterprise-Features und optimaler User Experience.**

## 📋 TODO - Master Roadmap

### 🏗 Phase 1: Foundation Setup (Woche 1-2)

#### Sprint 1.1: Projekt-Initialisierung
- [ ] **Gradle Multi-Module Setup**
  - Core Module (Entities, Services)
  - Web Module (Controllers, Templates)
  - Security Module (Authentication)
  - Test Module (Integration Tests)

- [ ] **Spring Boot Basis-Konfiguration**
  - Application Properties (dev/staging/prod)
  - Database Configuration (H2 dev, PostgreSQL prod)
  - Security Configuration (JWT + Session)
  - Logging Configuration (Logback + File Rolling)

- [ ] **Development Environment**
  - Docker Compose Setup
  - IntelliJ IDEA Konfiguration
  - Git Hooks für Code Quality
  - Gradle Tasks für Build Automation

#### Sprint 1.2: Domain Model Design
- [ ] **Core Entities** implementieren:
  ```java
  User, Role, Permission // Authentication
  Question, Answer, Topic // Learning Content
  LearningPath, Progress // User Progress
  Examination, Session // Testing
  ```

- [ ] **Repository Layer** (Spring Data JPA):
  - Custom Queries für komplexe Abfragen
  - Pagination & Sorting Support
  - Database Migration Scripts (Flyway)

- [ ] **Service Layer Foundation**:
  - UserService (Registration, Profile)
  - QuestionService (CRUD, Search)
  - ProgressService (Tracking, Analytics)

### 🔐 Phase 2: Authentication & Security (Woche 3)

#### Sprint 2.1: Security Implementation
- [ ] **Spring Security Configuration**
  - JWT Token Implementation
  - Role-based Access Control (RBAC)
  - Password Encryption (BCrypt)
  - Session Management

- [ ] **Authentication Endpoints**
  ```java
  POST /api/auth/register
  POST /api/auth/login
  POST /api/auth/logout
  POST /api/auth/refresh
  POST /api/auth/forgot-password
  ```

- [ ] **Security Testing**
  - Unit Tests für Authentication
  - Integration Tests für Authorization
  - Security Penetration Tests

### 🎨 Phase 3: Web Frontend (Woche 4-5)

#### Sprint 3.1: Template Engine & UI Framework
- [ ] **Thymeleaf Setup**
  - Layout Templates (Header, Footer, Navigation)
  - Fragment System für Wiederverwendung
  - Internationalization (i18n) Support

- [ ] **HTMX Integration**
  - Dynamic Form Submissions
  - Partial Page Updates
  - Progressive Enhancement
  - Error Handling

- [ ] **Styling & Components**
  - Tailwind CSS Integration
  - Component Library erstellen
  - Responsive Design System
  - Dark/Light Mode Toggle

#### Sprint 3.2: Core UI Pages
- [ ] **Authentication Pages**
  - Login Form (mit Remember Me)
  - Registration Form (mit Validation)
  - Password Reset Flow
  - Email Verification

- [ ] **Dashboard & Navigation**
  - User Dashboard (Progress Overview)
  - Main Navigation (Responsive)
  - Breadcrumb Navigation
  - Quick Actions Panel

### 📚 Phase 4: Learning Engine (Woche 6-7)

#### Sprint 4.1: Question Management
- [ ] **Question CRUD Operations**
  - Create/Edit Question Interface
  - Multiple Choice Support
  - Image/Code Snippet Support
  - Difficulty Rating System

- [ ] **Topic Organization**
  - Hierarchical Topic Structure
  - Tag System für Kategorisierung
  - Search & Filter Functionality
  - Content Moderation Tools

#### Sprint 4.2: Learning Algorithm
- [ ] **Adaptive Learning System**
  ```java
  // Spaced Repetition Algorithm
  public class SpacedRepetitionService {
      public LocalDateTime calculateNextReview(
          Question question, 
          UserResponse response,
          LearningProgress progress
      );
  }
  ```

- [ ] **Progress Tracking**
  - Learning Streaks
  - Performance Analytics
  - Weakness Identification
  - Recommendation Engine

### 🎓 Phase 5: Examination System (Woche 8)

#### Sprint 5.1: Prüfungsmodus
- [ ] **Examination Engine**
  - Timed Test Sessions
  - Question Randomization
  - Auto-Save Functionality
  - Anti-Cheating Measures

- [ ] **Result Analysis**
  - Detailed Score Reports
  - Performance Comparison
  - Weakness Analysis
  - Improvement Suggestions

### 🚀 Phase 6: Performance & Deployment (Woche 9-10)

#### Sprint 6.1: Optimization
- [ ] **Performance Tuning**
  - Database Query Optimization
  - Caching Strategy (Redis)
  - Image Optimization
  - CDN Integration

- [ ] **Production Deployment**
  - Docker Multi-Stage Builds
  - Kubernetes Manifests
  - CI/CD Pipeline (GitHub Actions)
  - Monitoring & Alerting

## 🏗 Detaillierte Architektur

### 1. Module Structure
```
fachinformatiker-lernapp/
├── lernapp-core/           # Domain Models & Business Logic
│   ├── src/main/java/
│   │   ├── model/         # JPA Entities
│   │   ├── repository/    # Data Access Layer
│   │   └── service/       # Business Services
│   └── build.gradle
├── lernapp-security/       # Authentication & Authorization
│   ├── src/main/java/
│   │   ├── config/        # Security Configuration
│   │   ├── jwt/          # JWT Implementation
│   │   └── oauth/        # OAuth2 Support (future)
│   └── build.gradle
├── lernapp-web/           # Web Layer (Controllers & Templates)
│   ├── src/main/java/
│   │   ├── controller/    # REST & Web Controllers
│   │   └── dto/          # Data Transfer Objects
│   ├── src/main/resources/
│   │   ├── templates/     # Thymeleaf Templates
│   │   └── static/       # CSS, JS, Images
│   └── build.gradle
├── lernapp-integration/   # Integration Tests
└── build.gradle
```

### 2. Database Schema Design
```sql
-- Core User Management
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) UNIQUE NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    first_name VARCHAR(100),
    last_name VARCHAR(100),
    learning_progress JSONB DEFAULT '{}',
    preferences JSONB DEFAULT '{}',
    created_at TIMESTAMP DEFAULT NOW(),
    updated_at TIMESTAMP DEFAULT NOW()
);

-- Learning Content
CREATE TABLE topics (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    parent_id BIGINT REFERENCES topics(id),
    sort_order INTEGER DEFAULT 0
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    topic_id BIGINT REFERENCES topics(id),
    question_text TEXT NOT NULL,
    question_type VARCHAR(20) DEFAULT 'MULTIPLE_CHOICE',
    difficulty_level INTEGER DEFAULT 1,
    explanation TEXT,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    question_id BIGINT REFERENCES questions(id),
    answer_text TEXT NOT NULL,
    is_correct BOOLEAN DEFAULT FALSE,
    sort_order INTEGER DEFAULT 0
);

-- Progress Tracking
CREATE TABLE user_progress (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT REFERENCES users(id),
    question_id BIGINT REFERENCES questions(id),
    attempts INTEGER DEFAULT 0,
    correct_attempts INTEGER DEFAULT 0,
    last_attempt TIMESTAMP,
    next_review TIMESTAMP,
    confidence_level DECIMAL(3,2) DEFAULT 0.00,
    UNIQUE(user_id, question_id)
);
```

### 3. Service Layer Architecture
```java
@Service
@Transactional
public class LearningService {
    
    private final QuestionRepository questionRepository;
    private final ProgressRepository progressRepository;
    private final SpacedRepetitionAlgorithm algorithm;
    
    public LearningSession createLearningSession(User user, Topic topic) {
        // 1. Analyze user's progress in topic
        // 2. Select questions based on spaced repetition
        // 3. Create personalized learning session
        // 4. Track session start
    }
    
    public void submitAnswer(User user, Question question, Answer selectedAnswer) {
        // 1. Validate answer
        // 2. Update progress tracking
        // 3. Calculate next review date
        // 4. Update confidence level
        // 5. Trigger achievement checks
    }
}
```

### 4. Frontend Architecture
```html
<!-- Base Layout Template -->
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org" 
      xmlns:layout="http://www.ultraq.net.nz/thymeleaf/layout">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Fachinformatiker Lernapp</title>
    <script src="https://unpkg.com/htmx.org@1.9.6"></script>
    <script src="https://unpkg.com/alpinejs@3.x.x/dist/cdn.min.js" defer></script>
    <link href="/css/app.css" rel="stylesheet">
</head>
<body class="bg-gray-50 dark:bg-gray-900">
    <div th:replace="~{fragments/header :: header}"></div>
    <main layout:fragment="content"></main>
    <div th:replace="~{fragments/footer :: footer}"></div>
</body>
</html>
```

## 🧪 Testing Strategy

### 1. Unit Tests
```java
@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    
    @Mock
    private UserRepository userRepository;
    
    @InjectMocks
    private UserService userService;
    
    @Test
    @DisplayName("Should create user with encrypted password")
    void shouldCreateUserWithEncryptedPassword() {
        // Given
        UserRegistrationDTO registrationDTO = new UserRegistrationDTO(
            "testuser", "test@example.com", "password123"
        );
        
        // When
        User createdUser = userService.registerUser(registrationDTO);
        
        // Then
        assertThat(createdUser.getPassword()).isNotEqualTo("password123");
        assertThat(passwordEncoder.matches("password123", createdUser.getPassword())).isTrue();
    }
}
```

### 2. Integration Tests
```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = "spring.datasource.url=jdbc:h2:mem:testdb")
class LearningControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Test
    void shouldCreateLearningSession() {
        // Test complete user journey
    }
}
```

### 3. Performance Tests
```java
@Test
@Timeout(value = 2, unit = TimeUnit.SECONDS)
void shouldLoadQuestionsWithinTimeLimit() {
    // Performance regression tests
}
```

## 📊 Monitoring & Observability

### 1. Application Metrics
```yaml
# application.yml
management:
  endpoints:
    web:
      exposure:
        include: health,info,metrics,prometheus
  metrics:
    export:
      prometheus:
        enabled: true
```

### 2. Custom Metrics
```java
@Component
public class LearningMetrics {
    
    private final Counter questionsAnswered;
    private final Timer sessionDuration;
    
    public LearningMetrics(MeterRegistry meterRegistry) {
        this.questionsAnswered = Counter.builder("questions.answered")
            .description("Total questions answered")
            .register(meterRegistry);
    }
}
```

## 🔧 Development Tools

### 1. IDE Configuration
- **IntelliJ IDEA Settings** - Code Style, Inspections
- **Live Templates** - Boilerplate Code Generation
- **Plugins** - Spring Boot Assistant, Gradle
- **Database Tools** - Built-in DB Browser

### 2. Code Quality
```gradle
// build.gradle
plugins {
    id 'checkstyle'
    id 'pmd'
    id 'jacoco'
}

checkstyle {
    toolVersion = '10.12.4'
    configFile = file('checkstyle.xml')
}

jacoco {
    toolVersion = '0.8.8'
}

test {
    finalizedBy jacocoTestReport
    jacocoTestReport.dependsOn test
}
```

## 📅 Zeitplan & Meilensteine

### Woche 1-2: Foundation
- **M1**: Projekt Setup & Basic Configuration ✅
- **M2**: Database Schema & Entities ⏳
- **M3**: Security Foundation ⏳

### Woche 3-4: Core Features
- **M4**: Authentication System ⏳
- **M5**: Basic Web Interface ⏳
- **M6**: Question Management ⏳

### Woche 5-6: Advanced Features
- **M7**: Learning Algorithm ⏳
- **M8**: Progress Tracking ⏳
- **M9**: Examination System ⏳

### Woche 7-8: Polish & Deploy
- **M10**: Performance Optimization ⏳
- **M11**: Production Deployment ⏳
- **M12**: Go-Live! 🎯

## 🚨 Risiko-Management

### Technische Risiken
1. **Performance Issues**
   - *Mitigation*: Frühe Performance Tests, Profiling
2. **Database Migration Complexity**
   - *Mitigation*: Schrittweise Migration, Rollback-Plan
3. **Frontend Integration Challenges**
   - *Mitigation*: Prototype früh, iterative Entwicklung

### Projekt-Risiken
1. **Scope Creep**
   - *Mitigation*: Strenge Feature-Priorisierung
2. **Learning Curve**
   - *Mitigation*: Pair Programming, Code Reviews
3. **Time Constraints**
   - *Mitigation*: MVP-first Approach

## 📈 Success Metrics

### Performance KPIs
- Page Load Time < 1s
- API Response Time < 200ms
- 99.9% Uptime
- Support für 1000+ concurrent users

### Business KPIs
- User Engagement > 80%
- Learning Progress Improvement > 30%
- Bug Report Rate < 1%
- User Satisfaction Score > 4.5/5

---

**Next Action**: Projekt-Setup starten mit Gradle Multi-Module Structure!

*Status: Ready to Start Development 🚀*
