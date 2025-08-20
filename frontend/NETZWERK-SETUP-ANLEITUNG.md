# Manuelle Firewall-Konfiguration für Frontend Port 5173

## Schritt 1: Windows Defender Firewall öffnen
1. **Windows-Taste + R** drücken
2. `firewall.cpl` eingeben und Enter drücken
3. **"Erweiterte Einstellungen"** klicken

## Schritt 2: Eingehende Regel für Port 5173 erstellen
1. **"Eingehende Regeln"** in der linken Spalte wählen
2. **"Neue Regel..."** in der rechten Spalte klicken
3. **"Port"** auswählen → **Weiter**
4. **"TCP"** auswählen
5. **"Bestimmte lokale Ports"** → `5173` eingeben
6. **"Verbindung zulassen"** → **Weiter**
7. **Alle drei Häkchen** aktiviert lassen (Domäne, Privat, Öffentlich)
8. **Name:** `Lernapp Frontend Port 5173` eingeben
9. **"Fertig stellen"**

## Schritt 3: PowerShell als Administrator (Alternative)
```powershell
# PowerShell als Administrator öffnen:
New-NetFirewallRule -DisplayName "Lernapp Frontend Port 5173" -Direction Inbound -Protocol TCP -LocalPort 5173 -Action Allow
```

## Schritt 4: Frontend starten
```cmd
cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java\frontend
.\start-10-42-1-117.bat
```

## Ihre URLs nach der Konfiguration:
- **🏠 Lokal:** http://localhost:5173
- **🌐 Netzwerk:** http://10.42.1.117:5173

## Test der Verbindung:
1. **Lokal testen:** Browser → http://localhost:5173
2. **Netzwerk testen:** Anderes Gerät im gleichen WLAN → http://10.42.1.117:5173
3. **Smartphone:** Gleichen WLAN verwenden → http://10.42.1.117:5173

## Häufige Probleme:
- **Frontend lädt nicht:** Node.js/npm nicht im PATH
- **Netzwerk nicht erreichbar:** Firewall blockiert Port 5173
- **Smartphone kann nicht zugreifen:** Verschiedene WLANs oder Router-Firewall

---
Erstellt von Hans Hahn - Alle Rechte vorbehalten