# FACHINFORMATIKER LERNAPP - KOMPILIERUNGS-ANLEITUNG

## Schnellstart (Alles in einem Befehl):
```batch
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
gradlew.bat clean build -x test && gradlew.bat :lernapp-web:bootRun
```

## Schritt für Schritt:

### 1. Terminal öffnen
- Windows-Taste + R
- "cmd" eingeben
- Enter drücken

### 2. Zum Projektverzeichnis wechseln
```batch
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java
```

### 3. Alte Build-Dateien löschen (optional aber empfohlen)
```batch
gradlew.bat clean
```

### 4. Projekt kompilieren
```batch
gradlew.bat build -x test
```
- `-x test` überspringt die Tests für schnellere Kompilierung

### 5. Anwendung starten
```batch
gradlew.bat :lernapp-web:bootRun
```

## Alternative: Nur kompilieren ohne Tests
```batch
gradlew.bat compileJava
```

## Alternative: Mit IntelliJ IDEA
1. Projekt in IntelliJ öffnen
2. Rechtsklick auf `LernappApplication.java`
3. "Run 'LernappApplication'" wählen

## Troubleshooting

### Wenn Kompilierung fehlschlägt:
```batch
# Cache löschen
gradlew.bat clean cleanBuildCache

# Dependencies neu laden
gradlew.bat build --refresh-dependencies
```

### Wenn Port 8080 belegt ist:
```batch
# Java-Prozesse beenden
taskkill /F /IM java.exe

# Oder anderen Port verwenden
gradlew.bat :lernapp-web:bootRun -Dserver.port=8081
```

## Erfolgreich kompiliert wenn:
- Keine roten Fehler erscheinen
- "BUILD SUCCESSFUL" erscheint
- Bei bootRun: "Started LernappApplication in X seconds" erscheint
- App erreichbar unter: http://localhost:8080
