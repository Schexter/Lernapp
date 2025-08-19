-- Import AP1 Fragen aus CSV
-- Erstellt von Hans Hahn - Alle Rechte vorbehalten

-- Temporäre Tabelle für CSV-Import erstellen
CREATE TABLE IF NOT EXISTS ap1_import (
    exam_year VARCHAR(10),
    exam_season VARCHAR(50),
    main_category VARCHAR(100),
    sub_category VARCHAR(100),
    topic_tags VARCHAR(500),
    question_text VARCHAR(1000),
    answer_a VARCHAR(500),
    answer_b VARCHAR(500),
    answer_c VARCHAR(500),
    answer_d VARCHAR(500),
    correct_answer VARCHAR(10),
    difficulty VARCHAR(20),
    points INTEGER,
    explanation VARCHAR(2000),
    hint VARCHAR(500),
    frequency VARCHAR(10)
);

-- CSV-Daten importieren (H2 Console: CSVREAD verwenden)
-- In der H2 Console ausführen:
-- INSERT INTO ap1_import SELECT * FROM CSVREAD('C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\data\ALLE_AP1_FRAGEN_IMPORT.csv');

-- Daten in die questions Tabelle übertragen
INSERT INTO questions (question_text, category, difficulty, correct_answer_index, explanation, points, created_at, updated_at)
SELECT 
    question_text,
    main_category,
    CASE 
        WHEN LOWER(difficulty) = 'leicht' THEN 'EASY'
        WHEN LOWER(difficulty) = 'mittel' THEN 'MEDIUM'
        WHEN LOWER(difficulty) = 'schwer' THEN 'HARD'
        ELSE 'MEDIUM'
    END,
    CASE correct_answer
        WHEN 'A' THEN 0
        WHEN 'B' THEN 1
        WHEN 'C' THEN 2
        WHEN 'D' THEN 3
        ELSE 0
    END,
    CONCAT(explanation, ' (Hinweis: ', hint, ')'),
    points,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM ap1_import;

-- Antworten einfügen (für jede Frage 4 Antworten)
-- Antwort A
INSERT INTO question_answers (question_id, answer)
SELECT q.id, ai.answer_a
FROM questions q
JOIN ap1_import ai ON q.question_text = ai.question_text;

-- Antwort B
INSERT INTO question_answers (question_id, answer)
SELECT q.id, ai.answer_b
FROM questions q
JOIN ap1_import ai ON q.question_text = ai.question_text;

-- Antwort C
INSERT INTO question_answers (question_id, answer)
SELECT q.id, ai.answer_c
FROM questions q
JOIN ap1_import ai ON q.question_text = ai.question_text;

-- Antwort D
INSERT INTO question_answers (question_id, answer)
SELECT q.id, ai.answer_d
FROM questions q
JOIN ap1_import ai ON q.question_text = ai.question_text;

-- Temporäre Tabelle löschen
DROP TABLE IF EXISTS ap1_import;

-- Statistik anzeigen
SELECT 
    category,
    COUNT(*) as anzahl_fragen,
    AVG(points) as durchschnitt_punkte
FROM questions
GROUP BY category
ORDER BY anzahl_fragen DESC;
