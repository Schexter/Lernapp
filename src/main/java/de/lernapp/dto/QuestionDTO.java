package de.lernapp.dto;

import de.lernapp.model.Question;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuestionDTO {
    private Long id;
    private String questionText;
    private String optionA;
    private String optionB;
    private String optionC;
    private String optionD;
    private String category;
    private int difficulty;
    private int points;
    
    // Correct answer and explanation are not included initially
    // They will be sent after the user answers
    
    public static QuestionDTO fromQuestion(Question question) {
        return QuestionDTO.builder()
                .id(question.getId())
                .questionText(question.getQuestionText())
                .optionA(question.getOptionA())
                .optionB(question.getOptionB())
                .optionC(question.getOptionC())
                .optionD(question.getOptionD())
                .category(question.getCategory())
                .difficulty(question.getDifficulty())
                .points(question.getPoints())
                .build();
    }
}