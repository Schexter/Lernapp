package com.fachinformatiker.lernapp.service;

import com.fachinformatiker.lernapp.model.Role;
import com.fachinformatiker.lernapp.model.User;
import com.fachinformatiker.lernapp.repository.RoleRepository;
import com.fachinformatiker.lernapp.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("UserService Tests")
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User testUser;
    private Role userRole;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
            .id(1L)
            .username("testuser")
            .email("test@example.com")
            .password("hashedPassword")
            .firstName("Test")
            .lastName("User")
            .active(true)
            .emailVerified(false)
            .roles(new HashSet<>())
            .build();

        userRole = Role.builder()
            .id(1L)
            .name("ROLE_USER")
            .description("Standard user role")
            .build();
    }

    @Test
    @DisplayName("Should create user successfully")
    void shouldCreateUserSuccessfully() {
        // Given
        User newUser = User.builder()
            .username("newuser")
            .email("new@example.com")
            .password("password123")
            .build();

        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(userRepository.save(any(User.class))).thenReturn(newUser);

        // When
        User createdUser = userService.createUser(newUser);

        // Then
        assertThat(createdUser).isNotNull();
        verify(passwordEncoder).encode("password123");
        verify(userRepository).save(any(User.class));
        verify(roleRepository).findByName("ROLE_USER");
    }

    @Test
    @DisplayName("Should throw exception when username already exists")
    void shouldThrowExceptionWhenUsernameExists() {
        // Given
        User newUser = User.builder()
            .username("existinguser")
            .email("new@example.com")
            .password("password123")
            .build();

        when(userRepository.existsByUsername("existinguser")).thenReturn(true);

        // When/Then
        assertThatThrownBy(() -> userService.createUser(newUser))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Username already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should find user by username")
    void shouldFindUserByUsername() {
        // Given
        when(userRepository.findByUsernameWithRoles("testuser"))
            .thenReturn(Optional.of(testUser));

        // When
        Optional<User> foundUser = userService.findByUsername("testuser");

        // Then
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getUsername()).isEqualTo("testuser");
        verify(userRepository).findByUsernameWithRoles("testuser");
    }

    @Test
    @DisplayName("Should update password successfully")
    void shouldUpdatePasswordSuccessfully() {
        // Given
        String oldPassword = "oldPassword";
        String newPassword = "newPassword";
        String encodedNewPassword = "encodedNewPassword";

        when(userRepository.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(oldPassword, testUser.getPassword()))
            .thenReturn(true);
        when(passwordEncoder.encode(newPassword))
            .thenReturn(encodedNewPassword);
        when(userRepository.save(any(User.class)))
            .thenReturn(testUser);

        // When
        userService.updatePassword("testuser", oldPassword, newPassword);

        // Then
        verify(passwordEncoder).matches(oldPassword, testUser.getPassword());
        verify(passwordEncoder).encode(newPassword);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should throw exception when old password is incorrect")
    void shouldThrowExceptionWhenOldPasswordIncorrect() {
        // Given
        when(userRepository.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches(anyString(), anyString()))
            .thenReturn(false);

        // When/Then
        assertThatThrownBy(() -> 
            userService.updatePassword("testuser", "wrongPassword", "newPassword"))
            .isInstanceOf(RuntimeException.class)
            .hasMessage("Invalid old password");

        verify(userRepository, never()).save(any());
    }

    @Test
    @DisplayName("Should verify email successfully")
    void shouldVerifyEmailSuccessfully() {
        // Given
        String token = "verification-token";
        testUser.setEmailVerificationToken(token);
        testUser.setEmailVerified(false);

        when(userRepository.findByEmailVerificationToken(token))
            .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
            .thenReturn(testUser);

        // When
        userService.verifyEmail(token);

        // Then
        assertThat(testUser.getEmailVerified()).isTrue();
        assertThat(testUser.getEmailVerificationToken()).isNull();
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should add role to user")
    void shouldAddRoleToUser() {
        // Given
        Role adminRole = Role.builder()
            .id(2L)
            .name("ROLE_ADMIN")
            .users(new HashSet<>())
            .build();

        when(userRepository.findByUsername("testuser"))
            .thenReturn(Optional.of(testUser));
        when(roleRepository.findByName("ROLE_ADMIN"))
            .thenReturn(Optional.of(adminRole));
        when(userRepository.save(any(User.class)))
            .thenReturn(testUser);

        // When
        userService.addRoleToUser("testuser", "ROLE_ADMIN");

        // Then
        assertThat(testUser.getRoles()).contains(adminRole);
        verify(userRepository).save(testUser);
    }

    @Test
    @DisplayName("Should increment learning streak")
    void shouldIncrementLearningStreak() {
        // Given
        Long userId = 1L;

        // When
        userService.incrementLearningStreak(userId);

        // Then
        verify(userRepository).incrementLearningStreak(userId);
    }

    @Test
    @DisplayName("Should add points and check level")
    void shouldAddPointsAndCheckLevel() {
        // Given
        Long userId = 1L;
        Integer points = 100;
        testUser.setTotalPoints(900);
        testUser.setCurrentLevel(1);

        when(userRepository.findById(userId))
            .thenReturn(Optional.of(testUser));
        when(userRepository.save(any(User.class)))
            .thenReturn(testUser);

        // When
        userService.addPoints(userId, points);

        // Then
        verify(userRepository).addPoints(userId, points);
        verify(userRepository).findById(userId);
    }
}