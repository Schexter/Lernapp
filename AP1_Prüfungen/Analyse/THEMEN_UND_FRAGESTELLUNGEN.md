# 🎯 THEMEN & FRAGESTELLUNGEN - KOMPAKTE ÜBERSICHT
Erstellt von Hans Hahn - Alle Rechte vorbehalten
Datum: 20.08.2025

## 📊 KERNERKENNTNISSE AUS UPLOAD-ANALYSE

### 🔥 AP1-PRÜFUNG - KRITISCHE THEMEN (Basis: LERNFOKUS_60_PROZENT_STRATEGIE.md)

#### **NETZPLANTECHNIK** - 25 Punkte (Höchste Priorität)
**Fragestellungen:**
- Projektmerkmale definieren (4P)
- SMART-Kriterien erklären (4P) 
- Netzplan vervollständigen (14P)
- Kritischen Pfad markieren (1P)
- Projektverzögerung analysieren (2P)

**Erfolgsformeln:**
```
FAZ = MAX(Vorgänger-FEZ)
FEZ = FAZ + Dauer
SEZ = MIN(Nachfolger-SAZ)
SAZ = SEZ - Dauer
GP = SAZ - FAZ (Kritischer Pfad = GP=0)
```

#### **IPv6-GRUNDLAGEN** - NEU! Ersetzt RAID
**Fragestellungen:**
- 128-Bit Adresslänge erkennen
- Hexadezimalschreibweise (0-9, A-F)
- Präfixlänge bestimmen (/64 = erste 64 Bit)
- Link-local Adressen identifizieren (fe80::)

#### **POWERSHELL-DEBUGGING** - 8 Punkte sicher
**Häufige Fehlertypen:**
- `-gt/-lt` Verwechslung
- `*1000` statt `*100` bei Prozent
- If-Then-Else Logik
- Variablen ohne `$`

#### **OSI-SCHICHTENMODELL** - NEU! Sehr wichtig
**Merksatz:** "Alle Priester Saufen Tequila Nach Der Predigt"
- MAC-Adresse → Schicht 2 (Data Link)
- IP-Adresse → Schicht 3 (Network) 
- TCP → Schicht 4 (Transport)

#### **CIA-TRIAD & DATENSCHUTZ** - 24 Punkte
**CIA-Zuordnung:**
- **Confidentiality**: Verschlüsselung, Zugriffskontrolle
- **Integrity**: Checksummen, digitale Signaturen  
- **Availability**: Backup, Redundanz

---

### ☁️ AZURE AZ-104 - KERNTHEMEN (Basis: AZ104_LEGALER_LERNPLAN.md)

#### **PRÜFUNGSGEWICHTUNG:**
- RBAC & Azure AD (20%)
- Networking (20%) 
- VMs & Storage (20%)
- Monitoring (15%)
- Backup & DR (15%)
- Governance (10%)

#### **MUST-KNOW THEMEN:**
```
Woche 1-2: Subscriptions, Resource Groups, RBAC
Woche 3-4: VNets, NSGs, Load Balancer, VPN Gateway
Woche 5-6: VMs, Scale Sets, Storage Accounts, Backup
Woche 7: Azure Monitor, Log Analytics, Alerts
Woche 8: Practice Tests & Prüfung
```

---

### 🔒 SECURITY BASICS - SOFORT ANWENDBAR (Basis: SECURITY_BASICS_HANSHAHN.md)

#### **IMMEDIATE SECURITY WINS:**
```python
# Password Hashing (NIEMALS Klartext!)
import bcrypt
salt = bcrypt.gensalt()
hashed = bcrypt.hashpw(password.encode('utf-8'), salt)

# SQL Injection verhindern
cursor.execute("SELECT * FROM users WHERE name = ?", (user_input,))

# Input Validation
dangerous_chars = ['<', '>', '"', "'", '&', '/', '\\']
```

#### **SECURITY LERNPLAN (30 Tage):**
```
Woche 1: OWASP Top 10, SQL Injection, XSS
Woche 2: Netzwerk-Basics, Wireshark, HTTP/HTTPS
Woche 3: Burp Suite, Web App Testing, Password Cracking
Woche 4: Metasploit Basics, CTF Challenges, Portfolio-Audit
```

---

### 💻 JAVA/SPRING BOOT - PROJEKT STATUS (Basis: TODO.md)

#### **AKTUELLER STAND:**
- ✅ Backend: 100% (Spring Boot, H2 DB, REST APIs)
- ✅ Frontend: 60% (React, TypeScript, TailwindCSS)
- 🔴 Authentication: 0% (JWT, User Management)
- 🔴 Lernfortschritt: 0% (Progress Tracking)

#### **4-WOCHEN ROADMAP:**
```
Woche 1: User Authentication (JWT, Login/Register)
Woche 2: Lernfortschritt-Tracking (Progress, Spaced Repetition)  
Woche 3: Prüfungsmodus (Timer, Randomization, Results)
Woche 4: Polish & Deployment (Testing, Docs, Production)
```

---

### 🎯 WEITERE IDENTIFIZIERTE THEMEN:

#### **ENERGIEBERECHNUNGEN** - 25 Punkte
```
Effizienz = Output / Input × 100%
80Plus Standard: 80-92% je nach Level
Amortisationszeit = Mehrkosten / monatliche Einsparung
```

#### **PLY-DATEIFORMATE** - VÖLLIG NEU!
```
PLY = Polygon File Format (3D-Geometrie)
Speicher = Punkte × (3×4 Byte + 3×1 Byte) = Punkte × 15 Byte
```

#### **ANSCHLUSSTECHNIK** - NEU!
- HDMI: Audio + Video + HDCP
- USB-C: Universal + Power Delivery 100W
- DisplayPort: Hohe Auflösung + Daisy Chain

---

## ⏰ OPTIMALER TAGESRHYTHMUS

### **WISSENSCHAFTLICH OPTIMIERT (Basis: ULTIMATIVER_LERNPLAN_HANSHAHN.md):**

```
06:00-07:00: THEORIE (Gehirn ausgeruht)
  → AP1 neue Konzepte
  → Formeln & Definitionen

12:00-12:30: WIEDERHOLUNG (Kurze Sessions)
  → Karteikarten
  → Azure Microsoft Learn

19:00-21:00: PRAXIS (Kreativität höher)
  → Programmieren/Hands-on
  → Labs & Experimente

21:00-21:30: DOKUMENTATION
  → Fehler dokumentieren
  → Fortschritt reviewen
```

---

## 🚀 QUICK-START AKTIONSPLAN

### **HEUTE (30 Min):**
```
□ Lernapp starten: cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java && npm run dev
□ 1 Netzplan aus AP1-Prüfung rechnen (15 Min)
□ Azure Free Account aktivieren
□ TryHackMe Account erstellen
□ Diesen Plan ausdrucken & sichtbar aufhängen
```

### **DIESE WOCHE:**
```
Mo: Netzplan-Formeln wiederholen + User Authentication Backend
Di: IPv6-Grundlagen + JWT Implementation  
Mi: PowerShell-Debugging + Frontend Integration
Do: OSI-Modell + Testing & Debugging
Fr: Prüfungssimulation + Feature-Release
Sa: Azure VNet Lab + Security TryHackMe Room
So: Woche reviewen + Nächste Woche planen
```

---

## 📊 ERFOLGSKENNZAHLEN

### **WÖCHENTLICHE ZIELE:**
```
AP1: 5 Netzpläne + 1 Simulation >50 Punkte
Java: 5 Features + 10 Tests + 100+ Zeilen Code  
Azure: 3 Learn Module + 2 Labs + 1 Practice Test
Security: 1 TryHackMe Room + 2 Bugs gefixed
```

### **MONTHLY MILESTONES:**
```
Monat 1: ✓ AP1 bestanden + ✓ Lernapp 90% fertig
Monat 2: ✓ AZ-104 bestanden + ✓ Security Basics
Monat 3: ✓ Portfolio erweitert + ✓ Job Ready
```

---

## 💡 ANTI-PROKRASTINATION

### **2-MINUTEN-REGEL:**
Wenn etwas < 2 Min dauert → SOFORT machen
- Git commit, Code-Kommentar, Kurze Doku

### **NOTFALLPLAN BEI BLOCKADE:**
1. 🟢 Pomodoro (25 Min + 5 Min Pause)
2. 🟡 Scope reduzieren (1 kleines Thema)  
3. 🔴 Pause machen (1 Tag ist OK)
4. 🆘 Hilfe suchen (Community, Mentor)

---

**ERFOLGSMOTTO:**
*"Konsistenz schlägt Intensität - Jeden Tag 1% besser!"*

**SOFORTIGER NÄCHSTER SCHRITT:**
*PowerShell öffnen → cd C:\SoftwareEntwicklung\Fachinformatiker_Lernapp_Java → npm run dev*

---

*Erstellt von Hans Hahn - Alle Rechte vorbehalten*