package de.lernapp.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO für Login-Anfragen
 */
@Data
public class LoginRequest {
    
    @NotBlank(message = "Username ist erforderlich")
    private String username;
    
    @NotBlank(message = "Passwort ist erforderlich")
    private String password;
}