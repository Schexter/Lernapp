package com.fachinformatiker.lernapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Password Reset DTO
 * Für Passwort-Zurücksetzen-Funktion
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordResetDTO {
    
    @NotBlank(message = "Reset token is required")
    private String token;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    private String newPasswordConfirmation;
    
    @AssertTrue(message = "Passwords must match")
    public boolean isPasswordsMatch() {
        return newPassword != null && newPassword.equals(newPasswordConfirmation);
    }
}
