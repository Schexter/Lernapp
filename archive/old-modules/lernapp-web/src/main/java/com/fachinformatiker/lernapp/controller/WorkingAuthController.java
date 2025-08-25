package com.fachinformatiker.lernapp.controller;

import com.fachinformatiker.lernapp.dto.LoginDTO;
import com.fachinformatiker.lernapp.dto.RegistrationDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Funktionsfähiger Auth Controller ohne komplexe Dependencies
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
//@RestController  // DEAKTIVIERT - Verwende UltraSimpleAuthController
@RequestMapping("/api/auth-old")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class WorkingAuthController {

    private final AuthenticationManager authenticationManager;

    @PostMapping("/login")
    public ResponseEntity<?> authenticateUser(@RequestBody LoginDTO loginDto) {
        try {
            log.info("Login attempt for user: {}", loginDto.getUsername());

            // Authentifizierung über Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginDto.getUsername(),
                            loginDto.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);
            
            // Einfache Token-Generierung (für Development)
            String token = "dev-token-" + System.currentTimeMillis();
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400L);
            response.put("user", createUserResponse(authentication));

            log.info("Login successful for user: {}", loginDto.getUsername());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("Login failed for user: {}", loginDto.getUsername(), e);
            
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid username or password");
            error.put("message", "Authentication failed");
            
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody RegistrationDTO registrationDto) {
        try {
            log.info("Registration attempt for username: {}", registrationDto.getUsername());

            // Einfache Validierung
            if ("admin".equals(registrationDto.getUsername()) || 
                "student".equals(registrationDto.getUsername()) ||
                "demo".equals(registrationDto.getUsername()) ||
                "teacher".equals(registrationDto.getUsername())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Username already exists");
                return ResponseEntity.badRequest().body(error);
            }

            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", Map.of(
                "id", System.currentTimeMillis(),
                "username", registrationDto.getUsername(),
                "email", registrationDto.getEmail(),
                "firstName", registrationDto.getFirstName(),
                "lastName", registrationDto.getLastName(),
                "role", "STUDENT"
            ));
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Registration failed", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User logged out successfully");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/demo-login")
    public ResponseEntity<?> demoLogin() {
        try {
            log.info("Demo login requested");
            
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken("demo", "demo123")
            );
            
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = "demo-token-" + System.currentTimeMillis();
            
            Map<String, Object> response = new HashMap<>();
            response.put("token", token);
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400L);
            response.put("user", createUserResponse(authentication));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Demo login failed", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Demo login failed");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    private Map<String, Object> createUserResponse(Authentication authentication) {
        String username = authentication.getName();
        
        Map<String, Object> user = new HashMap<>();
        user.put("id", (long) username.hashCode());
        user.put("username", username);
        user.put("email", username + "@lernapp.de");
        user.put("firstName", "Test");
        user.put("lastName", "User");
        user.put("role", determineRole(username));
        user.put("authorities", authentication.getAuthorities());
        
        return user;
    }
    
    private String determineRole(String username) {
        return switch (username) {
            case "admin" -> "ADMIN";
            case "teacher" -> "TEACHER";
            default -> "STUDENT";
        };
    }
}