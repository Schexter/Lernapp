package com.fachinformatiker.lernapp.dto;

import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailedProgressDTO {
    private Long userId;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double overallProgress;
    private Integer totalQuestions;
    private Integer answeredQuestions;
    private Integer correctAnswers;
    private List<TopicProgressDTO> topicProgress;
}
