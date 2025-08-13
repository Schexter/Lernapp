package com.fachinformatiker.lernapp.controller;

import com.fachinformatiker.lernapp.dto.*;
import com.fachinformatiker.lernapp.facade.UserServiceFacade;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller für User Management
 * Handles user registration, profile management, and user queries
 * 
 * @author Hans Hahn
 */
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "Endpoints für Benutzerverwaltung")
public class UserController {

    private final UserServiceFacade userService;

    @Operation(summary = "Registriere neuen Benutzer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Benutzer erfolgreich erstellt"),
        @ApiResponse(responseCode = "400", description = "Ungültige Eingabedaten"),
        @ApiResponse(responseCode = "409", description = "Benutzer existiert bereits")
    })
    @PostMapping("/register")
    public ResponseEntity<UserDTO> registerUser(@Valid @RequestBody UserRegistrationDTO registrationDTO) {
        log.info("Registering new user: {}", registrationDTO.getUsername());
        try {
            UserDTO createdUser = userService.registerUser(registrationDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            log.error("Registration failed: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @Operation(summary = "Hole Benutzer per ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Benutzer gefunden"),
        @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden")
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(
            @Parameter(description = "User ID") @PathVariable Long id) {
        log.debug("Fetching user with id: {}", id);
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Hole Benutzer per Username")
    @GetMapping("/username/{username}")
    public ResponseEntity<UserDTO> getUserByUsername(
            @Parameter(description = "Username") @PathVariable String username) {
        log.debug("Fetching user with username: {}", username);
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Update Benutzerprofil")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Profil erfolgreich aktualisiert"),
        @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden"),
        @ApiResponse(responseCode = "400", description = "Ungültige Daten")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUserProfile(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO updateDTO) {
        log.info("Updating profile for user id: {}", id);
        try {
            UserDTO updatedUser = userService.updateUser(id, updateDTO);
            return ResponseEntity.ok(updatedUser);
        } catch (IllegalArgumentException e) {
            log.error("Update failed: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    @Operation(summary = "Lösche Benutzer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Benutzer erfolgreich gelöscht"),
        @ApiResponse(responseCode = "404", description = "Benutzer nicht gefunden")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        log.info("Deleting user with id: {}", id);
        boolean deleted = userService.deleteUser(id);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @Operation(summary = "Hole alle Benutzer (paginiert)")
    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(
            @Parameter(description = "Pagination parameters") Pageable pageable) {
        log.debug("Fetching users page: {}", pageable.getPageNumber());
        Page<UserDTO> users = userService.getAllUsers(pageable);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Suche Benutzer")
    @GetMapping("/search")
    public ResponseEntity<List<UserDTO>> searchUsers(
            @RequestParam(required = false) String query,
            @RequestParam(required = false) String role) {
        log.debug("Searching users with query: {} and role: {}", query, role);
        List<UserDTO> users = userService.searchUsers(query, role);
        return ResponseEntity.ok(users);
    }

    @Operation(summary = "Hole Lernstatistiken eines Benutzers")
    @GetMapping("/{id}/statistics")
    public ResponseEntity<UserStatisticsDTO> getUserStatistics(@PathVariable Long id) {
        log.debug("Fetching statistics for user: {}", id);
        return userService.getUserStatistics(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Ändere Benutzerpasswort")
    @PostMapping("/{id}/change-password")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeDTO passwordChangeDTO) {
        log.info("Changing password for user: {}", id);
        try {
            userService.changePassword(id, passwordChangeDTO);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("Password change failed: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "Aktiviere/Deaktiviere Benutzer")
    @PatchMapping("/{id}/status")
    public ResponseEntity<UserDTO> toggleUserStatus(
            @PathVariable Long id,
            @RequestParam boolean active) {
        log.info("Setting user {} status to: {}", id, active);
        try {
            UserDTO user = userService.setUserStatus(id, active);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
