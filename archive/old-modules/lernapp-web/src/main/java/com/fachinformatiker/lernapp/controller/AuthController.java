package com.fachinformatiker.lernapp.controller;

import com.fachinformatiker.lernapp.dto.*;
import com.fachinformatiker.lernapp.security.jwt.JwtTokenProvider;
import com.fachinformatiker.lernapp.facade.UserServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
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

//@RestController // DEAKTIVIERT - Fehlende Dependencies (JwtTokenProvider, UserServiceFacade)
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Authentication", description = "Authentication management endpoints")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final UserServiceFacade userService;

    @PostMapping("/login")
    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDto) {
        log.info("Login attempt for user: {}", loginDto.getUsername());

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        loginDto.getUsername(),
                        loginDto.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication.getName());
        
        // Get user details for response
        UserDTO user = userService.getUserByUsername(authentication.getName()).orElse(null);

        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("refreshToken", refreshToken);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 86400L);
        response.put("user", user);

        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    @Operation(summary = "Register new user", description = "Create a new user account")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationDTO registrationDto) {
        log.info("Registration attempt for username: {}", registrationDto.getUsername());

        if (userService.existsByUsername(registrationDto.getUsername())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Username is already taken!");
            return ResponseEntity.badRequest().body(error);
        }

        if (userService.existsByEmail(registrationDto.getEmail())) {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Email is already in use!");
            return ResponseEntity.badRequest().body(error);
        }

        UserDTO user = userService.registerUser(registrationDto);
        
        Map<String, Object> response = new HashMap<>();
        response.put("message", "User registered successfully");
        response.put("user", user);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh token", description = "Get new access token using refresh token")
    public ResponseEntity<?> refreshToken(@RequestParam String refreshToken) {
        if (tokenProvider.validateToken(refreshToken)) {
            String username = tokenProvider.getUsernameFromToken(refreshToken);
            
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String newAccessToken = tokenProvider.generateToken(authentication);
            
            Map<String, String> response = new HashMap<>();
            response.put("accessToken", newAccessToken);
            response.put("tokenType", "Bearer");
            
            return ResponseEntity.ok(response);
        } else {
            Map<String, String> error = new HashMap<>();
            error.put("error", "Invalid refresh token");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
        }
    }

    @PostMapping("/demo-login")
    @Operation(summary = "Demo login", description = "Login with demo account for testing")
    public ResponseEntity<?> demoLogin() {
        log.info("Demo login requested");
        
        // Use demo credentials
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken("demo", "demo123")
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.generateToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken("demo");
        
        // Get user details for response
        UserDTO user = userService.getUserByUsername("demo").orElse(null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("token", jwt);
        response.put("refreshToken", refreshToken);
        response.put("tokenType", "Bearer");
        response.put("expiresIn", 86400L);
        response.put("user", user);
        
        return ResponseEntity.ok(response);
    }
    
    @PostMapping("/logout")
    @Operation(summary = "Logout user", description = "Logout current user")
    public ResponseEntity<?> logoutUser() {
        SecurityContextHolder.clearContext();
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "User logged out successfully");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset-request")
    @Operation(summary = "Request password reset", description = "Send password reset email")
    public ResponseEntity<?> requestPasswordReset(@RequestParam String email) {
        log.info("Password reset requested for email: {}", email);
        
        userService.initiatePasswordReset(email);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password reset email sent if account exists");
        
        return ResponseEntity.ok(response);
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Reset password", description = "Reset password with token")
    public ResponseEntity<?> resetPassword(@Valid @RequestBody PasswordResetDTO passwordResetDto) {
        userService.resetPassword(passwordResetDto);
        
        Map<String, String> response = new HashMap<>();
        response.put("message", "Password has been reset successfully");
        
        return ResponseEntity.ok(response);
    }
}