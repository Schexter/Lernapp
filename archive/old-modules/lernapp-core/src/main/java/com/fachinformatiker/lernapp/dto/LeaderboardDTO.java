package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LeaderboardDTO {
    private Long topicId;
    private String topicName;
    private List<LeaderboardEntry> entries;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LeaderboardEntry {
        private Integer rank;
        private Long userId;
        private String username;
        private Integer points;
        private Double accuracy;
    }
}
