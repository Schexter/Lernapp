package com.fachinformatiker.lernapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Password Change DTO
 * Für Passwortänderung
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PasswordChangeDTO {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100)
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    private String newPasswordConfirmation;
}
