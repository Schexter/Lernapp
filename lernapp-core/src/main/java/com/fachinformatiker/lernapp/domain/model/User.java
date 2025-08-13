package com.fachinformatiker.lernapp.domain.model;

import com.fachinformatiker.lernapp.domain.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * User Entity - Repräsentiert einen Benutzer im System.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
@Entity
@Table(name = "users", 
    indexes = {
        @Index(name = "idx_username", columnList = "username"),
        @Index(name = "idx_email", columnList = "email"),
        @Index(name = "idx_active", columnList = "active")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = "username"),
        @UniqueConstraint(columnNames = "email")
    }
)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true, exclude = {"progressList", "learningPaths"})
@ToString(exclude = {"progressList", "learningPaths"})
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
public class User extends BaseEntity {

    @Column(name = "username", nullable = false, unique = true, length = 50)
    @NotBlank(message = "Username ist erforderlich")
    @Size(min = 3, max = 50, message = "Username muss zwischen 3 und 50 Zeichen lang sein")
    private String username;

    @Column(name = "email", nullable = false, unique = true, length = 255)
    @NotBlank(message = "Email ist erforderlich")
    @Email(message = "Email muss gültig sein")
    private String email;

    @Column(name = "password_hash", nullable = false)
    @NotBlank(message = "Passwort ist erforderlich")
    private String passwordHash;

    @Column(name = "first_name", length = 100)
    @Size(max = 100, message = "Vorname darf maximal 100 Zeichen lang sein")
    private String firstName;

    @Column(name = "last_name", length = 100)
    @Size(max = 100, message = "Nachname darf maximal 100 Zeichen lang sein")
    private String lastName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    @Builder.Default
    private UserRole role = UserRole.STUDENT;

    @Column(name = "avatar_url", length = 500)
    private String avatarUrl;

    @Column(name = "bio", columnDefinition = "TEXT")
    private String bio;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "login_streak")
    @Builder.Default
    private Integer loginStreak = 0;

    @Column(name = "total_points")
    @Builder.Default
    private Integer totalPoints = 0;

    @Column(name = "email_verified")
    @Builder.Default
    private Boolean emailVerified = false;

    @Column(name = "notification_enabled")
    @Builder.Default
    private Boolean notificationEnabled = true;

    @Column(name = "theme_preference", length = 20)
    @Builder.Default
    private String themePreference = "light";

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "de";

    // Beziehungen - werden lazy geladen für Performance
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Progress> progressList = new ArrayList<>();

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "user_learning_paths",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "learning_path_id")
    )
    @Builder.Default
    private Set<LearningPath> learningPaths = new HashSet<>();

    /**
     * Hilfsmethode: Vollständiger Name
     */
    public String getFullName() {
        if (firstName != null && lastName != null) {
            return firstName + " " + lastName;
        } else if (firstName != null) {
            return firstName;
        } else if (lastName != null) {
            return lastName;
        } else {
            return username;
        }
    }

    /**
     * Hilfsmethode: Ist der User ein Admin?
     */
    public boolean isAdmin() {
        return UserRole.ADMIN.equals(this.role);
    }

    /**
     * Hilfsmethode: Ist der User ein Instructor?
     */
    public boolean isInstructor() {
        return UserRole.INSTRUCTOR.equals(this.role);
    }

    /**
     * Hilfsmethode: Points hinzufügen
     */
    public void addPoints(int points) {
        this.totalPoints = (this.totalPoints == null ? 0 : this.totalPoints) + points;
    }

    /**
     * Lifecycle: Vor dem Speichern
     */
    @PrePersist
    @Override
    protected void onCreate() {
        super.onCreate();
        if (role == null) {
            role = UserRole.STUDENT;
        }
        if (loginStreak == null) {
            loginStreak = 0;
        }
        if (totalPoints == null) {
            totalPoints = 0;
        }
    }
}
