package com.fachinformatiker.lernapp.domain.enums;

/**
 * Fragentypen für das Lernsystem.
 * 
 * @author Hans Hahn
 * @since 2025-08-13
 */
public enum QuestionType {
    MULTIPLE_CHOICE("Multiple Choice"),
    SINGLE_CHOICE("Single Choice"),
    TRUE_FALSE("Wahr/Falsch"),
    TEXT_INPUT("Texteingabe"),
    CODE_COMPLETION("Code-Vervollständigung"),
    DRAG_DROP("Drag & Drop"),
    MATCHING("Zuordnung");

    private final String displayName;

    QuestionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
