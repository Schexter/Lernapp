package com.fachinformatiker.lernapp.integration;

import com.fachinformatiker.lernapp.domain.model.*;
import com.fachinformatiker.lernapp.domain.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

/**
 * Integration Tests für Repository Layer
 * Testet die Datenbankzugriffe und Custom Queries
 * 
 * @author Hans Hahn
 */
@DataJpaTest
@ActiveProfiles("test")
@DisplayName("Repository Integration Tests")
public class RepositoryIntegrationTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private QuestionRepository questionRepository;

    @Autowired
    private TopicRepository topicRepository;

    @Autowired
    private ProgressRepository progressRepository;

    private User testUser;
    private Topic testTopic;
    private Question testQuestion;

    @BeforeEach
    void setUp() {
        // Clear database
        entityManager.clear();

        // Create test data
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setEmail("test@example.com");
        testUser.setPasswordHash("hashed_password");
        testUser.setActive(true);
        testUser = entityManager.persistAndFlush(testUser);

        testTopic = new Topic();
        testTopic.setName("Java Grundlagen");
        testTopic.setDescription("Basics of Java");
        testTopic.setSortOrder(1);
        testTopic = entityManager.persistAndFlush(testTopic);

        testQuestion = new Question();
        testQuestion.setTopic(testTopic);
        testQuestion.setQuestionText("Was ist Java?");
        testQuestion.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        testQuestion.setDifficultyLevel(DifficultyLevel.BEGINNER);
        testQuestion.setExplanation("Java ist eine Programmiersprache");

        Answer answer1 = new Answer();
        answer1.setAnswerText("Eine Programmiersprache");
        answer1.setCorrect(true);
        answer1.setQuestion(testQuestion);

        Answer answer2 = new Answer();
        answer2.setAnswerText("Ein Kaffee");
        answer2.setCorrect(false);
        answer2.setQuestion(testQuestion);

        testQuestion.setAnswers(Set.of(answer1, answer2));
        testQuestion = entityManager.persistAndFlush(testQuestion);
    }

    @Test
    @DisplayName("Sollte User per Username finden")
    void shouldFindUserByUsername() {
        // When
        Optional<User> found = userRepository.findByUsername("testuser");

        // Then
        assertAll(
            () -> assertThat(found).isPresent(),
            () -> assertThat(found.get().getEmail()).isEqualTo("test@example.com"),
            () -> assertThat(found.get().isActive()).isTrue()
        );
    }

    @Test
    @DisplayName("Sollte User per Email finden")
    void shouldFindUserByEmail() {
        // When
        Optional<User> found = userRepository.findByEmail("test@example.com");

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Sollte prüfen ob Username existiert")
    void shouldCheckIfUsernameExists() {
        // When
        boolean exists = userRepository.existsByUsername("testuser");
        boolean notExists = userRepository.existsByUsername("nonexistent");

        // Then
        assertThat(exists).isTrue();
        assertThat(notExists).isFalse();
    }

    @Test
    @DisplayName("Sollte aktive User finden")
    void shouldFindActiveUsers() {
        // Given
        User inactiveUser = new User();
        inactiveUser.setUsername("inactive");
        inactiveUser.setEmail("inactive@example.com");
        inactiveUser.setPasswordHash("hash");
        inactiveUser.setActive(false);
        entityManager.persistAndFlush(inactiveUser);

        // When
        List<User> activeUsers = userRepository.findByActiveTrue();

        // Then
        assertThat(activeUsers).hasSize(1);
        assertThat(activeUsers.get(0).getUsername()).isEqualTo("testuser");
    }

    @Test
    @DisplayName("Sollte Fragen nach Topic finden")
    void shouldFindQuestionsByTopic() {
        // Given
        Question anotherQuestion = new Question();
        anotherQuestion.setTopic(testTopic);
        anotherQuestion.setQuestionText("Was ist OOP?");
        anotherQuestion.setQuestionType(QuestionType.TEXT);
        anotherQuestion.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
        entityManager.persistAndFlush(anotherQuestion);

        // When
        List<Question> questions = questionRepository.findByTopicId(testTopic.getId());

        // Then
        assertThat(questions).hasSize(2);
        assertThat(questions).extracting(Question::getQuestionText)
            .containsExactlyInAnyOrder("Was ist Java?", "Was ist OOP?");
    }

    @Test
    @DisplayName("Sollte Fragen nach Schwierigkeit finden")
    void shouldFindQuestionsByDifficulty() {
        // Given
        Question advancedQuestion = new Question();
        advancedQuestion.setTopic(testTopic);
        advancedQuestion.setQuestionText("Advanced Question");
        advancedQuestion.setQuestionType(QuestionType.TEXT);
        advancedQuestion.setDifficultyLevel(DifficultyLevel.ADVANCED);
        entityManager.persistAndFlush(advancedQuestion);

        // When
        List<Question> beginnerQuestions = questionRepository.findByDifficultyLevel(DifficultyLevel.BEGINNER);
        List<Question> advancedQuestions = questionRepository.findByDifficultyLevel(DifficultyLevel.ADVANCED);

        // Then
        assertThat(beginnerQuestions).hasSize(1);
        assertThat(advancedQuestions).hasSize(1);
    }

    @Test
    @DisplayName("Sollte Fragen mit Volltextsuche finden")
    void shouldSearchQuestions() {
        // When
        List<Question> foundQuestions = questionRepository.searchByText("Java");

        // Then
        assertThat(foundQuestions).hasSize(1);
        assertThat(foundQuestions.get(0).getQuestionText()).contains("Java");
    }

    @Test
    @DisplayName("Sollte zufällige Fragen zurückgeben")
    void shouldGetRandomQuestions() {
        // Given - Add more questions
        for (int i = 0; i < 10; i++) {
            Question q = new Question();
            q.setTopic(testTopic);
            q.setQuestionText("Question " + i);
            q.setQuestionType(QuestionType.MULTIPLE_CHOICE);
            q.setDifficultyLevel(DifficultyLevel.INTERMEDIATE);
            entityManager.persistAndFlush(q);
        }

        // When
        List<Question> randomQuestions = questionRepository.findRandomQuestions(5);

        // Then
        assertThat(randomQuestions).hasSize(5);
    }

    @Test
    @DisplayName("Sollte Topics mit Hierarchie verwalten")
    void shouldManageTopicHierarchy() {
        // Given
        Topic parentTopic = new Topic();
        parentTopic.setName("Programmierung");
        parentTopic.setDescription("Allgemeine Programmierung");
        parentTopic = entityManager.persistAndFlush(parentTopic);

        Topic childTopic1 = new Topic();
        childTopic1.setName("Java");
        childTopic1.setParent(parentTopic);
        childTopic1 = entityManager.persistAndFlush(childTopic1);

        Topic childTopic2 = new Topic();
        childTopic2.setName("Python");
        childTopic2.setParent(parentTopic);
        childTopic2 = entityManager.persistAndFlush(childTopic2);

        // When
        List<Topic> children = topicRepository.findByParentId(parentTopic.getId());
        List<Topic> rootTopics = topicRepository.findByParentIsNull();

        // Then
        assertThat(children).hasSize(2);
        assertThat(children).extracting(Topic::getName)
            .containsExactlyInAnyOrder("Java", "Python");
        assertThat(rootTopics).contains(parentTopic, testTopic);
    }

    @Test
    @DisplayName("Sollte Progress für User und Question speichern")
    void shouldSaveProgressForUserAndQuestion() {
        // Given
        Progress progress = new Progress();
        progress.setUser(testUser);
        progress.setQuestion(testQuestion);
        progress.setAttempts(3);
        progress.setCorrectAttempts(2);
        progress.setConfidenceLevel(0.67);

        // When
        Progress saved = progressRepository.save(progress);
        Optional<Progress> found = progressRepository.findByUserIdAndQuestionId(
            testUser.getId(), testQuestion.getId()
        );

        // Then
        assertAll(
            () -> assertThat(saved.getId()).isNotNull(),
            () -> assertThat(found).isPresent(),
            () -> assertThat(found.get().getConfidenceLevel()).isEqualTo(0.67),
            () -> assertThat(found.get().getAttempts()).isEqualTo(3)
        );
    }

    @Test
    @DisplayName("Sollte Progress nach User finden")
    void shouldFindProgressByUser() {
        // Given
        Progress progress1 = new Progress();
        progress1.setUser(testUser);
        progress1.setQuestion(testQuestion);
        progress1.setAttempts(5);
        progress1.setCorrectAttempts(3);
        entityManager.persistAndFlush(progress1);

        // Create another question and progress
        Question question2 = new Question();
        question2.setTopic(testTopic);
        question2.setQuestionText("Another question");
        question2.setQuestionType(QuestionType.TRUE_FALSE);
        question2.setDifficultyLevel(DifficultyLevel.BEGINNER);
        question2 = entityManager.persistAndFlush(question2);

        Progress progress2 = new Progress();
        progress2.setUser(testUser);
        progress2.setQuestion(question2);
        progress2.setAttempts(2);
        progress2.setCorrectAttempts(2);
        entityManager.persistAndFlush(progress2);

        // When
        List<Progress> userProgress = progressRepository.findByUserId(testUser.getId());

        // Then
        assertThat(userProgress).hasSize(2);
        assertThat(userProgress).extracting(Progress::getAttempts)
            .containsExactlyInAnyOrder(5, 2);
    }

    @Test
    @DisplayName("Sollte Topics paginiert laden")
    void shouldLoadTopicsPaginated() {
        // Given - Create more topics
        for (int i = 0; i < 15; i++) {
            Topic topic = new Topic();
            topic.setName("Topic " + i);
            topic.setDescription("Description " + i);
            entityManager.persistAndFlush(topic);
        }

        // When
        Page<Topic> firstPage = topicRepository.findAll(PageRequest.of(0, 10));
        Page<Topic> secondPage = topicRepository.findAll(PageRequest.of(1, 10));

        // Then
        assertAll(
            () -> assertThat(firstPage.getContent()).hasSize(10),
            () -> assertThat(secondPage.getContent()).hasSize(6), // 15 + 1 original = 16 total
            () -> assertThat(firstPage.getTotalElements()).isEqualTo(16),
            () -> assertThat(firstPage.getTotalPages()).isEqualTo(2)
        );
    }

    @Test
    @DisplayName("Sollte Cascade Operations korrekt durchführen")
    void shouldHandleCascadeOperations() {
        // Given
        Question questionWithAnswers = new Question();
        questionWithAnswers.setTopic(testTopic);
        questionWithAnswers.setQuestionText("Cascade Test");
        questionWithAnswers.setQuestionType(QuestionType.MULTIPLE_CHOICE);
        questionWithAnswers.setDifficultyLevel(DifficultyLevel.BEGINNER);

        Answer a1 = new Answer();
        a1.setAnswerText("Answer 1");
        a1.setCorrect(true);
        a1.setQuestion(questionWithAnswers);

        Answer a2 = new Answer();
        a2.setAnswerText("Answer 2");
        a2.setCorrect(false);
        a2.setQuestion(questionWithAnswers);

        questionWithAnswers.setAnswers(Set.of(a1, a2));

        // When
        Question saved = questionRepository.save(questionWithAnswers);
        entityManager.flush();
        entityManager.clear();

        Optional<Question> loaded = questionRepository.findById(saved.getId());

        // Then
        assertAll(
            () -> assertThat(loaded).isPresent(),
            () -> assertThat(loaded.get().getAnswers()).hasSize(2),
            () -> assertThat(loaded.get().getAnswers())
                .extracting(Answer::getAnswerText)
                .containsExactlyInAnyOrder("Answer 1", "Answer 2")
        );
    }
}
