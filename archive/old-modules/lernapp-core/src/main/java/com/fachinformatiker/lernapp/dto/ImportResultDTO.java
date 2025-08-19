package com.fachinformatiker.lernapp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Import Result DTO
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ImportResultDTO {
    private int totalQuestions;
    private int successfulImports;
    private int failedImports;
    private List<String> errors;
}
