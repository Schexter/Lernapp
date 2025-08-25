# Fachinformatiker Lernapp REST API Documentation

## Base URL
- Development: `http://localhost:8080/api/v1`
- Production: `https://api.lernapp.de/api/v1`

## Authentication
Alle Endpoints (außer Registration und Login) benötigen einen JWT Token im Authorization Header:
```
Authorization: Bearer <jwt-token>
```

## API Endpoints

### User Management

#### Register User
```http
POST /users/register
Content-Type: application/json

{
  "username": "johndoe",
  "email": "john@example.com",
  "password": "SecurePass123!",
  "firstName": "John",
  "lastName": "Doe"
}

Response: 201 Created
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "active": true,
  "createdAt": "2025-08-13T10:00:00Z"
}
```

#### Get User Profile
```http
GET /users/{id}
Authorization: Bearer <token>

Response: 200 OK
{
  "id": 1,
  "username": "johndoe",
  "email": "john@example.com",
  "firstName": "John",
  "lastName": "Doe",
  "roles": ["USER"],
  "active": true,
  "lastLogin": "2025-08-13T09:30:00Z"
}
```

#### Update User Profile
```http
PUT /users/{id}
Authorization: Bearer <token>
Content-Type: application/json

{
  "firstName": "Johnny",
  "lastName": "Doe Jr.",
  "email": "johnny@example.com"
}

Response: 200 OK
```

### Question Management

#### Create Question
```http
POST /questions
Authorization: Bearer <token>
Content-Type: application/json

{
  "topicId": 1,
  "questionText": "Was ist der Unterschied zwischen == und === in JavaScript?",
  "questionType": "MULTIPLE_CHOICE",
  "difficultyLevel": "INTERMEDIATE",
  "explanation": "=== prüft Typ und Wert, == nur Wert mit Type Coercion",
  "answers": [
    {
      "answerText": "=== prüft Typ und Wert",
      "correct": true,
      "explanation": "Strict Equality"
    },
    {
      "answerText": "Kein Unterschied",
      "correct": false
    }
  ]
}

Response: 201 Created
```

#### Get Questions by Topic
```http
GET /questions/topic/{topicId}
Authorization: Bearer <token>

Response: 200 OK
[
  {
    "id": 1,
    "topicId": 1,
    "questionText": "Was ist Java?",
    "questionType": "MULTIPLE_CHOICE",
    "difficultyLevel": "BEGINNER",
    "answers": [...]
  }
]
```

#### Search Questions
```http
GET /questions/search?query=java&topicId=1
Authorization: Bearer <token>

Response: 200 OK
[
  {
    "id": 1,
    "questionText": "Was ist Java?",
    ...
  }
]
```

### Learning Management

#### Create Learning Session
```http
POST /learning/sessions?userId=1&topicId=1&questionCount=20
Authorization: Bearer <token>

Response: 201 Created
{
  "id": 123,
  "userId": 1,
  "topicId": 1,
  "topicName": "Java Grundlagen",
  "questions": [...],
  "totalQuestions": 20,
  "answeredQuestions": 0,
  "correctAnswers": 0,
  "startedAt": "2025-08-13T10:00:00Z",
  "active": true
}
```

#### Submit Answer
```http
POST /learning/answer
Authorization: Bearer <token>
Content-Type: application/json

{
  "userId": 1,
  "questionId": 5,
  "answerId": 12
}

Response: 200 OK
{
  "correct": true,
  "explanation": "Richtig! Java ist eine objektorientierte Programmiersprache.",
  "newConfidenceLevel": 0.75,
  "nextReview": "2025-08-16T10:00:00Z",
  "totalAttempts": 3,
  "correctAttempts": 2
}
```

#### Get User Progress
```http
GET /learning/progress/{userId}?topicId=1
Authorization: Bearer <token>

Response: 200 OK
{
  "userId": 1,
  "totalQuestions": 150,
  "answeredQuestions": 75,
  "correctAnswers": 60,
  "averageConfidence": 0.72,
  "masteredQuestions": 45,
  "learningQuestions": 20,
  "newQuestions": 75
}
```

#### Get Questions for Review (Spaced Repetition)
```http
GET /learning/review/{userId}?limit=10
Authorization: Bearer <token>

Response: 200 OK
[
  {
    "id": 5,
    "questionText": "Was ist Polymorphismus?",
    "lastReview": "2025-08-10T10:00:00Z",
    "confidenceLevel": 0.6,
    ...
  }
]
```

#### Get Learning Streak
```http
GET /learning/streak/{userId}
Authorization: Bearer <token>

Response: 200 OK
{
  "currentStreak": 7,
  "longestStreak": 21,
  "lastActivityDate": "2025-08-13",
  "activeToday": true,
  "daysUntilStreakLoss": 1
}
```

#### Get Weak Topics
```http
GET /learning/weak-topics/{userId}?confidenceThreshold=0.5
Authorization: Bearer <token>

Response: 200 OK
[
  {
    "topicId": 3,
    "topicName": "Netzwerke",
    "totalQuestions": 50,
    "answeredQuestions": 30,
    "correctAnswers": 12,
    "averageConfidence": 0.35,
    "status": "LEARNING"
  }
]
```

#### Get Learning Recommendations
```http
GET /learning/recommendations/{userId}
Authorization: Bearer <token>

Response: 200 OK
{
  "userId": 1,
  "recommendedQuestionIds": [12, 15, 23, 45],
  "recommendedTopicIds": [3, 5],
  "learningStrategy": "Fokussiere dich auf schwache Themen und wiederhole alte Fragen",
  "focusArea": "Netzwerke und Datenbanken",
  "recommendedDailyQuestions": 15
}
```

#### Get Learning Statistics
```http
GET /learning/statistics/{userId}?days=30
Authorization: Bearer <token>

Response: 200 OK
{
  "userId": 1,
  "totalSessions": 25,
  "totalQuestions": 375,
  "correctAnswers": 280,
  "averageScore": 0.75,
  "totalTimeMinutes": 450,
  "dailyActivity": {
    "2025-08-13": 15,
    "2025-08-12": 20,
    ...
  },
  "topicPerformance": {
    "Java Grundlagen": 0.85,
    "Netzwerke": 0.45,
    ...
  },
  "improvementRate": 0.12
}
```

### Error Responses

#### 400 Bad Request
```json
{
  "timestamp": "2025-08-13T10:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/v1/users/register"
}
```

#### 401 Unauthorized
```json
{
  "timestamp": "2025-08-13T10:00:00Z",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid or expired token",
  "path": "/api/v1/questions"
}
```

#### 404 Not Found
```json
{
  "timestamp": "2025-08-13T10:00:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: 999",
  "path": "/api/v1/users/999"
}
```

## Swagger UI
Interactive API documentation available at:
- Development: http://localhost:8080/swagger-ui.html
- API Spec: http://localhost:8080/v3/api-docs

## Rate Limiting
- 100 requests per minute per IP for anonymous users
- 1000 requests per minute for authenticated users

## Pagination
All list endpoints support pagination:
```
GET /questions?page=0&size=20&sort=createdAt,desc
```

## Filtering
Most endpoints support filtering:
```
GET /questions?topicId=1&difficulty=BEGINNER&type=MULTIPLE_CHOICE
```

---
*Erstellt von Hans Hahn - Alle Rechte vorbehalten*
