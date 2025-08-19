package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.*;
import com.fachinformatiker.lernapp.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.*;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("QuestionService Tests")
class QuestionServiceTest {

    @Mock
    private QuestionRepository questionRepository;

    @Mock
    private AnswerRepository answerRepository;

    @Mock
    private TopicRepository topicRepository;

    @Mock
    private TagRepository tagRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private QuestionService questionService;

    private Question testQuestion;
    private Topic testTopic;
    private User testUser;
    private Answer correctAnswer;
    private Answer wrongAnswer;

    @BeforeEach
    void setUp() {
        testTopic = Topic.builder()
            .id(1L)
            .name("Java Grundlagen")
            .description("Java basics")
            .build();

        testUser = User.builder()
            .id(1L)
            .username("instructor")
            .email("instructor@example.com")
            .build();

        correctAnswer = Answer.builder()
            .id(1L)
            .answerText("Correct answer")
            .isCorrect(true)
            .sortOrder(0)
            .build();

        wrongAnswer = Answer.builder()
            .id(2L)
            .answerText("Wrong answer")
            .isCorrect(false)
            .sortOrder(1)
            .build();

        testQuestion = Question.builder()
            .id(1L)
            .questionText("Was ist Java?")
            .questionType(Question.QuestionType.MULTIPLE_CHOICE)
            .difficultyLevel(2)
            .points(10)
            .topic(testTopic)
            .createdBy(testUser)
            .answers(Arrays.asList(correctAnswer, wrongAnswer))
            .tags(new ArrayList<>())
            .build();
    }

    @Test
    @DisplayName("Should find question by id with answers")
    void shouldFindQuestionByIdWithAnswers() {
        // Given
        when(questionRepository.findByIdWithAnswers(1L))
            .thenReturn(Optional.of(testQuestion));

        // When
        Optional<Question> found = questionService.findById(1L);

        // Then
        assertThat(found).isPresent();
        assertThat(found.get().getQuestionText()).isEqualTo("Was ist Java?");
        assertThat(found.get().getAnswers()).hasSize(2);
        verify(questionRepository).findByIdWithAnswers(1L);
    }

    @Test
    @DisplayName("Should create question successfully")
    void shouldCreateQuestionSuccessfully() {
        // Given
        Question newQuestion = Question.builder()
            .questionText("New question?")
            .questionType(Question.QuestionType.SINGLE_CHOICE)
            .difficultyLevel(1)
            .topic(testTopic)
            .answers(Arrays.asList(correctAnswer, wrongAnswer))
            .tags(new ArrayList<>())
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));
        when(questionRepository.save(any(Question.class))).thenReturn(newQuestion);
        when(answerRepository.save(any(Answer.class))).thenReturn(correctAnswer);

        // When
        Question created = questionService.createQuestion(newQuestion, 1L);

        // Then
        assertThat(created).isNotNull();
        verify(userRepository).findById(1L);
        verify(topicRepository).findById(1L);
        verify(questionRepository).save(any(Question.class));
        verify(answerRepository, times(2)).save(any(Answer.class));
    }

    @Test
    @DisplayName("Should validate question has correct answer")
    void shouldValidateQuestionHasCorrectAnswer() {
        // Given
        Answer wrongAnswer1 = Answer.builder()
            .answerText("Wrong 1")
            .isCorrect(false)
            .build();
        
        Answer wrongAnswer2 = Answer.builder()
            .answerText("Wrong 2")
            .isCorrect(false)
            .build();

        Question invalidQuestion = Question.builder()
            .questionText("Invalid question?")
            .questionType(Question.QuestionType.MULTIPLE_CHOICE)
            .difficultyLevel(1)
            .topic(testTopic)
            .answers(Arrays.asList(wrongAnswer1, wrongAnswer2))
            .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(topicRepository.findById(1L)).thenReturn(Optional.of(testTopic));

        // When/Then
        assertThatThrownBy(() -> questionService.createQuestion(invalidQuestion, 1L))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("At least one correct answer is required");

        verify(questionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find questions by topic")
    void shouldFindQuestionsByTopic() {
        // Given
        List<Question> questions = Arrays.asList(testQuestion);
        when(questionRepository.findByTopicId(1L)).thenReturn(questions);

        // When
        List<Question> found = questionService.findByTopic(1L);

        // Then
        assertThat(found).hasSize(1);
        assertThat(found.get(0).getTopic().getName()).isEqualTo("Java Grundlagen");
        verify(questionRepository).findByTopicId(1L);
    }

    @Test
    @DisplayName("Should get random questions")
    void shouldGetRandomQuestions() {
        // Given
        int count = 5;
        Page<Question> page = new PageImpl<>(Arrays.asList(testQuestion));
        when(questionRepository.findRandomQuestions(any(Pageable.class)))
            .thenReturn(page);

        // When
        List<Question> randomQuestions = questionService.getRandomQuestions(count);

        // Then
        assertThat(randomQuestions).hasSize(1);
        verify(questionRepository).findRandomQuestions(any(Pageable.class));
    }

    @Test
    @DisplayName("Should update question successfully")
    void shouldUpdateQuestionSuccessfully() {
        // Given
        Question updatedQuestion = Question.builder()
            .id(1L)
            .questionText("Updated question?")
            .questionType(Question.QuestionType.TRUE_FALSE)
            .difficultyLevel(3)
            .build();

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(updatedQuestion);

        // When
        Question result = questionService.updateQuestion(updatedQuestion);

        // Then
        assertThat(result).isNotNull();
        verify(questionRepository).findById(1L);
        verify(questionRepository).save(any(Question.class));
    }

    @Test
    @DisplayName("Should soft delete question")
    void shouldSoftDeleteQuestion() {
        // Given
        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(questionRepository.save(any(Question.class))).thenReturn(testQuestion);

        // When
        questionService.deleteQuestion(1L);

        // Then
        assertThat(testQuestion.getActive()).isFalse();
        verify(questionRepository).findById(1L);
        verify(questionRepository).save(testQuestion);
    }

    @Test
    @DisplayName("Should add answer to question")
    void shouldAddAnswerToQuestion() {
        // Given
        Answer newAnswer = Answer.builder()
            .answerText("New answer")
            .isCorrect(false)
            .build();

        when(questionRepository.findById(1L)).thenReturn(Optional.of(testQuestion));
        when(answerRepository.save(any(Answer.class))).thenReturn(newAnswer);

        // When
        Answer added = questionService.addAnswer(1L, newAnswer);

        // Then
        assertThat(added).isNotNull();
        assertThat(added.getQuestion()).isEqualTo(testQuestion);
        verify(answerRepository).save(any(Answer.class));
    }

    @Test
    @DisplayName("Should find questions for review")
    void shouldFindQuestionsForReview() {
        // Given
        Long userId = 1L;
        List<Question> reviewQuestions = Arrays.asList(testQuestion);
        when(questionRepository.findQuestionsForReview(userId))
            .thenReturn(reviewQuestions);

        // When
        List<Question> found = questionService.getQuestionsForReview(userId);

        // Then
        assertThat(found).hasSize(1);
        verify(questionRepository).findQuestionsForReview(userId);
    }
}