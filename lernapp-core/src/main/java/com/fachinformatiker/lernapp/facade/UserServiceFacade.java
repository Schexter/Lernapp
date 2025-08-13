package com.fachinformatiker.lernapp.facade;

import com.fachinformatiker.lernapp.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Optional;

/**
 * User Service Facade Interface
 * 
 * @author Hans Hahn
 */
public interface UserServiceFacade {
    
    // User Management
    UserDTO registerUser(@Valid UserRegistrationDTO registration);
    Optional<UserDTO> getUserById(Long id);
    Optional<UserDTO> getUserByUsername(String username);
    UserDTO updateUser(Long id, @Valid UserUpdateDTO updateDTO);
    boolean deleteUser(Long id);
    
    // Authentication
    LoginResponseDTO login(@Valid LoginDTO loginDTO);
    void logout(String token);
    LoginResponseDTO refreshToken(String refreshToken);
    
    // Profile
    UserProfileDTO getUserProfile(Long userId);
    UserProfileDTO updateProfile(Long userId, @Valid UserProfileDTO profile);
    void changePassword(Long userId, @Valid PasswordChangeDTO passwordChange);
    
    // User Listing
    Page<UserDTO> getAllUsers(Pageable pageable);
    List<UserDTO> searchUsers(String query, String role);
    
    // Statistics
    Optional<UserStatisticsDTO> getUserStatistics(Long userId);
    
    // Preferences
    UserPreferencesDTO getUserPreferences(Long userId);
    UserPreferencesDTO updatePreferences(Long userId, @Valid UserPreferencesDTO preferences);
    
    // Avatar
    void updateAvatar(Long userId, String avatarUrl);
    
    // Email Verification
    void verifyEmail(String token);
    void resendVerificationEmail(Long userId);
    
    // Password Reset
    void requestPasswordReset(String email);
    void resetPassword(@Valid PasswordResetDTO resetDTO);
    
    // Status Management
    UserDTO setUserStatus(Long userId, boolean active);
}
