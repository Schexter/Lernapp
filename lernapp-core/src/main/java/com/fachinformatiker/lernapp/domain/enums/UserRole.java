package com.fachinformatiker.lernapp.domain.enums;

/**
 * Benutzerrollen im System.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
public enum UserRole {
    ADMIN("Administrator"),
    INSTRUCTOR("Ausbilder"),
    STUDENT("Auszubildender"),
    GUEST("Gast");

    private final String displayName;

    UserRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
