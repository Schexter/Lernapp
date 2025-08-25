package com.fachinformatiker.lernapp.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO für Benutzer-Registrierung
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Data
public class RegistrationDTO {
    
    @NotBlank(message = "Username ist erforderlich")
    @Size(min = 3, max = 50, message = "Username muss zwischen 3 und 50 Zeichen lang sein")
    private String username;
    
    @NotBlank(message = "Email ist erforderlich")
    @Email(message = "Gültige Email-Adresse erforderlich")
    private String email;
    
    @NotBlank(message = "Password ist erforderlich")
    @Size(min = 6, message = "Password muss mindestens 6 Zeichen lang sein")
    private String password;
    
    @NotBlank(message = "Vorname ist erforderlich")
    private String firstName;
    
    @NotBlank(message = "Nachname ist erforderlich")
    private String lastName;
    
    private String role = "STUDENT"; // Default Role
}