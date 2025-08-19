package com.fachinformatiker.lernapp.controller;

import com.fachinformatiker.lernapp.model.Question;
import com.fachinformatiker.lernapp.dto.*;
import com.fachinformatiker.lernapp.facade.QuestionServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * REST Controller für Question Management
 * Verwaltet Fragen, Antworten und Topics
 * 
 * @author Hans Hahn
 */
@RestController
@RequestMapping("/api/v1/questions")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Question Management", description = "Endpoints für Fragenverwaltung")
public class QuestionController {

    private final QuestionServiceFacade questionService;

    @Operation(summary = "Erstelle neue Frage")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Frage erfolgreich erstellt"),
        @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten")
    })
    @PostMapping
    public ResponseEntity<QuestionDTO> createQuestion(@Valid @RequestBody QuestionDTO questionDTO) {
        log.info("Creating new question for topic: {}", questionDTO.getTopicId());
        QuestionDTO created = questionService.createQuestion(questionDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @Operation(summary = "Hole Frage per ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Frage gefunden"),
        @ApiResponse(responseCode = "404", description = "Frage nicht gefunden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<QuestionDTO> getQuestionById(@PathVariable Long id) {
        log.debug("Fetching question with id: {}", id);
        return questionService.getQuestionById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update Frage")
    @PutMapping("/{id}")
    public ResponseEntity<QuestionDTO> updateQuestion(
            @PathVariable Long id,
            @Valid @RequestBody QuestionDTO questionDTO) {
        log.info("Updating question: {}", id);
        try {
            QuestionDTO updated = questionService.updateQuestion(id, questionDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            log.error("Update failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Lösche Frage")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestion(@PathVariable Long id) {
        log.info("Deleting question: {}", id);
        boolean deleted = questionService.deleteQuestion(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Hole alle Fragen (paginiert)")
    @GetMapping
    public ResponseEntity<Page<QuestionDTO>> getAllQuestions(
            @Parameter(description = "Pagination parameters") Pageable pageable,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Integer difficulty,
            @RequestParam(required = false) Question.QuestionType type) {
        log.debug("Fetching questions with filters - topic: {}, difficulty: {}, type: {}", 
                 topicId, difficulty, type);
        Page<QuestionDTO> questions = questionService.getQuestions(pageable, topicId, difficulty, type);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Hole Fragen nach Topic")
    @GetMapping("/topic/{topicId}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByTopic(@PathVariable Long topicId) {
        log.debug("Fetching questions for topic: {}", topicId);
        List<QuestionDTO> questions = questionService.getQuestionsByTopic(topicId);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Hole Fragen nach Schwierigkeit")
    @GetMapping("/difficulty/{level}")
    public ResponseEntity<List<QuestionDTO>> getQuestionsByDifficulty(
            @PathVariable Integer level) {
        log.debug("Fetching questions with difficulty: {}", level);
        List<QuestionDTO> questions = questionService.getQuestionsByDifficulty(level);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Suche Fragen")
    @GetMapping("/search")
    public ResponseEntity<List<QuestionDTO>> searchQuestions(
            @RequestParam String query,
            @RequestParam(required = false) Long topicId) {
        log.debug("Searching questions with query: {}", query);
        List<QuestionDTO> questions = questionService.searchQuestions(query, topicId);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Hole zufällige Fragen")
    @GetMapping("/random")
    public ResponseEntity<List<QuestionDTO>> getRandomQuestions(
            @RequestParam(defaultValue = "10") int count,
            @RequestParam(required = false) Long topicId,
            @RequestParam(required = false) Integer difficulty) {
        log.debug("Fetching {} random questions", count);
        List<QuestionDTO> questions = questionService.getRandomQuestions(count, topicId, difficulty);
        return ResponseEntity.ok(questions);
    }

    @Operation(summary = "Validiere Antwort")
    @PostMapping("/{questionId}/validate")
    public ResponseEntity<AnswerValidationDTO> validateAnswer(
            @PathVariable Long questionId,
            @RequestBody AnswerSubmissionDTO submission) {
        log.debug("Validating answer for question: {}", questionId);
        AnswerValidationDTO validation = questionService.validateAnswer(questionId, submission);
        return ResponseEntity.ok(validation);
    }

    @Operation(summary = "Importiere Fragen aus CSV/JSON")
    @PostMapping("/import")
    public ResponseEntity<ImportResultDTO> importQuestions(
            @RequestParam("file") MultipartFile file,
            @RequestParam(required = false) Long topicId) {
        log.info("Importing questions from file: {}", file.getOriginalFilename());
        try {
            ImportResultDTO result = questionService.importQuestions(file, topicId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Import failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Exportiere Fragen als CSV/JSON")
    @GetMapping("/export")
    public ResponseEntity<byte[]> exportQuestions(
            @RequestParam(required = false) Long topicId,
            @RequestParam(defaultValue = "json") String format) {
        log.info("Exporting questions in format: {}", format);
        try {
            byte[] data = questionService.exportQuestions(topicId, format);
            return ResponseEntity.ok()
                    .header("Content-Disposition", "attachment; filename=questions." + format)
                    .body(data);
        } catch (Exception e) {
            log.error("Export failed: {}", e.getMessage());
            return ResponseEntity.internalServerError().build();
        }
    }

    @Operation(summary = "Hole Fragen-Statistiken")
    @GetMapping("/{id}/statistics")
    public ResponseEntity<QuestionStatisticsDTO> getQuestionStatistics(@PathVariable Long id) {
        log.debug("Fetching statistics for question: {}", id);
        return questionService.getQuestionStatistics(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Füge Antwort zu Frage hinzu")
    @PostMapping("/{questionId}/answers")
    public ResponseEntity<AnswerDTO> addAnswer(
            @PathVariable Long questionId,
            @Valid @RequestBody AnswerDTO answerDTO) {
        log.info("Adding answer to question: {}", questionId);
        try {
            AnswerDTO created = questionService.addAnswer(questionId, answerDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Update Antwort")
    @PutMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<AnswerDTO> updateAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId,
            @Valid @RequestBody AnswerDTO answerDTO) {
        log.info("Updating answer {} for question {}", answerId, questionId);
        try {
            AnswerDTO updated = questionService.updateAnswer(questionId, answerId, answerDTO);
            return ResponseEntity.ok(updated);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Lösche Antwort")
    @DeleteMapping("/{questionId}/answers/{answerId}")
    public ResponseEntity<Void> deleteAnswer(
            @PathVariable Long questionId,
            @PathVariable Long answerId) {
        log.info("Deleting answer {} from question {}", answerId, questionId);
        boolean deleted = questionService.deleteAnswer(questionId, answerId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
