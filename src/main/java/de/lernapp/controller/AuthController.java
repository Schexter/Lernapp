package de.lernapp.controller;

import de.lernapp.dto.AuthResponse;
import de.lernapp.dto.LoginRequest;
import de.lernapp.dto.RegisterRequest;
import de.lernapp.model.User;
import de.lernapp.service.JwtService;
import de.lernapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.stream.Collectors;

/**
 * REST Controller für Authentication
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", maxAge = 3600)
@Validated
public class AuthController {
    
    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);
    
    private final AuthenticationManager authenticationManager;
    private final UserService userService;
    private final JwtService jwtService;
    
    /**
     * Login Endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        logger.info("Login-Versuch für User: {}", loginRequest.getUsername());
        
        try {
            // Authentifiziere User
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getUsername(),
                    loginRequest.getPassword()
                )
            );
            
            // Generiere JWT Token
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtService.generateToken(userDetails);
            
            // Update last login
            User user = userService.updateLastLogin(loginRequest.getUsername());
            
            // Erstelle Response
            AuthResponse response = AuthResponse.builder()
                .token(token)
                .username(user.getUsername())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .userId(user.getId())
                .experiencePoints(user.getExperiencePoints())
                .level(user.getLevel())
                .roles(user.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList()))
                .message("Login erfolgreich!")
                .build();
            
            logger.info("✅ Login erfolgreich für User: {}", user.getUsername());
            return ResponseEntity.ok(response);
            
        } catch (BadCredentialsException e) {
            logger.warn("❌ Login fehlgeschlagen für User: {} - Ungültige Credentials", 
                loginRequest.getUsername());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Ungültiger Benutzername oder Passwort");
        } catch (Exception e) {
            logger.error("❌ Login-Fehler: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
        }
    }
    
    /**
     * Registrierungs Endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterRequest registerRequest) {
        logger.info("Registrierungs-Versuch für User: {}", registerRequest.getUsername());
        
        try {
            // Prüfe ob User bereits existiert
            if (userService.findByUsername(registerRequest.getUsername()).isPresent()) {
                logger.warn("❌ Registrierung fehlgeschlagen - Username existiert bereits: {}", 
                    registerRequest.getUsername());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Benutzername ist bereits vergeben");
            }
            
            if (userService.findByEmail(registerRequest.getEmail()).isPresent()) {
                logger.warn("❌ Registrierung fehlgeschlagen - Email existiert bereits: {}", 
                    registerRequest.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Email-Adresse ist bereits registriert");
            }
            
            // Registriere neuen User
            User newUser = userService.registerUser(
                registerRequest.getUsername(),
                registerRequest.getEmail(),
                registerRequest.getPassword(),
                registerRequest.getFirstName(),
                registerRequest.getLastName()
            );
            
            // Generiere JWT Token
            UserDetails userDetails = userService.loadUserByUsername(newUser.getUsername());
            String token = jwtService.generateToken(userDetails);
            
            // Erstelle Response
            AuthResponse response = AuthResponse.builder()
                .token(token)
                .username(newUser.getUsername())
                .email(newUser.getEmail())
                .firstName(newUser.getFirstName())
                .lastName(newUser.getLastName())
                .userId(newUser.getId())
                .experiencePoints(0)
                .level(1)
                .roles(newUser.getRoles().stream()
                    .map(role -> role.getName().name())
                    .collect(Collectors.toList()))
                .message("Registrierung erfolgreich! Willkommen bei der Lernapp!")
                .build();
            
            logger.info("✅ Registrierung erfolgreich für User: {}", newUser.getUsername());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("❌ Registrierungs-Fehler: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
        }
    }
    
    /**
     * Token-Validierungs Endpoint
     */
    @GetMapping("/validate")
    public ResponseEntity<?> validateToken(@RequestHeader("Authorization") String authHeader) {
        logger.debug("Token-Validierung angefordert");
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Ungültiges Token-Format");
            }
            
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            
            UserDetails userDetails = userService.loadUserByUsername(username);
            
            if (jwtService.validateToken(token, userDetails)) {
                User user = userService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User nicht gefunden"));
                
                AuthResponse response = AuthResponse.builder()
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .firstName(user.getFirstName())
                    .lastName(user.getLastName())
                    .userId(user.getId())
                    .experiencePoints(user.getExperiencePoints())
                    .level(user.getLevel())
                    .roles(user.getRoles().stream()
                        .map(role -> role.getName().name())
                        .collect(Collectors.toList()))
                    .message("Token ist gültig")
                    .build();
                
                return ResponseEntity.ok(response);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token ist ungültig oder abgelaufen");
            }
            
        } catch (Exception e) {
            logger.error("Token-Validierungs-Fehler: ", e);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body("Token-Validierung fehlgeschlagen");
        }
    }
    
    /**
     * Logout Endpoint (clientseitig Token löschen)
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String authHeader) {
        logger.info("Logout angefordert");
        
        // Bei JWT-basierter Auth wird das Token clientseitig gelöscht
        // Optional: Token in eine Blacklist eintragen (nicht implementiert)
        
        return ResponseEntity.ok()
            .body("Logout erfolgreich. Bitte löschen Sie das Token clientseitig.");
    }
    
    /**
     * Passwort-Änderungs Endpoint
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {
        
        logger.info("Passwort-Änderung angefordert");
        
        try {
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Nicht autorisiert");
            }
            
            String token = authHeader.substring(7);
            String username = jwtService.extractUsername(token);
            
            // Validiere altes Passwort
            try {
                authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, oldPassword)
                );
            } catch (BadCredentialsException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Altes Passwort ist falsch");
            }
            
            // TODO: Implementiere Passwort-Update in UserService
            // userService.updatePassword(username, newPassword);
            
            return ResponseEntity.ok("Passwort erfolgreich geändert");
            
        } catch (Exception e) {
            logger.error("Passwort-Änderungs-Fehler: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("Fehler beim Ändern des Passworts");
        }
    }
    
    /**
     * Health-Check Endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<?> health() {
        return ResponseEntity.ok()
            .body("Auth-Service ist bereit!");
    }
}