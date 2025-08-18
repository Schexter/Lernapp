package com.fachinformatiker.lernapp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO für Login-Anfragen
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Data
public class LoginDTO {
    
    @NotBlank(message = "Username ist erforderlich")
    private String username;
    
    @NotBlank(message = "Password ist erforderlich")
    private String password;
    
    private boolean rememberMe = false;
}