package com.fachinformatiker.lernapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Change Password DTO
 * Für Passwortänderung
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ChangePasswordDTO {
    
    @NotBlank(message = "Current password is required")
    private String currentPassword;
    
    @NotBlank(message = "New password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "Password must contain at least one uppercase letter, one lowercase letter and one number")
    private String newPassword;
    
    @NotBlank(message = "Password confirmation is required")
    private String newPasswordConfirmation;
    
    @AssertTrue(message = "New passwords must match")
    public boolean isPasswordsMatch() {
        return newPassword != null && newPassword.equals(newPasswordConfirmation);
    }
    
    @AssertTrue(message = "New password must be different from current password")
    public boolean isPasswordChanged() {
        return !newPassword.equals(currentPassword);
    }
}
