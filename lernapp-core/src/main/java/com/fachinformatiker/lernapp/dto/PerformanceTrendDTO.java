package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerformanceTrendDTO {
    private Long userId;
    private List<TrendPoint> trendData;
    private Double averageAccuracy;
    private String trend; // "improving", "stable", "declining"
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendPoint {
        private String date;
        private Double accuracy;
        private Integer questionsAnswered;
    }
}
