package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.Role;
import com.fachinformatiker.lernapp.model.User;
import com.fachinformatiker.lernapp.repository.RoleRepository;
import com.fachinformatiker.lernapp.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.fachinformatiker.lernapp.util.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Cacheable(value = "users", key = "#id")
    public Optional<User> findById(Long id) {
        log.debug("Finding user by id: {}", id);
        return userRepository.findById(id);
    }
    
    @Cacheable(value = "users", key = "#username")
    public Optional<User> findByUsername(String username) {
        log.debug("Finding user by username: {}", username);
        return userRepository.findByUsernameWithRoles(username);
    }
    
    public Optional<User> findByEmail(String email) {
        log.debug("Finding user by email: {}", email);
        return userRepository.findByEmailWithRoles(email);
    }
    
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        log.debug("Finding user by username or email: {}", usernameOrEmail);
        return userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail);
    }
    
    public Page<User> findAll(Pageable pageable) {
        log.debug("Finding all users with pagination");
        return userRepository.findAll(pageable);
    }
    
    public Page<User> findAllActive(Pageable pageable) {
        log.debug("Finding all active users");
        return userRepository.findAllActive(pageable);
    }
    
    @CacheEvict(value = "users", allEntries = true)
    public User createUser(User user) {
        log.info("Creating new user: {}", user.getUsername());
        
        validateNewUser(user);
        
        // Encode password
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        
        // Set default values
        user.setActive(true);
        user.setEmailVerified(false);
        user.setLearningStreak(0);
        user.setTotalPoints(0);
        user.setCurrentLevel(1);
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        
        // Add default role
        Role userRole = roleRepository.findByName("ROLE_USER")
            .orElseThrow(() -> new RuntimeException("Default role not found"));
        user.getRoles().add(userRole);
        
        // Generate verification token
        user.setEmailVerificationToken(generateToken());
        
        User savedUser = userRepository.save(user);
        log.info("User created successfully: {}", savedUser.getUsername());
        
        return savedUser;
    }
    
    @CacheEvict(value = "users", key = "#user.username")
    public User updateUser(User user) {
        log.info("Updating user: {}", user.getUsername());
        
        User existingUser = userRepository.findById(user.getId())
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Update allowed fields
        existingUser.setFirstName(user.getFirstName());
        existingUser.setLastName(user.getLastName());
        existingUser.setAvatarUrl(user.getAvatarUrl());
        existingUser.setBio(user.getBio());
        existingUser.setLearningStyle(user.getLearningStyle());
        existingUser.setPreferences(user.getPreferences());
        existingUser.setUpdatedAt(LocalDateTime.now());
        
        return userRepository.save(existingUser);
    }
    
    @CacheEvict(value = "users", key = "#username")
    public void updatePassword(String username, String oldPassword, String newPassword) {
        log.info("Updating password for user: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new RuntimeException("Invalid old password");
        }
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Password updated successfully for user: {}", username);
    }
    
    public void updateLastLoginTime(Long userId) {
        log.debug("Updating last login time for user: {}", userId);
        userRepository.updateLastLoginTime(userId, LocalDateTime.now());
    }
    
    public void incrementLearningStreak(Long userId) {
        log.debug("Incrementing learning streak for user: {}", userId);
        userRepository.incrementLearningStreak(userId);
    }
    
    public void resetLearningStreak(Long userId) {
        log.debug("Resetting learning streak for user: {}", userId);
        userRepository.resetLearningStreak(userId);
    }
    
    public void addPoints(Long userId, Integer points) {
        log.debug("Adding {} points to user: {}", points, userId);
        userRepository.addPoints(userId, points);
        checkAndUpdateLevel(userId);
    }
    
    @CacheEvict(value = "users", key = "#username")
    public void deleteUser(String username) {
        log.warn("Deleting user: {}", username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setActive(false);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("User soft deleted: {}", username);
    }
    
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }
    
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
    
    public Page<User> getLeaderboard(Pageable pageable) {
        log.debug("Getting leaderboard");
        return userRepository.findLeaderboard(pageable);
    }
    
    public List<User> getActiveLearnersWithStreak() {
        log.debug("Getting active learners with streak");
        return userRepository.findActiveLearnersWithStreak();
    }
    
    public Long countActiveUsers() {
        return userRepository.countActiveUsers();
    }
    
    public Long countNewUsersSince(LocalDateTime date) {
        return userRepository.countNewUsersSince(date);
    }
    
    // Email verification
    public void verifyEmail(String token) {
        log.info("Verifying email with token: {}", token);
        
        User user = userRepository.findByEmailVerificationToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid verification token"));
        
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Email verified for user: {}", user.getUsername());
    }
    
    // Password reset
    public void initiatePasswordReset(String email) {
        log.info("Initiating password reset for email: {}", email);
        
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        user.setPasswordResetToken(generateToken());
        user.setPasswordResetExpires(LocalDateTime.now().plusHours(24));
        userRepository.save(user);
        
        // TODO: Send password reset email
        log.info("Password reset initiated for user: {}", user.getUsername());
    }
    
    public void resetPassword(String token, String newPassword) {
        log.info("Resetting password with token");
        
        User user = userRepository.findByValidPasswordResetToken(token, LocalDateTime.now())
            .orElseThrow(() -> new RuntimeException("Invalid or expired reset token"));
        
        user.setPassword(passwordEncoder.encode(newPassword));
        user.setPasswordResetToken(null);
        user.setPasswordResetExpires(null);
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
        
        log.info("Password reset successful for user: {}", user.getUsername());
    }
    
    // Role management
    public void addRoleToUser(String username, String roleName) {
        log.info("Adding role {} to user {}", roleName, username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        user.addRole(role);
        userRepository.save(user);
        
        log.info("Role added successfully");
    }
    
    public void removeRoleFromUser(String username, String roleName) {
        log.info("Removing role {} from user {}", roleName, username);
        
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        Role role = roleRepository.findByName(roleName)
            .orElseThrow(() -> new RuntimeException("Role not found"));
        
        user.removeRole(role);
        userRepository.save(user);
        
        log.info("Role removed successfully");
    }
    
    // Helper methods
    private void validateNewUser(User user) {
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new RuntimeException("Username already exists");
        }
        
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
    }
    
    private String generateToken() {
        return UUID.randomUUID().toString();
    }
    
    private void checkAndUpdateLevel(Long userId) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User not found"));
        
        // Simple level calculation based on points
        int newLevel = calculateLevel(user.getTotalPoints());
        
        if (newLevel > user.getCurrentLevel()) {
            user.setCurrentLevel(newLevel);
            userRepository.save(user);
            log.info("User {} leveled up to level {}", user.getUsername(), newLevel);
        }
    }
    
    private int calculateLevel(int points) {
        // Simple level calculation: every 1000 points = 1 level
        return Math.min(1 + (points / 1000), 100);
    }
}