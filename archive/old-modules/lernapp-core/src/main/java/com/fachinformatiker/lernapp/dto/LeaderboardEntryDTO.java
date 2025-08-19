package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.util.List;

/**
 * Leaderboard Entry DTO
 * Einzelner Leaderboard-Eintrag
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardEntryDTO {
    private Integer rank;
    private Long userId;
    private String username;
    private String avatarUrl;
    private Integer points;
    private Double accuracy;
    private Integer questionsAnswered;
    private Integer learningStreak;
    private List<String> badges;
}
