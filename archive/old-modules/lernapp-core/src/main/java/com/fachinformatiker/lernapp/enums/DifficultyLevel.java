package com.fachinformatiker.lernapp.enums;

public enum DifficultyLevel {
    EASY(1, "Leicht"),
    MEDIUM(2, "Mittel"),
    HARD(3, "Schwer"),
    EXPERT(4, "Experte");
    
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
    
    public static DifficultyLevel fromLevel(int level) {
        for (DifficultyLevel dl : values()) {
            if (dl.level == level) {
                return dl;
            }
        }
        return MEDIUM;
    }
}