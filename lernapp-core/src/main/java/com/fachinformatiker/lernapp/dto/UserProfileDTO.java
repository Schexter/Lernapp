package com.fachinformatiker.lernapp.dto;

import lombok.*;

/**
 * User Profile DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private Long userId;
    private String username;
    private String email;
    private String firstName;
    private String lastName;
    private String bio;
    private String avatarUrl;
    private String learningStyle;
    private Integer learningStreak;
    private Integer totalPoints;
    private Integer currentLevel;
    private Boolean emailVerified;
}
