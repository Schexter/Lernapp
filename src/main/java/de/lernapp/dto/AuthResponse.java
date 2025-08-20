package de.lernapp.dto;

import lombok.Builder;
import lombok.Data;
import java.util.List;

/**
 * DTO für Authentication Response
 */
@Data
@Builder
public class AuthResponse {
    
    private String token;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private List<String> roles;
    private Long userId;
    private Integer experiencePoints;
    private Integer level;
    private Integer totalQuestionsAnswered;
    private Integer correctAnswers;
    private Integer currentStreak;
    private Integer bestStreak;
    private String lastLogin;
    private String message;
}