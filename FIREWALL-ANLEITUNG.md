# Manuelle Firewall-Konfiguration für Lernapp

## Windows Defender Firewall - Manuelle Konfiguration

### Schritt 1: Windows-Einstellungen öffnen
1. **Windows-Taste + R** drücken
2. `firewall.cpl` eingeben und Enter
3. **Oder:** Windows-Einstellungen → Netzwerk & Internet → Windows-Sicherheit → Firewall & Netzwerkschutz

### Schritt 2: Eingehende Regel erstellen
1. **Erweiterte Einstellungen** klicken
2. **Eingehende Regeln** → **Neue Regel**
3. **Port** auswählen → **Weiter**
4. **TCP** auswählen
5. **Bestimmte lokale Ports:** `8080` eingeben
6. **Verbindung zulassen** → **Weiter**
7. **Alle Profile aktiviert lassen** (Domäne, Privat, Öffentlich)
8. **Name:** `Fachinformatiker Lernapp Port 8080`
9. **Fertig stellen**

### Schritt 3: Regel testen
- Öffne CMD als Administrator
- Teste: `netsh advfirewall firewall show rule name="Fachinformatiker Lernapp Port 8080"`
- Sollte die neue Regel anzeigen

## Alternative: PowerShell (als Administrator)

```powershell
New-NetFirewallRule -DisplayName "Fachinformatiker Lernapp Port 8080" -Direction Inbound -Protocol TCP -LocalPort 8080 -Action Allow
```

## Häufige Probleme

### Problem 1: Antivirus blockiert
- **Lösung:** Lernapp-Ordner in Antivirus-Ausnahmen hinzufügen
- **Pfad:** `C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\`

### Problem 2: Router-Firewall
- **Lösung:** Port 8080 in Router-Einstellungen freigeben
- **Hinweis:** Nur nötig für Zugriff von außerhalb des lokalen Netzwerks

### Problem 3: Unternehmensnetzwerk
- **Lösung:** IT-Administrator kontaktieren
- **Info:** Port 8080 kann von Netzwerk-Richtlinien blockiert werden

## Testen der Firewall-Regel

### Test 1: Localhost
```
http://localhost:8080
```

### Test 2: Lokale IP (im eigenen Netzwerk)
```
http://[IHRE-IP]:8080
```

### Test 3: Von anderem Gerät
- Smartphone/Tablet im gleichen WLAN
- Gleiche IP-Adresse verwenden

## Debug-Befehle

### Alle Firewall-Regeln anzeigen:
```cmd
netsh advfirewall firewall show rule name=all
```

### Port-Status prüfen:
```cmd
netstat -an | findstr :8080
```

### Aktive Verbindungen:
```cmd
netstat -b | findstr :8080
```

---
Erstellt von Hans Hahn - Alle Rechte vorbehalten