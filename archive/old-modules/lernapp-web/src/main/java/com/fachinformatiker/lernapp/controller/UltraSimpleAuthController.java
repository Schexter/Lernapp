package com.fachinformatiker.lernapp.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * ULTRA-SIMPLE Auth Controller für Development
 * Keine Dependencies, keine Komplexität, funktioniert einfach!
 */
@RestController
@RequestMapping("/api/auth")
@Slf4j
@CrossOrigin(origins = "*")
public class UltraSimpleAuthController {

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credentials) {
        String username = credentials.get("username");
        String password = credentials.get("password");
        
        log.info("=== ULTRA SIMPLE LOGIN ===");
        log.info("Username: {}", username);
        log.info("Password: {}", password != null ? "***" : "null");
        
        // Super simple validation - accept these users
        boolean validLogin = false;
        if ("demo".equals(username) && "demo123".equals(password)) {
            validLogin = true;
        } else if ("student".equals(username) && "student123".equals(password)) {
            validLogin = true;
        } else if ("admin".equals(username) && "admin123".equals(password)) {
            validLogin = true;
        } else if ("teacher".equals(username) && "teacher123".equals(password)) {
            validLogin = true;
        }
        
        if (validLogin) {
            Map<String, Object> response = new HashMap<>();
            response.put("token", "simple-token-" + System.currentTimeMillis());
            response.put("tokenType", "Bearer");
            response.put("expiresIn", 86400L);
            
            // Create user object
            Map<String, Object> user = new HashMap<>();
            user.put("id", username.hashCode());
            user.put("username", username);
            user.put("email", username + "@lernapp.de");
            user.put("firstName", getFirstName(username));
            user.put("lastName", "User");
            user.put("role", getRole(username));
            
            response.put("user", user);
            
            log.info("Login successful for: {}", username);
            return ResponseEntity.ok(response);
        } else {
            log.warn("Login failed for: {}", username);
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid credentials");
            error.put("message", "Username or password incorrect");
            return ResponseEntity.status(401).body(error);
        }
    }
    
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody Map<String, String> userData) {
        log.info("Registration request: {}", userData.get("username"));
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Registration successful");
        response.put("user", Map.of(
            "id", System.currentTimeMillis(),
            "username", userData.get("username"),
            "email", userData.get("email")
        ));
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        log.info("Logout request");
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Logout successful");
        return ResponseEntity.ok(response);
    }
    
    @GetMapping("/test")
    public ResponseEntity<?> test() {
        log.info("Auth test endpoint called");
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "OK");
        response.put("message", "Auth endpoint is working");
        response.put("timestamp", String.valueOf(System.currentTimeMillis()));
        
        return ResponseEntity.ok(response);
    }
    
    private String getFirstName(String username) {
        switch (username) {
            case "demo": return "Demo";
            case "student": return "Student";
            case "teacher": return "Teacher";
            case "admin": return "Admin";
            default: return "User";
        }
    }
    
    private String getRole(String username) {
        switch (username) {
            case "admin": return "ADMIN";
            case "teacher": return "TEACHER";
            default: return "STUDENT";
        }
    }
}
