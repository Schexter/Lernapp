package com.fachinformatiker.lernapp.integration;

import com.fachinformatiker.lernapp.controller.UserController;
import com.fachinformatiker.lernapp.controller.QuestionController;
import com.fachinformatiker.lernapp.controller.LearningController;
import com.fachinformatiker.lernapp.dto.*;
import com.fachinformatiker.lernapp.service.UserService;
import com.fachinformatiker.lernapp.service.QuestionService;
import com.fachinformatiker.lernapp.service.LearningService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.*;

/**
 * REST Controller Integration Tests
 * Testet die API Endpoints End-to-End
 * 
 * @author Hans Hahn
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@DisplayName("REST API Integration Tests")
public class RestApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @MockBean
    private QuestionService questionService;

    @MockBean
    private LearningService learningService;

    private UserDTO testUser;
    private QuestionDTO testQuestion;
    private LearningSessionDTO testSession;

    @BeforeEach
    void setUp() {
        // Setup test data
        testUser = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("test@example.com")
                .firstName("Test")
                .lastName("User")
                .active(true)
                .build();

        testQuestion = QuestionDTO.builder()
                .id(1L)
                .topicId(1L)
                .questionText("Was ist Java?")
                .questionType(com.fachinformatiker.lernapp.domain.model.QuestionType.MULTIPLE_CHOICE)
                .difficultyLevel(com.fachinformatiker.lernapp.domain.model.DifficultyLevel.BEGINNER)
                .build();

        testSession = LearningSessionDTO.builder()
                .id(1L)
                .userId(1L)
                .topicId(1L)
                .topicName("Java Grundlagen")
                .totalQuestions(10)
                .answeredQuestions(0)
                .correctAnswers(0)
                .active(true)
                .build();
    }

    // ============= USER CONTROLLER TESTS =============

    @Test
    @DisplayName("POST /api/v1/users/register - Sollte neuen User registrieren")
    void shouldRegisterNewUser() throws Exception {
        // Given
        UserRegistrationDTO registration = UserRegistrationDTO.builder()
                .username("newuser")
                .email("new@example.com")
                .password("password123")
                .firstName("New")
                .lastName("User")
                .build();

        when(userService.registerUser(any(UserRegistrationDTO.class))).thenReturn(testUser);

        // When & Then
        mockMvc.perform(post("/api/v1/users/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(registration)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.email").value("test@example.com"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Sollte User per ID zurückgeben")
    void shouldGetUserById() throws Exception {
        // Given
        when(userService.getUserById(1L)).thenReturn(Optional.of(testUser));

        // When & Then
        mockMvc.perform(get("/api/v1/users/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.username").value("testuser"));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} - Sollte 404 bei nicht existierendem User zurückgeben")
    void shouldReturn404ForNonExistentUser() throws Exception {
        // Given
        when(userService.getUserById(999L)).thenReturn(Optional.empty());

        // When & Then
        mockMvc.perform(get("/api/v1/users/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} - Sollte User Profile updaten")
    void shouldUpdateUserProfile() throws Exception {
        // Given
        UserUpdateDTO updateDTO = UserUpdateDTO.builder()
                .firstName("Updated")
                .lastName("Name")
                .email("updated@example.com")
                .build();

        UserDTO updatedUser = UserDTO.builder()
                .id(1L)
                .username("testuser")
                .email("updated@example.com")
                .firstName("Updated")
                .lastName("Name")
                .build();

        when(userService.updateUser(any(Long.class), any(UserUpdateDTO.class)))
                .thenReturn(updatedUser);

        // When & Then
        mockMvc.perform(put("/api/v1/users/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("Updated"))
                .andExpect(jsonPath("$.email").value("updated@example.com"));
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} - Sollte User löschen")
    void shouldDeleteUser() throws Exception {
        // Given
        when(userService.deleteUser(1L)).thenReturn(true);

        // When & Then
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNoContent());
    }

    // ============= QUESTION CONTROLLER TESTS =============

    @Test
    @DisplayName("POST /api/v1/questions - Sollte neue Frage erstellen")
    void shouldCreateQuestion() throws Exception {
        // Given
        when(questionService.createQuestion(any(QuestionDTO.class))).thenReturn(testQuestion);

        // When & Then
        mockMvc.perform(post("/api/v1/questions")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testQuestion)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.questionText").value("Was ist Java?"));
    }

    @Test
    @DisplayName("GET /api/v1/questions/{id} - Sollte Frage per ID zurückgeben")
    void shouldGetQuestionById() throws Exception {
        // Given
        when(questionService.getQuestionById(1L)).thenReturn(Optional.of(testQuestion));

        // When & Then
        mockMvc.perform(get("/api/v1/questions/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.questionText").value("Was ist Java?"));
    }

    @Test
    @DisplayName("GET /api/v1/questions/topic/{topicId} - Sollte Fragen nach Topic filtern")
    void shouldGetQuestionsByTopic() throws Exception {
        // Given
        when(questionService.getQuestionsByTopic(1L)).thenReturn(List.of(testQuestion));

        // When & Then
        mockMvc.perform(get("/api/v1/questions/topic/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].topicId").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/questions/search - Sollte Fragen suchen")
    void shouldSearchQuestions() throws Exception {
        // Given
        when(questionService.searchQuestions("Java", null)).thenReturn(List.of(testQuestion));

        // When & Then
        mockMvc.perform(get("/api/v1/questions/search")
                .param("query", "Java"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].questionText", containsString("Java")));
    }

    @Test
    @DisplayName("POST /api/v1/questions/{id}/validate - Sollte Antwort validieren")
    void shouldValidateAnswer() throws Exception {
        // Given
        AnswerSubmissionDTO submission = AnswerSubmissionDTO.builder()
                .userId(1L)
                .questionId(1L)
                .answerId(1L)
                .build();

        AnswerValidationDTO validation = AnswerValidationDTO.builder()
                .correct(true)
                .explanation("Richtig! Java ist eine Programmiersprache.")
                .build();

        when(questionService.validateAnswer(any(Long.class), any(AnswerSubmissionDTO.class)))
                .thenReturn(validation);

        // When & Then
        mockMvc.perform(post("/api/v1/questions/1/validate")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.explanation").exists());
    }

    // ============= LEARNING CONTROLLER TESTS =============

    @Test
    @DisplayName("POST /api/v1/learning/sessions - Sollte neue Lernsession erstellen")
    void shouldCreateLearningSession() throws Exception {
        // Given
        when(learningService.createLearningSession(1L, 1L, 10)).thenReturn(testSession);

        // When & Then
        mockMvc.perform(post("/api/v1/learning/sessions")
                .param("userId", "1")
                .param("topicId", "1")
                .param("questionCount", "10"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.topicName").value("Java Grundlagen"))
                .andExpect(jsonPath("$.totalQuestions").value(10));
    }

    @Test
    @DisplayName("GET /api/v1/learning/sessions/active - Sollte aktive Session zurückgeben")
    void shouldGetActiveSession() throws Exception {
        // Given
        when(learningService.getActiveSession(1L)).thenReturn(Optional.of(testSession));

        // When & Then
        mockMvc.perform(get("/api/v1/learning/sessions/active")
                .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(true))
                .andExpect(jsonPath("$.userId").value(1));
    }

    @Test
    @DisplayName("POST /api/v1/learning/answer - Sollte Antwort submitten")
    void shouldSubmitAnswer() throws Exception {
        // Given
        AnswerSubmissionDTO submission = AnswerSubmissionDTO.builder()
                .userId(1L)
                .questionId(1L)
                .answerId(1L)
                .build();

        AnswerResponseDTO response = AnswerResponseDTO.builder()
                .correct(true)
                .explanation("Korrekt!")
                .newConfidenceLevel(0.8)
                .totalAttempts(1)
                .correctAttempts(1)
                .build();

        when(learningService.submitAnswer(any(AnswerSubmissionDTO.class))).thenReturn(response);

        // When & Then
        mockMvc.perform(post("/api/v1/learning/answer")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(submission)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correct").value(true))
                .andExpect(jsonPath("$.newConfidenceLevel").value(0.8));
    }

    @Test
    @DisplayName("GET /api/v1/learning/progress/{userId} - Sollte User Progress zurückgeben")
    void shouldGetUserProgress() throws Exception {
        // Given
        ProgressDTO progress = ProgressDTO.builder()
                .userId(1L)
                .totalQuestions(100)
                .answeredQuestions(50)
                .correctAnswers(40)
                .averageConfidence(0.75)
                .build();

        when(learningService.getUserProgress(1L)).thenReturn(progress);

        // When & Then
        mockMvc.perform(get("/api/v1/learning/progress/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalQuestions").value(100))
                .andExpect(jsonPath("$.answeredQuestions").value(50))
                .andExpect(jsonPath("$.averageConfidence").value(0.75));
    }

    @Test
    @DisplayName("GET /api/v1/learning/streak/{userId} - Sollte Learning Streak zurückgeben")
    void shouldGetLearningStreak() throws Exception {
        // Given
        StreakDTO streak = StreakDTO.builder()
                .currentStreak(7)
                .longestStreak(15)
                .activeToday(true)
                .build();

        when(learningService.getLearningStreak(1L)).thenReturn(streak);

        // When & Then
        mockMvc.perform(get("/api/v1/learning/streak/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.currentStreak").value(7))
                .andExpect(jsonPath("$.longestStreak").value(15))
                .andExpect(jsonPath("$.activeToday").value(true));
    }

    @Test
    @DisplayName("GET /api/v1/learning/weak-topics/{userId} - Sollte schwache Themen identifizieren")
    void shouldGetWeakTopics() throws Exception {
        // Given
        TopicProgressDTO weakTopic = TopicProgressDTO.builder()
                .topicId(1L)
                .topicName("Netzwerke")
                .averageConfidence(0.3)
                .status("LEARNING")
                .build();

        when(learningService.getWeakTopics(1L, 0.5)).thenReturn(List.of(weakTopic));

        // When & Then
        mockMvc.perform(get("/api/v1/learning/weak-topics/1")
                .param("confidenceThreshold", "0.5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].topicName").value("Netzwerke"))
                .andExpect(jsonPath("$[0].averageConfidence").value(0.3));
    }

    @Test
    @DisplayName("GET /api/v1/learning/recommendations/{userId} - Sollte Empfehlungen generieren")
    void shouldGetRecommendations() throws Exception {
        // Given
        RecommendationDTO recommendations = RecommendationDTO.builder()
                .userId(1L)
                .recommendedQuestionIds(List.of(1L, 2L, 3L))
                .recommendedTopicIds(List.of(1L))
                .learningStrategy("Fokus auf schwache Themen")
                .focusArea("Netzwerke")
                .recommendedDailyQuestions(15)
                .build();

        when(learningService.generateRecommendations(1L)).thenReturn(recommendations);

        // When & Then
        mockMvc.perform(get("/api/v1/learning/recommendations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.focusArea").value("Netzwerke"))
                .andExpect(jsonPath("$.recommendedDailyQuestions").value(15));
    }
}
