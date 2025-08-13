package com.fachinformatiker.lernapp.dto;

import lombok.*;
import org.springframework.http.ResponseEntity;

/**
 * Session Result DTO
 * Für Lernsession-Ergebnisse
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SessionResultDTO {
    private Long sessionId;
    private Long userId;
    private Long topicId;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    private Double accuracy;
    private Integer pointsEarned;
    private Long timeSpentSeconds;
    private Boolean completed;
    private String feedback;
    
    public ResponseEntity<SessionResultDTO> toResponseEntity() {
        return ResponseEntity.ok(this);
    }
}
