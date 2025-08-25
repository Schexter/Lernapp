package com.fachinformatiker.lernapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * User Registration DTO
 * Für neue Benutzerregistrierung
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegistrationDTO {
    
    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 50)
    private String username;
    
    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;
    
    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100)
    private String password;
    
    @NotBlank(message = "Password confirmation is required")
    private String passwordConfirmation;
    
    private String firstName;
    private String lastName;
    
    @AssertTrue(message = "Terms must be accepted")
    private Boolean acceptTerms;
}
