package com.fachinformatiker.lernapp.dto;

import lombok.*;

/**
 * User Preferences DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserPreferencesDTO {
    private Long userId;
    private String theme; // light, dark, auto
    private String language; // de, en
    private Boolean emailNotifications;
    private Boolean pushNotifications;
    private Integer dailyGoalQuestions;
    private Integer dailyGoalMinutes;
    private String preferredDifficulty;
    private Boolean showHints;
    private Boolean autoAdvance;
    private Integer fontSize;
    private Boolean soundEffects;
}
