package com.fachinformatiker.lernapp.domain.enums;

/**
 * Schwierigkeitsgrade für Fragen.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
public enum DifficultyLevel {
    BEGINNER(1, "Anfänger"),
    EASY(2, "Leicht"),
    MEDIUM(3, "Mittel"),
    HARD(4, "Schwer"),
    EXPERT(5, "Experte");

    private final int level;
    private final String displayName;

    DifficultyLevel(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }
}
