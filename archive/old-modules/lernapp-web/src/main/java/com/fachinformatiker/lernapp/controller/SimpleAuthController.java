package com.fachinformatiker.lernapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Vereinfachter Auth Controller für Development
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
//@RestController // DEAKTIVIERT - Ersetzt durch echten AuthController
@RequestMapping("/api/auth")
@Slf4j
@CrossOrigin(origins = "*")
public class SimpleAuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        try {
            String username = credentials.get("username");
            String password = credentials.get("password");
            
            log.info("Simple login attempt for user: {}", username);
            
            // Einfache Validierung
            if (isValidUser(username, password)) {
                Map<String, Object> response = new HashMap<>();
                response.put("token", "dev-token-" + System.currentTimeMillis());
                response.put("tokenType", "Bearer");
                response.put("expiresIn", 86400L);
                response.put("user", createUserResponse(username));
                
                log.info("Login successful for user: {}", username);
                return ResponseEntity.ok(response);
            } else {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid credentials");
                return ResponseEntity.status(401).body(error);
            }
            
        } catch (Exception e) {
            log.error("Login error: ", e);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Login failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        try {
            String username = userData.get("username");
            String email = userData.get("email");
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User registered successfully");
            response.put("user", createUserResponse(username));
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Registration failed: " + e.getMessage());
            return ResponseEntity.status(500).body(error);
        }
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
    
    private boolean isValidUser(String username, String password) {
        // Einfache Test-User Validierung
        return ("student".equals(username) && "student123".equals(password)) ||
               ("demo".equals(username) && "demo123".equals(password)) ||
               ("admin".equals(username) && "admin123".equals(password)) ||
               ("teacher".equals(username) && "teacher123".equals(password));
    }
    
    private Map<String, Object> createUserResponse(String username) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", 1L);
        user.put("username", username);
        user.put("email", username + "@example.com");
        user.put("firstName", "Test");
        user.put("lastName", "User");
        user.put("role", determineRole(username));
        return user;
    }
    
    private String determineRole(String username) {
        switch (username) {
            case "admin": return "ADMIN";
            case "teacher": return "TEACHER";
            case "student": return "STUDENT";
            default: return "STUDENT";
        }
    }
}