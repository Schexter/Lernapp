package de.lernapp.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.AssertTrue;

/**
 * DTO für Registrierungs-Anfragen
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {
    
    // Persönliche Informationen
    @NotBlank(message = "Vorname ist erforderlich")
    @Size(min = 2, max = 50, message = "Vorname muss zwischen 2 und 50 Zeichen lang sein")
    private String firstName;
    
    @NotBlank(message = "Nachname ist erforderlich")
    @Size(min = 2, max = 50, message = "Nachname muss zwischen 2 und 50 Zeichen lang sein")
    private String lastName;
    
    // Account-Informationen
    @NotBlank(message = "Benutzername ist erforderlich")
    @Size(min = 3, max = 20, message = "Benutzername muss zwischen 3 und 20 Zeichen lang sein")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", 
             message = "Benutzername darf nur Buchstaben, Zahlen, Punkte, Unterstriche und Bindestriche enthalten")
    private String username;
    
    @NotBlank(message = "E-Mail-Adresse ist erforderlich")
    @Email(message = "Bitte geben Sie eine gültige E-Mail-Adresse ein")
    @Size(max = 100, message = "E-Mail-Adresse darf maximal 100 Zeichen lang sein")
    private String email;
    
    // Passwort
    @NotBlank(message = "Passwort ist erforderlich")
    @Size(min = 8, message = "Passwort muss mindestens 8 Zeichen lang sein")
    private String password;
    
    @NotBlank(message = "Passwort-Bestätigung ist erforderlich")
    private String confirmPassword;
    
    // Berufliche Informationen (optional)
    private String ausbildungsrichtung; // FIAE, FISI, FIDA, FIDC, OTHER
    
    private String ausbildungsjahr; // 1, 2, 3, FINISHED
    
    @Size(max = 100, message = "Berufsschule darf maximal 100 Zeichen lang sein")
    private String berufsschule;
    
    // Zustimmungen
    @AssertTrue(message = "Sie müssen den Allgemeinen Geschäftsbedingungen zustimmen")
    private boolean agbAccepted;
    
    private boolean newsletter; // Optional
    
    // Custom Validation für Passwort-Übereinstimmung
    @AssertTrue(message = "Die Passwörter stimmen nicht überein")
    public boolean isPasswordMatching() {
        return password != null && password.equals(confirmPassword);
    }
    
    // Hilfsmethoden für Template
    public String getAusbildungsrichtungDisplay() {
        if (ausbildungsrichtung == null) return "";
        
        switch (ausbildungsrichtung) {
            case "FIAE": return "Fachinformatiker/in Anwendungsentwicklung";
            case "FISI": return "Fachinformatiker/in Systemintegration";
            case "FIDA": return "Fachinformatiker/in Daten- und Prozessanalyse";
            case "FIDC": return "Fachinformatiker/in Digitale Vernetzung";
            case "OTHER": return "Sonstiges";
            default: return ausbildungsrichtung;
        }
    }
    
    public String getAusbildungsjahrDisplay() {
        if (ausbildungsjahr == null) return "";
        
        switch (ausbildungsjahr) {
            case "1": return "1. Lehrjahr";
            case "2": return "2. Lehrjahr";
            case "3": return "3. Lehrjahr";
            case "FINISHED": return "Ausbildung beendet";
            default: return ausbildungsjahr;
        }
    }
}
