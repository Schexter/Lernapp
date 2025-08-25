package com.fachinformatiker.lernapp.init;

import com.fachinformatiker.lernapp.model.Question;
import com.fachinformatiker.lernapp.model.Question.Difficulty;
import com.fachinformatiker.lernapp.repository.QuestionRepository;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

/**
 * DataLoader lädt initiale Daten beim Applikationsstart
 * Fügt Beispiel-Fragen in die Datenbank ein
 */
@Component
public class DataLoader {
    
    @Autowired
    private QuestionRepository questionRepository;
    
    @PostConstruct
    public void loadData() {
        // Prüfe ob schon Daten vorhanden sind
        if (questionRepository.count() > 0) {
            System.out.println("✅ Datenbank enthält bereits " + questionRepository.count() + " Fragen");
            return;
        }
        
        System.out.println("📚 Lade initiale Fragen in die Datenbank...");
        
        // Java/Programmierung Fragen
        loadProgrammingQuestions();
        
        // Datenbank Fragen
        loadDatabaseQuestions();
        
        // Netzwerk Fragen
        loadNetworkQuestions();
        
        // Web Development Fragen
        loadWebDevQuestions();
        
        // IT-Sicherheit Fragen
        loadSecurityQuestions();
        
        System.out.println("✅ " + questionRepository.count() + " Fragen erfolgreich geladen!");
    }
    
    private void loadProgrammingQuestions() {
        List<Question> questions = Arrays.asList(
            createQuestion("Programmierung", "Java", "Was ist der Unterschied zwischen == und equals() in Java?",
                Arrays.asList(
                    "== vergleicht Referenzen, equals() vergleicht Inhalte",
                    "== vergleicht Inhalte, equals() vergleicht Referenzen",
                    "Beide sind identisch",
                    "== ist für Primitive, equals() für Objekte"
                ),
                "== vergleicht Referenzen, equals() vergleicht Inhalte",
                "Bei Objekten vergleicht == die Speicheradressen (Referenzen), während equals() den Inhalt der Objekte vergleicht (wenn überschrieben).",
                Difficulty.MITTEL
            ),
            
            createQuestion("Programmierung", "Java", "Welches Schlüsselwort wird verwendet, um eine Klasse in Java zu erben?",
                Arrays.asList("implements", "extends", "inherits", "derives"),
                "extends",
                "Das Schlüsselwort 'extends' wird für Klassenvererbung verwendet, während 'implements' für Interfaces genutzt wird.",
                Difficulty.LEICHT
            ),
            
            createQuestion("Programmierung", "OOP", "Was ist Polymorphismus?",
                Arrays.asList(
                    "Die Fähigkeit, mehrere Klassen zu erstellen",
                    "Die Fähigkeit einer Methode, verschiedene Formen anzunehmen",
                    "Die Kapselung von Daten",
                    "Die Vererbung von Eigenschaften"
                ),
                "Die Fähigkeit einer Methode, verschiedene Formen anzunehmen",
                "Polymorphismus ermöglicht es, dass eine Methode je nach Kontext unterschiedlich implementiert werden kann (Überladen, Überschreiben).",
                Difficulty.MITTEL
            ),
            
            createQuestion("Programmierung", "Datenstrukturen", "Was ist die zeitliche Komplexität für das Einfügen in eine HashMap?",
                Arrays.asList("O(1)", "O(n)", "O(log n)", "O(n²)"),
                "O(1)",
                "HashMaps bieten im Durchschnitt O(1) für Einfügen, Löschen und Suchen. Im schlechtesten Fall (viele Kollisionen) kann es O(n) sein.",
                Difficulty.MITTEL
            ),
            
            createQuestion("Programmierung", "Algorithmen", "Welcher Sortieralgorithmus hat die beste durchschnittliche Zeitkomplexität?",
                Arrays.asList("Bubble Sort", "Quick Sort", "Selection Sort", "Insertion Sort"),
                "Quick Sort",
                "Quick Sort hat eine durchschnittliche Zeitkomplexität von O(n log n), während die anderen O(n²) haben.",
                Difficulty.MITTEL
            )
        );
        
        questionRepository.saveAll(questions);
    }
    
    private void loadDatabaseQuestions() {
        List<Question> questions = Arrays.asList(
            createQuestion("Datenbanken", "SQL", "Was macht der SQL-Befehl 'GROUP BY'?",
                Arrays.asList(
                    "Sortiert die Ergebnisse",
                    "Gruppiert Zeilen mit gleichen Werten",
                    "Verbindet Tabellen",
                    "Filtert Ergebnisse"
                ),
                "Gruppiert Zeilen mit gleichen Werten",
                "GROUP BY gruppiert Zeilen mit identischen Werten in den angegebenen Spalten und wird oft mit Aggregatfunktionen verwendet.",
                Difficulty.LEICHT
            ),
            
            createQuestion("Datenbanken", "SQL", "Welcher JOIN-Typ gibt alle Zeilen aus beiden Tabellen zurück?",
                Arrays.asList("INNER JOIN", "LEFT JOIN", "RIGHT JOIN", "FULL OUTER JOIN"),
                "FULL OUTER JOIN",
                "FULL OUTER JOIN gibt alle Zeilen aus beiden Tabellen zurück, auch wenn keine Übereinstimmung vorliegt.",
                Difficulty.MITTEL
            ),
            
            createQuestion("Datenbanken", "Normalisierung", "Was ist die 1. Normalform (1NF)?",
                Arrays.asList(
                    "Keine transitiven Abhängigkeiten",
                    "Atomare Werte in allen Spalten",
                    "Keine partiellen Abhängigkeiten",
                    "Alle Nicht-Schlüssel-Attribute sind vom Primärschlüssel abhängig"
                ),
                "Atomare Werte in allen Spalten",
                "Die 1NF verlangt, dass alle Attribute atomare (unteilbare) Werte enthalten und keine Wiederholungsgruppen existieren.",
                Difficulty.MITTEL
            ),
            
            createQuestion("Datenbanken", "Transaktionen", "Was bedeutet ACID in Datenbanken?",
                Arrays.asList(
                    "Atomicity, Consistency, Isolation, Durability",
                    "Automatic, Concurrent, Isolated, Durable",
                    "Atomic, Complete, Independent, Durable",
                    "Asynchronous, Consistent, Indexed, Distributed"
                ),
                "Atomicity, Consistency, Isolation, Durability",
                "ACID sind die vier Grundeigenschaften von Datenbanktransaktionen für Zuverlässigkeit.",
                Difficulty.MITTEL
            ),
            
            createQuestion("Datenbanken", "Index", "Wann sollte man KEINEN Index verwenden?",
                Arrays.asList(
                    "Bei häufigen SELECT-Abfragen",
                    "Bei Tabellen mit vielen INSERT/UPDATE Operationen",
                    "Bei WHERE-Klauseln",
                    "Bei JOIN-Operationen"
                ),
                "Bei Tabellen mit vielen INSERT/UPDATE Operationen",
                "Indizes verlangsamen INSERT/UPDATE/DELETE Operationen, da sie aktualisiert werden müssen.",
                Difficulty.SCHWER
            )
        );
        
        questionRepository.saveAll(questions);
    }
    
    private void loadNetworkQuestions() {
        List<Question> questions = Arrays.asList(
            createQuestion("Netzwerktechnik", "OSI-Modell", "Auf welcher OSI-Schicht arbeitet ein Switch?",
                Arrays.asList("Schicht 1", "Schicht 2", "Schicht 3", "Schicht 4"),
                "Schicht 2",
                "Switches arbeiten auf der Data Link Layer (Schicht 2) und verwenden MAC-Adressen für die Weiterleitung.",
                Difficulty.LEICHT
            ),
            
            createQuestion("Netzwerktechnik", "TCP/IP", "Welcher Port wird standardmäßig für HTTPS verwendet?",
                Arrays.asList("80", "443", "8080", "8443"),
                "443",
                "HTTPS verwendet standardmäßig Port 443, während HTTP Port 80 verwendet.",
                Difficulty.LEICHT
            ),
            
            createQuestion("Netzwerktechnik", "Subnetting", "Wie viele nutzbare Hosts hat ein /28 Subnetz?",
                Arrays.asList("14", "16", "30", "32"),
                "14",
                "Ein /28 Subnetz hat 16 IP-Adressen (2^4), davon sind 14 nutzbar (16 - Netzwerk - Broadcast).",
                Difficulty.MITTEL
            ),
            
            createQuestion("Netzwerktechnik", "Protokolle", "Was ist der Unterschied zwischen TCP und UDP?",
                Arrays.asList(
                    "TCP ist verbindungslos, UDP ist verbindungsorientiert",
                    "TCP ist verbindungsorientiert, UDP ist verbindungslos",
                    "Beide sind identisch",
                    "TCP ist schneller als UDP"
                ),
                "TCP ist verbindungsorientiert, UDP ist verbindungslos",
                "TCP garantiert Zustellung und Reihenfolge, UDP ist schneller aber ohne Garantien.",
                Difficulty.MITTEL
            ),
            
            createQuestion("Netzwerktechnik", "DNS", "Was ist ein CNAME-Record?",
                Arrays.asList(
                    "Ein Alias für einen anderen Domainnamen",
                    "Eine IP-Adresse",
                    "Ein Mail-Server-Eintrag",
                    "Ein Nameserver-Eintrag"
                ),
                "Ein Alias für einen anderen Domainnamen",
                "CNAME (Canonical Name) Records erstellen einen Alias, der auf einen anderen Domainnamen verweist.",
                Difficulty.MITTEL
            )
        );
        
        questionRepository.saveAll(questions);
    }
    
    private void loadWebDevQuestions() {
        List<Question> questions = Arrays.asList(
            createQuestion("Web Development", "HTML", "Was ist semantisches HTML?",
                Arrays.asList(
                    "HTML mit CSS-Styling",
                    "HTML-Elemente, die ihre Bedeutung beschreiben",
                    "Validiertes HTML",
                    "HTML5-Elemente"
                ),
                "HTML-Elemente, die ihre Bedeutung beschreiben",
                "Semantische Elemente wie <header>, <nav>, <article> beschreiben ihren Inhalt, was für SEO und Barrierefreiheit wichtig ist.",
                Difficulty.LEICHT
            ),
            
            createQuestion("Web Development", "CSS", "Was ist der Unterschied zwischen 'margin' und 'padding'?",
                Arrays.asList(
                    "Margin ist innen, Padding ist außen",
                    "Margin ist außen, Padding ist innen",
                    "Beide sind identisch",
                    "Margin für Breite, Padding für Höhe"
                ),
                "Margin ist außen, Padding ist innen",
                "Margin ist der Außenabstand eines Elements, Padding der Innenabstand zwischen Rand und Inhalt.",
                Difficulty.LEICHT
            ),
            
            createQuestion("Web Development", "JavaScript", "Was ist ein Closure in JavaScript?",
                Arrays.asList(
                    "Eine geschlossene Funktion",
                    "Eine Funktion mit Zugriff auf äußere Variablen",
                    "Ein Fehlertyp",
                    "Eine Schleifenart"
                ),
                "Eine Funktion mit Zugriff auf äußere Variablen",
                "Closures ermöglichen es Funktionen, auf Variablen aus ihrem äußeren Scope zuzugreifen, auch nachdem die äußere Funktion beendet wurde.",
                Difficulty.SCHWER
            ),
            
            createQuestion("Web Development", "REST", "Was ist idempotent bei REST-APIs?",
                Arrays.asList(
                    "Mehrfache identische Anfragen haben denselben Effekt wie eine einzelne",
                    "Die API antwortet immer gleich schnell",
                    "Die API verwendet IDs",
                    "Die API ist zustandslos"
                ),
                "Mehrfache identische Anfragen haben denselben Effekt wie eine einzelne",
                "GET, PUT, DELETE sind idempotent - mehrfache Ausführung ändert das Ergebnis nicht. POST ist nicht idempotent.",
                Difficulty.SCHWER
            ),
            
            createQuestion("Web Development", "HTTP", "Was bedeutet der HTTP-Statuscode 418?",
                Arrays.asList(
                    "Not Found",
                    "I'm a teapot",
                    "Unauthorized",
                    "Bad Request"
                ),
                "I'm a teapot",
                "HTTP 418 ist ein Aprilscherz-Statuscode aus RFC 2324 (Hyper Text Coffee Pot Control Protocol).",
                Difficulty.LEICHT
            )
        );
        
        questionRepository.saveAll(questions);
    }
    
    private void loadSecurityQuestions() {
        List<Question> questions = Arrays.asList(
            createQuestion("IT-Sicherheit", "Kryptographie", "Was ist der Unterschied zwischen symmetrischer und asymmetrischer Verschlüsselung?",
                Arrays.asList(
                    "Symmetrisch nutzt einen Schlüssel, asymmetrisch zwei",
                    "Symmetrisch nutzt zwei Schlüssel, asymmetrisch einen",
                    "Beide sind identisch",
                    "Symmetrisch ist langsamer"
                ),
                "Symmetrisch nutzt einen Schlüssel, asymmetrisch zwei",
                "Symmetrische Verschlüsselung verwendet denselben Schlüssel für Ver- und Entschlüsselung, asymmetrische nutzt ein Schlüsselpaar.",
                Difficulty.MITTEL
            ),
            
            createQuestion("IT-Sicherheit", "Angriffe", "Was ist SQL-Injection?",
                Arrays.asList(
                    "Ein Virus",
                    "Einschleusen von SQL-Befehlen über Eingabefelder",
                    "Ein Verschlüsselungsverfahren",
                    "Ein Netzwerkprotokoll"
                ),
                "Einschleusen von SQL-Befehlen über Eingabefelder",
                "Bei SQL-Injection werden schadhafte SQL-Befehle über ungesicherte Eingabefelder eingeschleust, um die Datenbank zu manipulieren.",
                Difficulty.MITTEL
            ),
            
            createQuestion("IT-Sicherheit", "Authentifizierung", "Was ist 2FA?",
                Arrays.asList(
                    "Zwei Passwörter",
                    "Zwei-Faktor-Authentifizierung",
                    "Zweite Firewall-Aktivierung",
                    "Zwei-Fenster-Anwendung"
                ),
                "Zwei-Faktor-Authentifizierung",
                "2FA erfordert zwei verschiedene Authentifizierungsmethoden, z.B. Passwort (Wissen) und SMS-Code (Besitz).",
                Difficulty.LEICHT
            ),
            
            createQuestion("IT-Sicherheit", "Hashing", "Welcher Hashing-Algorithmus gilt als unsicher?",
                Arrays.asList("SHA-256", "SHA-512", "MD5", "bcrypt"),
                "MD5",
                "MD5 ist anfällig für Kollisionsangriffe und sollte nicht mehr für sicherheitskritische Anwendungen verwendet werden.",
                Difficulty.MITTEL
            ),
            
            createQuestion("IT-Sicherheit", "XSS", "Was ist Cross-Site Scripting (XSS)?",
                Arrays.asList(
                    "Einschleusen von JavaScript in Webseiten",
                    "Übertragen von Dateien zwischen Servern",
                    "Verschlüsselte Verbindungen",
                    "Cross-Platform-Entwicklung"
                ),
                "Einschleusen von JavaScript in Webseiten",
                "XSS ermöglicht es Angreifern, schädlichen JavaScript-Code in Webseiten einzuschleusen, der im Browser anderer Nutzer ausgeführt wird.",
                Difficulty.MITTEL
            )
        );
        
        questionRepository.saveAll(questions);
    }
    
    private Question createQuestion(String category, String subcategory, String text, 
                                   List<String> options, String correct, String explanation, 
                                   Difficulty difficulty) {
        Question q = new Question();
        q.setCategory(category);
        q.setSubcategory(subcategory);
        q.setQuestionText(text);
        q.setOptions(options);
        q.setCorrectAnswer(correct);
        q.setExplanation(explanation);
        q.setDifficulty(difficulty);
        q.setPoints(difficulty == Difficulty.LEICHT ? 1 : difficulty == Difficulty.MITTEL ? 2 : 3);
        return q;
    }
}
