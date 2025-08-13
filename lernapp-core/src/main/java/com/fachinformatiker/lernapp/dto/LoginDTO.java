package com.fachinformatiker.lernapp.dto;

import lombok.*;
import jakarta.validation.constraints.*;

/**
 * Login DTO
 * Für Benutzeranmeldung
 * 
 * @author Hans Hahn
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {
    
    @NotBlank(message = "Username or email is required")
    private String username; // Can be username or email
    
    @NotBlank(message = "Password is required")
    private String password;
    
    private Boolean rememberMe;
    
    // Optional: For two-factor authentication
    private String totpCode;
    
    // Optional: Device information for security
    private String deviceId;
    private String deviceName;
    private String ipAddress;
    private String userAgent;
}
