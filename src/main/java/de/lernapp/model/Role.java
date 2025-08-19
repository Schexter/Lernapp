package de.lernapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Set;
import java.util.HashSet;

/**
 * Role Entity für Benutzerrollen
 * Definiert verschiedene Zugriffsebenen in der Anwendung
 */
@Entity
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    @Enumerated(EnumType.STRING)
    private RoleName name;
    
    @Column(length = 100)
    private String description;
    
    @ManyToMany(mappedBy = "roles")
    private Set<User> users = new HashSet<>();
    
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "role_permissions",
        joinColumns = @JoinColumn(name = "role_id"),
        inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();
    
    // Role Namen als Enum
    public enum RoleName {
        ROLE_USER,      // Standard Benutzer
        ROLE_PREMIUM,   // Premium Benutzer mit erweiterten Features
        ROLE_ADMIN,     // Administrator
        ROLE_MODERATOR  // Moderator für Fragen-Verwaltung
    }
    
    // Helper Methods
    public void addPermission(Permission permission) {
        permissions.add(permission);
    }
    
    public void removePermission(Permission permission) {
        permissions.remove(permission);
    }
}