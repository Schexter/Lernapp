package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyActivityDTO {
    private Long userId;
    private List<ActivityEntry> activities;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ActivityEntry {
        private String date;
        private Integer questionsAnswered;
        private Integer correctAnswers;
        private Integer minutesSpent;
    }
}
