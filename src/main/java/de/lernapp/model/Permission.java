package de.lernapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Builder;

import java.util.Set;
import java.util.HashSet;

/**
 * Permission Entity für granulare Berechtigungen
 * Definiert spezifische Aktionen, die Benutzer ausführen können
 */
@Entity
@Table(name = "permissions")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 100)
    private String name;
    
    @Column(length = 200)
    private String description;
    
    @ManyToMany(mappedBy = "permissions")
    private Set<Role> roles = new HashSet<>();
    
    // Vordefinierte Permissions
    public static final String VIEW_QUESTIONS = "VIEW_QUESTIONS";
    public static final String ANSWER_QUESTIONS = "ANSWER_QUESTIONS";
    public static final String CREATE_QUESTIONS = "CREATE_QUESTIONS";
    public static final String EDIT_QUESTIONS = "EDIT_QUESTIONS";
    public static final String DELETE_QUESTIONS = "DELETE_QUESTIONS";
    public static final String VIEW_STATISTICS = "VIEW_STATISTICS";
    public static final String VIEW_ALL_STATISTICS = "VIEW_ALL_STATISTICS";
    public static final String MANAGE_USERS = "MANAGE_USERS";
    public static final String MANAGE_ROLES = "MANAGE_ROLES";
    public static final String VIEW_ADMIN_PANEL = "VIEW_ADMIN_PANEL";
    public static final String EXPORT_DATA = "EXPORT_DATA";
    public static final String IMPORT_DATA = "IMPORT_DATA";
}