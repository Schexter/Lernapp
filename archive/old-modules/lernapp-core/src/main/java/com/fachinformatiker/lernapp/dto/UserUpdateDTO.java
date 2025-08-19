package com.fachinformatiker.lernapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * User Update DTO
 * Für Benutzer-Updates
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserUpdateDTO {
    
    private String firstName;
    private String lastName;
    
    @Email(message = "Email must be valid")
    private String email;
    
    private String bio;
    private String avatarUrl;
    private String learningStyle;
    private Boolean active;
}
