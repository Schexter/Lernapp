package com.fachinformatiker.lernapp.enums;

public enum QuestionType {
    MULTIPLE_CHOICE("Multiple Choice"),
    TRUE_FALSE("Wahr/Falsch"),
    FILL_IN_BLANK("Lückentext"),
    MATCHING("Zuordnung");
    
    private final String displayName;
    
    QuestionType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}