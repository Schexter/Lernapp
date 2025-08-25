package com.fachinformatiker.lernapp.dto;

import lombok.*;

/**
 * Streak DTO
 * Learning Streak Information
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StreakDTO {
    private Long userId;
    private Integer currentStreak;
    private Integer longestStreak;
    private String lastActivityDate;
    private Boolean activeToday;
    private Integer daysUntilNextMilestone;
    private String nextMilestone;
}
