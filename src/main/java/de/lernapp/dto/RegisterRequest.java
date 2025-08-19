package de.lernapp.dto;

import lombok.Data;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO für Registrierungs-Anfragen
 */
@Data
public class RegisterRequest {
    
    @NotBlank(message = "Username ist erforderlich")
    @Size(min = 3, max = 20, message = "Username muss zwischen 3 und 20 Zeichen lang sein")
    private String username;
    
    @NotBlank(message = "Email ist erforderlich")
    @Email(message = "Bitte geben Sie eine gültige Email-Adresse ein")
    private String email;
    
    @NotBlank(message = "Passwort ist erforderlich")
    @Size(min = 6, message = "Passwort muss mindestens 6 Zeichen lang sein")
    private String password;
    
    @NotBlank(message = "Vorname ist erforderlich")
    private String firstName;
    
    @NotBlank(message = "Nachname ist erforderlich")
    private String lastName;
}