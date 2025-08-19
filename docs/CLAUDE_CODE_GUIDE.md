# 🤖 CLAUDE CODE INTEGRATION GUIDE
*Fachinformatiker Lernapp - Java Spring Boot*
*Erstellt von Hans Hahn - Alle Rechte vorbehalten*

## 📍 PROJEKT-LOKATION
```
C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\
```

## 🎯 MISSION STATEMENT
Du arbeitest an einer Spring Boot Lernapp für Fachinformatiker. Das Projekt verwendet Java 17, Spring Boot 3.x, H2/PostgreSQL, und Thymeleaf Templates. Ziel ist es, eine vollständige Lernplattform mit User-Management, Lernfortschritt-Tracking und Prüfungsmodus zu entwickeln.

## 📋 ARBEITSWEISE

### 1. SESSION START:
```bash
# Zuerst immer:
1. Öffne TODO.md
2. Prüfe CHANGELOG.md für letzten Stand
3. Prüfe error.log für ungelöste Probleme
4. Wähle aktuellen Sprint aus TODO.md
```

### 2. ENTWICKLUNGS-FLOW:
```
Task auswählen → Code schreiben → Testen → Dokumentieren → Commit
```

### 3. NACH JEDER ÄNDERUNG:
```markdown
# CHANGELOG.md Update Format:
## [YYYY-MM-DD HH:MM] - Sprint X: [Feature]
### Durchgeführt:
- Was wurde implementiert
### Funktioniert:
- Was erfolgreich getestet wurde
### Nächste Schritte:
- Was als nächstes kommt
```

## 🏗️ CODE-STRUKTUR

### NEUE KLASSE ERSTELLEN:
```java
package de.lernapp.[layer];

import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * [Beschreibung der Klasse - Deutsch]
 * 
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ExampleService {
    
    private final ExampleRepository repository;
    
    /**
     * [Methoden-Beschreibung - Deutsch]
     * 
     * @param id Die ID des gesuchten Elements
     * @return Das gefundene Element oder null
     */
    public Example findById(Long id) {
        log.debug("Searching for Example with id: {}", id);
        return repository.findById(id).orElse(null);
    }
}
```

### ENTITY TEMPLATE:
```java
package de.lernapp.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "examples")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Example {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }
}
```

### REST CONTROLLER TEMPLATE:
```java
package de.lernapp.controller.api;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/examples")
@RequiredArgsConstructor
public class ExampleController {
    
    private final ExampleService service;
    
    @GetMapping("/{id}")
    public ResponseEntity<Example> getById(@PathVariable Long id) {
        Example result = service.findById(id);
        return result != null 
            ? ResponseEntity.ok(result)
            : ResponseEntity.notFound().build();
    }
    
    @PostMapping
    public ResponseEntity<Example> create(@RequestBody Example example) {
        Example created = service.create(example);
        return ResponseEntity.ok(created);
    }
}
```

## 🧪 TEST-STRUKTUR

### UNIT TEST TEMPLATE:
```java
package de.lernapp.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExampleServiceTest {
    
    @Mock
    private ExampleRepository repository;
    
    private ExampleService service;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        service = new ExampleService(repository);
    }
    
    @Test
    void testFindById() {
        // Given
        Long id = 1L;
        Example expected = new Example();
        when(repository.findById(id)).thenReturn(Optional.of(expected));
        
        // When
        Example result = service.findById(id);
        
        // Then
        assertNotNull(result);
        assertEquals(expected, result);
    }
}
```

## 🔧 WICHTIGE BEFEHLE

### GRADLE BEFEHLE:
```bash
# Projekt bauen
./gradlew build

# Tests ausführen
./gradlew test

# Applikation starten
./gradlew bootRun

# Abhängigkeiten anzeigen
./gradlew dependencies

# Clean Build
./gradlew clean build
```

### GIT WORKFLOW:
```bash
# Status prüfen
git status

# Alle Änderungen stagen
git add .

# Commit mit Message
git commit -m "feat(auth): User Entity und Repository implementiert"

# Push zum Remote
git push origin main
```

## 📦 DEPENDENCIES HINZUFÜGEN

### In build.gradle:
```gradle
dependencies {
    // Neue Dependency hinzufügen:
    implementation 'group:artifact:version'
    
    // Beispiele:
    implementation 'io.jsonwebtoken:jjwt-api:0.11.5'
    implementation 'org.springframework.boot:spring-boot-starter-mail'
    implementation 'org.mapstruct:mapstruct:1.5.5.Final'
}
```

## 🐛 FEHLERBEHANDLUNG

### Bei Compile-Fehlern:
```bash
1. ./gradlew clean
2. ./gradlew build --refresh-dependencies
3. IntelliJ: File → Invalidate Caches and Restart
```

### Bei Runtime-Fehlern:
```java
// In error.log dokumentieren:
[YYYY-MM-DD HH:MM:SS] ERROR: [Fehlerbeschreibung]
Ursache: [Was war die Ursache]
Lösung: [Wie wurde es gelöst]
Verbesserung: [Präventionsmaßnahme]
---
```

## 🎯 SPRINT-CHECKLISTE

### VOR SPRINT-START:
- [ ] TODO.md öffnen und Sprint auswählen
- [ ] Alle benötigten Dependencies prüfen
- [ ] Projekt läuft lokal (./gradlew bootRun)

### WÄHREND ENTWICKLUNG:
- [ ] Eine Aufgabe nach der anderen
- [ ] Nach jeder Klasse: Compile-Test
- [ ] Nach jedem Feature: Funktionstest
- [ ] Regelmäßige Commits (alle 30 Min)

### NACH SPRINT-ENDE:
- [ ] Alle Tests laufen durch
- [ ] CHANGELOG.md aktualisiert
- [ ] TODO.md Tasks abgehakt
- [ ] Code committet und gepusht
- [ ] App läuft ohne Fehler

## 🚀 QUICK START FÜR NEUEN SPRINT

```bash
# 1. Terminal öffnen
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java

# 2. Git Status prüfen
git status

# 3. Projekt starten
./gradlew bootRun

# 4. In neuem Terminal: Tests laufen lassen
./gradlew test --continuous

# 5. Browser öffnen
start http://localhost:8080

# 6. H2 Console öffnen (falls benötigt)
start http://localhost:8080/h2-console
# JDBC URL: jdbc:h2:mem:testdb
# Username: sa
# Password: (leer)
```

## 📝 CODING STANDARDS

### NAMENSKONVENTIONEN:
```java
// Packages: lowercase
package de.lernapp.service;

// Classes: PascalCase
public class UserService { }

// Methods: camelCase
public User findByUsername(String username) { }

// Constants: UPPER_SNAKE_CASE
public static final String DEFAULT_ROLE = "STUDENT";

// Variables: camelCase
private String userName;
```

### KOMMENTAR-STYLE:
```java
/**
 * Klassen-Dokumentation (Deutsch für Geschäftslogik)
 */
public class Example {
    
    // Technischer Kommentar (Englisch)
    private static final int MAX_RETRY = 3;
    
    /**
     * Methoden-Dokumentation (Deutsch für wichtige Methoden)
     * 
     * @param input Eingabeparameter
     * @return Rückgabewert
     */
    public String process(String input) {
        // TODO: Implementation needed
        return input;
    }
}
```

## 🎓 BEST PRACTICES

1. **SOLID Principles** einhalten
2. **DRY** (Don't Repeat Yourself)
3. **KISS** (Keep It Simple, Stupid)
4. **Defensive Programming** - Null-Checks!
5. **Logging** - Wichtige Aktionen loggen
6. **Testing** - Minimum 70% Coverage
7. **Documentation** - Code selbsterklärend

## 🆘 HILFE & SUPPORT

### Bei Problemen:
1. Check `error.log`
2. Check `CHANGELOG.md` für ähnliche Probleme
3. Google/StackOverflow konsultieren
4. Spring Boot Docs: https://docs.spring.io/spring-boot/
5. Baeldung Tutorials: https://www.baeldung.com/

---

**REMEMBER:** 
- Ein Feature nach dem anderen
- Erst testen, dann committen
- Dokumentation nicht vergessen
- Bei Unsicherheit: Einfache Lösung wählen

**AKTUELLER FOKUS:** Sprint 1 - User & Authentication

---
*Happy Coding! 🚀*
