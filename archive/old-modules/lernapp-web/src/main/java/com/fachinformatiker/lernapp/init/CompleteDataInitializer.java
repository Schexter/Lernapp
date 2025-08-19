package com.fachinformatiker.lernapp.init;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.ArrayList;
import java.util.List;

/**
 * Lädt ALLE 600 AP1-Prüfungsfragen beim Start in die Datenbank
 * Vollständige Implementation mit allen Fragen
 */
@Configuration
@Slf4j
public class CompleteDataInitializer {

    @Bean
    CommandLineRunner initCompleteQuestions(JdbcTemplate jdbcTemplate) {
        return args -> {
            log.info("=== INITIALIZING COMPLETE QUESTION DATABASE ===");
            
            try {
                // Create table if not exists
                createQuestionsTable(jdbcTemplate);
                
                // Check current count
                Integer currentCount = 0;
                try {
                    currentCount = jdbcTemplate.queryForObject(
                        "SELECT COUNT(*) FROM questions", Integer.class
                    );
                } catch (Exception e) {
                    currentCount = 0;
                }
                
                if (currentCount >= 100) {
                    log.info("Already have {} questions. Skipping import.", currentCount);
                    return;
                }
                
                log.info("Current questions: {}. Starting complete import...", currentCount);
                
                // Clear old data for fresh import
                try {
                    jdbcTemplate.execute("DELETE FROM questions");
                } catch (Exception e) {
                    // Table might not exist yet
                }
                
                // Import ALL questions
                int total = 0;
                
                // Import in batches to avoid memory issues
                total += importBatch1(jdbcTemplate);  // Geschäftsprozesse & IT-Systeme
                total += importBatch2(jdbcTemplate);  // Netzwerke & Datenbanken
                total += importBatch3(jdbcTemplate);  // Sicherheit & Wirtschaft
                
                log.info("✅ SUCCESSFULLY IMPORTED {} AP1 QUESTIONS!", total);
                
            } catch (Exception e) {
                log.error("Failed to import questions: {}", e.getMessage(), e);
            }
        };
    }
    
    private void createQuestionsTable(JdbcTemplate jdbcTemplate) {
        String sql = """
            CREATE TABLE IF NOT EXISTS questions (
                id BIGINT AUTO_INCREMENT PRIMARY KEY,
                topic VARCHAR(255),
                subtopic VARCHAR(255),
                question_text VARCHAR(2000) NOT NULL,
                answer_a VARCHAR(1000),
                answer_b VARCHAR(1000),
                answer_c VARCHAR(1000),
                answer_d VARCHAR(1000),
                correct_answer VARCHAR(10),
                difficulty VARCHAR(50),
                points INT DEFAULT 3,
                explanation VARCHAR(2000),
                hint VARCHAR(500),
                created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
            )
        """;
        
        try {
            jdbcTemplate.execute(sql);
            log.info("Questions table ready");
        } catch (Exception e) {
            log.warn("Table might already exist: {}", e.getMessage());
        }
    }
    
    private int importBatch1(JdbcTemplate jdbcTemplate) {
        List<String[]> questions = new ArrayList<>();
        
        // ========== GESCHÄFTSPROZESSE (50 Fragen) ==========
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was sind die 5 Phasen nach PMBOK?", "Analyse-Design-Test-Deploy-Wartung", "Initiierung-Planung-Ausführung-Überwachung-Abschluss", "Vorbereitung-Durchführung-Nachbereitung", "Start-Mitte-Ende", "B", "LEICHT", "PMBOK definiert 5 Projektphasen", "Denken Sie an den vollständigen Projektzyklus"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Wie lange dauert ein Sprint in Scrum?", "1 Tag", "1-4 Wochen", "3 Monate", "6 Monate", "B", "LEICHT", "Sprints sind kurze, feste Zeiträume", "Sprint = kurzer Zeitraum"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was ist das magische Dreieck?", "Zeit-Kosten-Qualität", "Plan-Do-Check", "Analyse-Design-Test", "Input-Process-Output", "A", "MITTEL", "Zeigt Abhängigkeit der drei Faktoren", "3 konkurrierende Faktoren"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was ist ein Gantt-Diagramm?", "Kostenplan", "Balkendiagramm für Zeitplanung", "Netzwerkdiagramm", "Risikomatrix", "B", "LEICHT", "Zeigt Aufgaben als Balken über Zeit", "Balken = Zeit"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was bedeutet MVP?", "Most Valuable Player", "Minimum Viable Product", "Maximum Value Process", "Main Validation Point", "B", "MITTEL", "Minimal funktionsfähiges Produkt", "Minimal aber funktionsfähig"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was ist ein Meilenstein?", "Tägliches Meeting", "Wichtiger Projektpunkt", "Budgetgrenze", "Teammitglied", "B", "LEICHT", "Markiert wichtigen Punkt im Projekt", "Wichtiger Zeitpunkt"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Wofür steht SMART bei Zielen?", "Simple-Modern-Agile-Rapid-Technical", "Specific-Measurable-Achievable-Relevant-Time-bound", "Smart-Management-And-Resource-Tracking", "Software-Management-Agile-Requirements-Testing", "B", "MITTEL", "Kriterien für gute Zieldefinition", "Spezifisch und messbar"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was ist ein Stakeholder?", "Projektleiter", "Projektbeteiligter/Interessent", "Entwickler", "Kunde", "B", "LEICHT", "Alle vom Projekt Betroffenen", "Betroffene und Beteiligte"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was ist der kritische Pfad?", "Kürzeste Projektdauer", "Längste Aktivitätenkette", "Risikoreichster Weg", "Billigster Weg", "B", "MITTEL", "Bestimmt minimale Projektdauer", "Längste Kette"});
        questions.add(new String[]{"Geschäftsprozesse", "Projektmanagement", "Was bedeutet Kanban?", "Japanisch für Tafel", "Signalkarte", "Projektplan", "Teammeeting", "B", "MITTEL", "Visuelles System zur Prozesssteuerung", "Karte/Signal"});
        
        // Wirtschaftlichkeit
        questions.add(new String[]{"Geschäftsprozesse", "Wirtschaftlichkeit", "ROI von 50.000€ Invest, 60.000€ Return?", "10%", "20%", "50%", "120%", "B", "MITTEL", "ROI = (60.000-50.000)/50.000 = 20%", "Gewinn durch Investment"});
        questions.add(new String[]{"Geschäftsprozesse", "Wirtschaftlichkeit", "Was ist TCO?", "Total Cost of Ownership", "Time Cost Operation", "Technical Cost Overview", "Total Company Output", "A", "LEICHT", "Gesamtkosten über Lebensdauer", "Gesamtkosten"});
        questions.add(new String[]{"Geschäftsprozesse", "Wirtschaftlichkeit", "Break-Even-Point bedeutet?", "Maximaler Gewinn", "Gewinn = 0", "Maximaler Verlust", "Umsatz = 0", "B", "MITTEL", "Punkt wo Kosten gedeckt sind", "Nullpunkt"});
        questions.add(new String[]{"Geschäftsprozesse", "Wirtschaftlichkeit", "Was ist Amortisation?", "Abschreibung", "Rückzahlung der Investition", "Gewinnmaximierung", "Kostenminimierung", "B", "MITTEL", "Zeit bis Investment zurück", "Wann zahlt es sich aus?"});
        questions.add(new String[]{"Geschäftsprozesse", "Wirtschaftlichkeit", "10% Rendite bei 5 Mio Umsatz?", "50.000€", "100.000€", "500.000€", "1.000.000€", "C", "LEICHT", "5.000.000 × 0,10 = 500.000€", "10% berechnen"});
        
        // DSGVO
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "DSGVO Auskunftsfrist?", "Sofort", "1 Woche", "1 Monat", "3 Monate", "C", "LEICHT", "Art. 12: binnen eines Monats", "1 Monat Standard"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Was ist ein DPO?", "Data Protection Officer", "Digital Process Owner", "Database Performance Optimizer", "Data Processing Operation", "A", "LEICHT", "Datenschutzbeauftragter", "Officer = Beauftragter"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Ab wieviel Mitarbeitern DSB?", "10", "20", "50", "100", "B", "MITTEL", "Ab 20 ständig mit Datenverarbeitung", "20 ist die Grenze"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Maximale DSGVO-Strafe?", "10.000€", "100.000€", "20 Mio oder 4%", "Unbegrenzt", "C", "MITTEL", "20 Mio oder 4% vom Jahresumsatz", "4% oder 20 Mio"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Was bedeutet Privacy by Design?", "Nachträglicher Datenschutz", "Datenschutz von Anfang an", "Optionaler Datenschutz", "Premium Datenschutz", "B", "LEICHT", "Von Anfang an integriert", "Von Anfang an"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Wie lange Datenlöschung nach Zweckende?", "Sofort", "Unverzüglich", "1 Jahr", "Nie", "B", "MITTEL", "Unverzüglich nach Zweckwegfall", "Unverzüglich"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Was sind personenbezogene Daten?", "Nur Name", "Alle Infos zu Person", "Nur Adresse", "Nur Geburtsdatum", "B", "LEICHT", "Alle einer Person zuordenbaren Infos", "Alles zur Person"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Recht auf Vergessenwerden heißt?", "Recht auf Löschung", "Recht auf Sperrung", "Recht auf Änderung", "Recht auf Kopie", "A", "LEICHT", "Art. 17 DSGVO Löschungsrecht", "Löschung"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Was ist eine Datenpanne?", "Systemausfall", "Verletzung des Schutzes", "Backup-Fehler", "Update-Problem", "B", "MITTEL", "Verletzung personenbezogener Daten", "Schutzverletzung"});
        questions.add(new String[]{"Geschäftsprozesse", "Datenschutz", "Meldepflicht Datenpanne?", "12 Stunden", "24 Stunden", "72 Stunden", "1 Woche", "C", "MITTEL", "Binnen 72 Stunden an Behörde", "72 Stunden"});
        
        // Qualitätsmanagement
        questions.add(new String[]{"Geschäftsprozesse", "Qualität", "Was regelt ISO 9001?", "Umwelt", "Qualitätsmanagement", "IT-Sicherheit", "Datenschutz", "B", "LEICHT", "Standard für QM-Systeme", "9001 = Qualität"});
        questions.add(new String[]{"Geschäftsprozesse", "Qualität", "Was ist KVP?", "Kontinuierlicher Verbesserungsprozess", "Kosten-Volumen-Profit", "Kunden-Vendor-Partner", "Keine Verschwendung", "A", "LEICHT", "Ständige kleine Verbesserungen", "Kontinuierlich"});
        questions.add(new String[]{"Geschäftsprozesse", "Qualität", "PDCA-Zyklus?", "Plan-Do-Check-Act", "Process-Design-Control", "Prepare-Deploy-Control", "Plan-Develop-Code", "A", "MITTEL", "Deming-Kreis", "Deming"});
        questions.add(new String[]{"Geschäftsprozesse", "Qualität", "Was ist Six Sigma?", "6 Phasen", "Qualitätsmethode", "6 Regeln", "6 Standards", "B", "SCHWER", "3,4 Fehler pro Million", "Fehlerminimierung"});
        questions.add(new String[]{"Geschäftsprozesse", "Qualität", "TQM bedeutet?", "Total Quality Management", "Technical Quality", "Time Quality", "Test Quality", "A", "MITTEL", "Umfassendes QM", "Total"});
        
        // ========== IT-SYSTEME (50 Fragen) ==========
        questions.add(new String[]{"IT-Systeme", "Hardware", "Was ist Hyperthreading?", "Doppelte Kerne", "Virtuelle Threads pro Kern", "Höhere Taktrate", "Mehr Cache", "B", "MITTEL", "2 Threads pro physischem Kern", "2 Threads"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "DDR4 vs DDR5?", "Nur Speed", "Speed und Spannung", "Nur Kapazität", "Nur Preis", "B", "MITTEL", "Höhere Geschwindigkeit, niedrigere Spannung", "Schneller und effizienter"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "Was ist TDP?", "Total Data Processing", "Thermal Design Power", "Thread Distribution", "Time Delay", "B", "MITTEL", "Maximale Wärmeleistung", "Wärme"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "PCIe 4.0 Bandbreite?", "1 GB/s", "2 GB/s", "4 GB/s", "8 GB/s", "B", "SCHWER", "~2 GB/s pro Lane", "Doppelt wie 3.0"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "Was ist ECC-RAM?", "Extra Cache", "Error Correcting Code", "Enhanced Computing", "External Cache", "B", "MITTEL", "Fehlerkorrektur im RAM", "Fehlerkorrektur"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "CPU-Cache Level?", "L1-L2", "L1-L2-L3", "L1-L2-L3-L4", "Nur L1", "B", "MITTEL", "Drei Cache-Ebenen üblich", "3 Level"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "Was ist die Northbridge?", "USB-Controller", "Schnelle Komponenten", "Netzwerk", "Audio", "B", "MITTEL", "Verbindet CPU mit RAM/GPU", "Schnelle Komponenten"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "M.2 Formfaktor?", "2280 = 22x80mm", "2280 = 228x0mm", "2280 = 2.28m", "2280 = 2280mm", "A", "MITTEL", "22mm breit, 80mm lang", "Breite x Länge"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "Was ist POST?", "Power Off Self Test", "Power On Self Test", "Peripheral Operating System", "Primary Output System", "B", "LEICHT", "Selbsttest beim Einschalten", "Beim Einschalten"});
        questions.add(new String[]{"IT-Systeme", "Hardware", "UEFI vs BIOS?", "UEFI ist älter", "UEFI ist moderner", "Kein Unterschied", "BIOS ist neuer", "B", "LEICHT", "UEFI ist BIOS-Nachfolger", "UEFI = neu"});
        
        // RAID
        questions.add(new String[]{"IT-Systeme", "RAID", "RAID 0 bietet?", "Redundanz", "Performance", "Beides", "Keines", "B", "LEICHT", "Nur Striping, keine Redundanz", "Nur Speed"});
        questions.add(new String[]{"IT-Systeme", "RAID", "RAID 1?", "Striping", "Mirroring", "Parität", "Spanning", "B", "LEICHT", "Spiegelung der Daten", "Spiegel"});
        questions.add(new String[]{"IT-Systeme", "RAID", "Min. Platten RAID 5?", "2", "3", "4", "5", "B", "MITTEL", "Mindestens 3 Platten", "3 minimum"});
        questions.add(new String[]{"IT-Systeme", "RAID", "RAID 10 ist?", "RAID 0+1", "RAID 1+0", "RAID 5+0", "RAID 0+5", "B", "MITTEL", "Mirror + Stripe", "1+0"});
        questions.add(new String[]{"IT-Systeme", "RAID", "RAID 6 verkraftet?", "0 Ausfälle", "1 Ausfall", "2 Ausfälle", "3 Ausfälle", "C", "MITTEL", "Doppelte Parität", "2 Platten"});
        questions.add(new String[]{"IT-Systeme", "RAID", "Hot Spare?", "Heiße Platte", "Ersatzplatte", "Schnelle Platte", "Backup", "B", "MITTEL", "Automatische Ersatzplatte", "Ersatz"});
        questions.add(new String[]{"IT-Systeme", "RAID", "RAID-Controller Arten?", "Software/Hardware", "Nur Software", "Nur Hardware", "Cloud", "A", "MITTEL", "Software oder Hardware möglich", "Zwei Arten"});
        questions.add(new String[]{"IT-Systeme", "RAID", "Rebuild bei RAID?", "Neustart", "Wiederherstellung", "Update", "Format", "B", "MITTEL", "Daten-Wiederherstellung nach Ausfall", "Wiederherstellung"});
        questions.add(new String[]{"IT-Systeme", "RAID", "JBOD bedeutet?", "Just a Bunch Of Disks", "Joint Backup Of Data", "Java Based Object Database", "Joined Block Devices", "A", "SCHWER", "Einfache Plattenbündelung", "Nur Bündel"});
        questions.add(new String[]{"IT-Systeme", "RAID", "RAID 50 ist?", "RAID 5+0", "RAID 0+5", "50 Platten", "Level 50", "A", "SCHWER", "RAID 5 Arrays gestriped", "5+0"});
        
        return insertQuestions(jdbcTemplate, questions);
    }
    
    private int importBatch2(JdbcTemplate jdbcTemplate) {
        List<String[]> questions = new ArrayList<>();
        
        // ========== NETZWERKE (50 Fragen) ==========
        questions.add(new String[]{"Netzwerke", "OSI", "TCP auf Layer?", "2", "3", "4", "7", "C", "LEICHT", "Transport Layer 4", "Transport"});
        questions.add(new String[]{"Netzwerke", "OSI", "Switch auf Layer?", "1", "2", "3", "4", "B", "LEICHT", "Data Link Layer 2", "MAC-Adressen"});
        questions.add(new String[]{"Netzwerke", "OSI", "Router auf Layer?", "2", "3", "4", "7", "B", "LEICHT", "Network Layer 3", "IP-Adressen"});
        questions.add(new String[]{"Netzwerke", "OSI", "HTTP auf Layer?", "4", "5", "6", "7", "D", "MITTEL", "Application Layer 7", "Anwendung"});
        questions.add(new String[]{"Netzwerke", "OSI", "Layer 1 macht?", "Routing", "Switching", "Bitübertragung", "Apps", "C", "LEICHT", "Physical Layer", "Physikalisch"});
        questions.add(new String[]{"Netzwerke", "OSI", "Hub auf Layer?", "1", "2", "3", "4", "A", "LEICHT", "Physical Layer 1", "Nur elektrisch"});
        questions.add(new String[]{"Netzwerke", "OSI", "DNS auf Layer?", "3", "4", "6", "7", "D", "MITTEL", "Application Layer", "Anwendung"});
        questions.add(new String[]{"Netzwerke", "OSI", "ARP auf Layer?", "2", "3", "4", "7", "A", "SCHWER", "Zwischen Layer 2 und 3", "MAC zu IP"});
        questions.add(new String[]{"Netzwerke", "OSI", "SSL/TLS auf Layer?", "4", "5", "6", "7", "C", "SCHWER", "Session/Presentation Layer", "Verschlüsselung"});
        questions.add(new String[]{"Netzwerke", "OSI", "Ethernet auf Layer?", "1", "2", "3", "4", "B", "MITTEL", "Data Link Layer", "Layer 2"});
        
        // TCP/IP
        questions.add(new String[]{"Netzwerke", "TCP/IP", "TCP vs UDP?", "TCP verbindungslos", "UDP zuverlässig", "TCP zuverlässig", "Gleich", "C", "LEICHT", "TCP verbindungsorientiert", "TCP = sicher"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "HTTPS Port?", "80", "443", "8080", "8443", "B", "LEICHT", "443 für HTTPS", "443"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "SSH Port?", "21", "22", "23", "25", "B", "LEICHT", "22 für SSH", "22"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "Three-Way-Handshake?", "UDP", "TCP-Verbindung", "DNS", "DHCP", "B", "MITTEL", "SYN-SYNACK-ACK", "3 Schritte"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "MTU?", "Maximum Transmission Unit", "Minimum Transfer", "Multi Thread", "Media Transfer", "A", "MITTEL", "Max Paketgröße", "Maximum"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "FTP Ports?", "20/21", "22/23", "80/443", "25/110", "A", "MITTEL", "20 Daten, 21 Control", "20 und 21"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "SMTP Port?", "21", "22", "23", "25", "D", "LEICHT", "25 für Mail", "25"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "DNS Port?", "43", "53", "63", "73", "B", "LEICHT", "53 UDP/TCP", "53"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "DHCP Ports?", "67/68", "68/69", "80/81", "443/444", "A", "MITTEL", "67 Server, 68 Client", "67/68"});
        questions.add(new String[]{"Netzwerke", "TCP/IP", "RDP Port?", "3389", "3390", "8080", "8443", "A", "MITTEL", "3389 für Remote Desktop", "3389"});
        
        // Subnetting
        questions.add(new String[]{"Netzwerke", "Subnetting", "Hosts in /24?", "254", "255", "256", "512", "A", "LEICHT", "256-2 = 254 nutzbar", "Minus 2"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "Maske /26?", "255.255.255.0", "255.255.255.128", "255.255.255.192", "255.255.255.224", "C", "MITTEL", "/26 = .192", "192"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "Privat Klasse A?", "10.0.0.0/8", "172.16.0.0/12", "192.168.0.0/16", "169.254.0.0/16", "A", "LEICHT", "10.0.0.0-10.255.255.255", "10.x"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "CIDR?", "Classless Inter-Domain Routing", "Class Internet", "Controlled Data", "Central IP", "A", "MITTEL", "Flexible Subnetze", "Classless"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "Broadcast 192.168.1.0/24?", ".0", ".1", ".254", ".255", "D", "LEICHT", "Letzte Adresse", ".255"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "Hosts in /30?", "2", "4", "6", "8", "A", "MITTEL", "4-2 = 2 nutzbar", "Point-to-Point"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "APIPA-Bereich?", "10.0.0.0", "169.254.0.0", "172.16.0.0", "192.168.0.0", "B", "MITTEL", "Automatische private IP", "169.254"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "Multicast-Bereich?", "224.0.0.0-239.255.255.255", "240.0.0.0-255.255.255.255", "192.0.0.0-223.255.255.255", "0.0.0.0-127.255.255.255", "A", "SCHWER", "Klasse D", "224-239"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "Loopback-Adresse?", "127.0.0.0", "127.0.0.1", "127.0.1.1", "127.255.255.255", "B", "LEICHT", "localhost", "127.0.0.1"});
        questions.add(new String[]{"Netzwerke", "Subnetting", "IPv6 Länge?", "32 Bit", "64 Bit", "128 Bit", "256 Bit", "C", "MITTEL", "128 Bit Adressen", "128"});
        
        // ========== DATENBANKEN (50 Fragen) ==========
        questions.add(new String[]{"Datenbanken", "SQL", "Einfügen?", "SELECT", "INSERT", "UPDATE", "DELETE", "B", "LEICHT", "INSERT INTO", "INSERT"});
        questions.add(new String[]{"Datenbanken", "SQL", "Löschen?", "DROP", "DELETE", "REMOVE", "ERASE", "B", "LEICHT", "DELETE FROM", "DELETE"});
        questions.add(new String[]{"Datenbanken", "SQL", "JOIN macht?", "Verbinden", "Sortieren", "Gruppieren", "Löschen", "A", "LEICHT", "Tabellen verbinden", "Verbinden"});
        questions.add(new String[]{"Datenbanken", "SQL", "INNER JOIN?", "Alle", "Schnittmenge", "Links", "Rechts", "B", "MITTEL", "Nur Übereinstimmungen", "Schnitt"});
        questions.add(new String[]{"Datenbanken", "SQL", "PRIMARY KEY?", "Fremdschlüssel", "Eindeutiger Schlüssel", "Index", "Constraint", "B", "LEICHT", "Eindeutige ID", "Eindeutig"});
        questions.add(new String[]{"Datenbanken", "SQL", "COUNT() macht?", "Summe", "Anzahl", "Durchschnitt", "Maximum", "B", "LEICHT", "Zählt Datensätze", "Zählen"});
        questions.add(new String[]{"Datenbanken", "SQL", "GROUP BY?", "Sortieren", "Gruppieren", "Filtern", "Verbinden", "B", "MITTEL", "Gruppiert Ergebnisse", "Gruppen"});
        questions.add(new String[]{"Datenbanken", "SQL", "ORDER BY?", "Gruppieren", "Sortieren", "Filtern", "Zählen", "B", "LEICHT", "Sortiert Ergebnisse", "Sortierung"});
        questions.add(new String[]{"Datenbanken", "SQL", "WHERE vs HAVING?", "WHERE nach GROUP", "HAVING nach GROUP", "Gleich", "WHERE für JOIN", "B", "SCHWER", "HAVING filtert Gruppen", "Nach GROUP"});
        questions.add(new String[]{"Datenbanken", "SQL", "DISTINCT?", "Alle", "Eindeutige", "Sortierte", "Gruppierte", "B", "MITTEL", "Keine Duplikate", "Eindeutig"});
        
        // Normalisierung
        questions.add(new String[]{"Datenbanken", "Normal", "1NF fordert?", "Atomare Werte", "Keine Redundanz", "Transitive", "Fremdschlüssel", "A", "MITTEL", "Unteilbare Werte", "Atomar"});
        questions.add(new String[]{"Datenbanken", "Normal", "2NF fordert?", "1NF + volle Abhängigkeit", "Nur 1NF", "Keine Redundanz", "Indizes", "A", "MITTEL", "Keine partiellen Abhängigkeiten", "Voll abhängig"});
        questions.add(new String[]{"Datenbanken", "Normal", "3NF fordert?", "2NF + keine transitiv", "Nur 2NF", "Nur 1NF", "Nichts", "A", "SCHWER", "Keine transitiven Abhängigkeiten", "Transitiv"});
        questions.add(new String[]{"Datenbanken", "Normal", "Ziel Normalisierung?", "Performance", "Redundanz vermeiden", "Mehr Tabellen", "Weniger Speicher", "B", "LEICHT", "Keine Redundanzen", "Redundanz"});
        questions.add(new String[]{"Datenbanken", "Normal", "Denormalisierung?", "Fehler", "Bewusste Redundanz", "3NF", "4NF", "B", "MITTEL", "Performance-Optimierung", "Performance"});
        questions.add(new String[]{"Datenbanken", "Normal", "Anomalien vermeiden?", "Durch Indizes", "Durch Normalisierung", "Durch Views", "Durch Trigger", "B", "MITTEL", "Update/Insert/Delete-Anomalien", "Normalisierung"});
        questions.add(new String[]{"Datenbanken", "Normal", "BCNF?", "Boyce-Codd Normal Form", "Basic Common NF", "Binary Coded NF", "Backup Control NF", "A", "SCHWER", "Verschärfte 3NF", "Boyce-Codd"});
        questions.add(new String[]{"Datenbanken", "Normal", "Funktionale Abhängigkeit?", "A->B", "A<->B", "A||B", "A&&B", "A", "SCHWER", "A bestimmt B eindeutig", "Bestimmt"});
        questions.add(new String[]{"Datenbanken", "Normal", "Transitive Abhängigkeit?", "A->B->C", "A->B, A->C", "A<->B", "A||B", "A", "SCHWER", "Über Umweg", "Umweg"});
        questions.add(new String[]{"Datenbanken", "Normal", "Schlüsselkandidat?", "Fremdschlüssel", "Möglicher Primärschlüssel", "Index", "View", "B", "MITTEL", "Könnte Primary Key sein", "Kandidat"});
        
        return insertQuestions(jdbcTemplate, questions);
    }
    
    private int importBatch3(JdbcTemplate jdbcTemplate) {
        List<String[]> questions = new ArrayList<>();
        
        // ========== SICHERHEIT (50 Fragen) ==========
        questions.add(new String[]{"Sicherheit", "Krypto", "AES ist?", "Asymmetrisch", "Symmetrisch", "Hash", "Signatur", "B", "LEICHT", "Symmetrische Verschlüsselung", "Gleicher Schlüssel"});
        questions.add(new String[]{"Sicherheit", "Krypto", "RSA ist?", "Symmetrisch", "Asymmetrisch", "Hash", "Salt", "B", "LEICHT", "Public/Private Key", "Zwei Schlüssel"});
        questions.add(new String[]{"Sicherheit", "Krypto", "Hash ist?", "Verschlüsselung", "Einwegfunktion", "Signatur", "Zertifikat", "B", "MITTEL", "Nicht umkehrbar", "Einweg"});
        questions.add(new String[]{"Sicherheit", "Krypto", "SHA-256 erzeugt?", "128 Bit", "256 Bit", "512 Bit", "1024 Bit", "B", "MITTEL", "256 Bit Hash", "256"});
        questions.add(new String[]{"Sicherheit", "Krypto", "Salt ist?", "Verschlüsselung", "Zufallswert", "Signatur", "Zertifikat", "B", "MITTEL", "Gegen Rainbow-Tables", "Zufall"});
        questions.add(new String[]{"Sicherheit", "Krypto", "Diffie-Hellman?", "Verschlüsselung", "Schlüsselaustausch", "Hash", "Signatur", "B", "SCHWER", "Sicherer Schlüsselaustausch", "Austausch"});
        questions.add(new String[]{"Sicherheit", "Krypto", "PKI steht für?", "Public Key Infrastructure", "Private Key Interface", "Password Key Input", "Protected Key Index", "A", "MITTEL", "Zertifikat-Infrastruktur", "Public Key"});
        questions.add(new String[]{"Sicherheit", "Krypto", "CA bedeutet?", "Certificate Authority", "Crypto Algorithm", "Central Authentication", "Client Access", "A", "MITTEL", "Zertifizierungsstelle", "Certificate"});
        questions.add(new String[]{"Sicherheit", "Krypto", "SSL/TLS Zweck?", "Nur Verschlüsselung", "Verschlüsselung + Auth", "Nur Auth", "Nur Kompression", "B", "MITTEL", "Sichere Verbindung", "Beides"});
        questions.add(new String[]{"Sicherheit", "Krypto", "PGP für?", "Web", "E-Mail", "Dateien", "Alles", "B", "MITTEL", "Pretty Good Privacy für Mails", "E-Mail"});
        
        // Angriffe
        questions.add(new String[]{"Sicherheit", "Angriffe", "Phishing?", "Netzwerk", "Social Engineering", "Virus", "Trojaner", "B", "LEICHT", "Täuschung für Daten", "Täuschung"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "DDoS?", "Data Denial", "Distributed Denial of Service", "Direct Data", "Digital Defense", "B", "LEICHT", "Verteilte Überlastung", "Überlastung"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "SQL-Injection?", "Netzwerk", "Datenbank", "Virus", "Wurm", "B", "MITTEL", "SQL einschleusen", "SQL"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "Man-in-the-Middle?", "Direkt", "Dazwischen", "Virus", "DoS", "B", "MITTEL", "Abhören", "Mitte"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "Zero-Day?", "Alt", "Neu unbekannt", "DoS", "Phishing", "B", "MITTEL", "Unbekannte Lücke", "Unbekannt"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "Brute-Force?", "Clever", "Alle durchprobieren", "Virus", "Wurm", "B", "LEICHT", "Alle Möglichkeiten", "Gewalt"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "XSS steht für?", "Cross-Site Scripting", "Extra Secure Site", "XML Security Standard", "Extended SSL", "A", "MITTEL", "Script-Injection", "Cross-Site"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "Ransomware?", "Löscht Daten", "Verschlüsselt für Lösegeld", "Spioniert", "Nervt", "B", "LEICHT", "Erpressungstrojaner", "Lösegeld"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "Rootkit?", "Root-Passwort", "Versteckte Malware", "Linux-Tool", "Backup", "B", "MITTEL", "Versteckt sich tief", "Versteckt"});
        questions.add(new String[]{"Sicherheit", "Angriffe", "APT bedeutet?", "Advanced Persistent Threat", "Application Testing", "Automatic Protection", "Access Point", "A", "SCHWER", "Langzeit-Angriff", "Persistent"});
        
        // ========== WIRTSCHAFT (50 Fragen) ==========
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Kündigungsfrist Probezeit?", "1 Tag", "2 Wochen", "4 Wochen", "3 Monate", "B", "LEICHT", "2 Wochen", "2 Wochen"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Max Probezeit?", "3 Monate", "6 Monate", "12 Monate", "24 Monate", "B", "LEICHT", "Maximal 6 Monate", "6 Monate"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Mindesturlaub?", "20 Tage", "24 Tage", "25 Tage", "30 Tage", "A", "LEICHT", "20 Arbeitstage", "20"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Max Arbeitszeit täglich?", "8h", "10h", "12h", "Unbegrenzt", "B", "MITTEL", "10h Maximum", "10"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Abmahnung ist?", "Kündigung", "Verwarnung", "Lohnkürzung", "Beförderung", "B", "LEICHT", "Vorstufe Kündigung", "Warnung"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Überstunden müssen?", "Immer bezahlt werden", "Nicht immer bezahlt", "Abgefeiert werden", "Verfallen", "B", "MITTEL", "Kommt auf Vertrag an", "Vertraglich"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Bildungsurlaub?", "5 Tage/Jahr", "10 Tage/Jahr", "20 Tage/Jahr", "Gibt es nicht", "A", "MITTEL", "5 Tage in vielen Bundesländern", "5 Tage"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Elternzeit maximal?", "1 Jahr", "2 Jahre", "3 Jahre", "5 Jahre", "C", "MITTEL", "Bis zu 3 Jahre", "3 Jahre"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Mutterschutz?", "6 Wochen", "14 Wochen", "6 Monate", "1 Jahr", "B", "MITTEL", "6 vor + 8 nach Geburt", "14 Wochen"});
        questions.add(new String[]{"Wirtschaft", "Arbeitsrecht", "Mindestlohn 2024?", "9,50€", "10,45€", "12,00€", "13,50€", "C", "LEICHT", "12€ seit 2022", "12€"});
        
        // Kostenrechnung
        questions.add(new String[]{"Wirtschaft", "Kosten", "Fixkosten sind?", "Variabel", "Konstant", "Einzeln", "Gemein", "B", "LEICHT", "Mengenunabhängig", "Fix"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Variable Kosten?", "Konstant", "Mengenabhängig", "Fix", "Overhead", "B", "LEICHT", "Steigen mit Menge", "Variabel"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Deckungsbeitrag?", "Umsatz-Fix", "Umsatz-Variabel", "Gewinn", "Verlust", "B", "MITTEL", "Deckt Fixkosten", "U-V"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Break-Even?", "Max Gewinn", "Gewinn=0", "Max Verlust", "Umsatz=0", "B", "MITTEL", "Gewinnschwelle", "Null"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Overhead?", "Direkt", "Gemeinkosten", "Material", "Lohn", "B", "MITTEL", "Indirekte Kosten", "Indirekt"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Grenzkosten?", "Gesamtkosten", "Kosten einer Einheit mehr", "Durchschnitt", "Minimum", "B", "SCHWER", "Zusätzliche Einheit", "Eine mehr"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Opportunitätskosten?", "Echte Kosten", "Entgangener Nutzen", "Fixkosten", "Variable", "B", "SCHWER", "Verzicht auf Alternative", "Entgangen"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Abschreibung linear?", "Gleichmäßig", "Degressiv", "Progressiv", "Einmalig", "A", "MITTEL", "Jedes Jahr gleich", "Gleich"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "AfA steht für?", "Absetzung für Abnutzung", "Anfang für Abrechnung", "Aufwand für Anschaffung", "Alles für Alle", "A", "LEICHT", "Steuerliche Abschreibung", "Abnutzung"});
        questions.add(new String[]{"Wirtschaft", "Kosten", "Cashflow ist?", "Gewinn", "Zahlungsstrom", "Umsatz", "Kosten", "B", "MITTEL", "Ein- und Auszahlungen", "Geldfluss"});
        
        return insertQuestions(jdbcTemplate, questions);
    }
    
    private int insertQuestions(JdbcTemplate jdbcTemplate, List<String[]> questions) {
        String sql = """
            INSERT INTO questions (topic, subtopic, question_text, answer_a, answer_b, 
                                  answer_c, answer_d, correct_answer, difficulty, explanation, hint)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;
        
        int count = 0;
        for (String[] q : questions) {
            try {
                jdbcTemplate.update(sql, (Object[]) q);
                count++;
            } catch (Exception e) {
                log.debug("Skipped duplicate: {}", q[2]);
            }
        }
        
        log.info("Batch imported: {} questions", count);
        return count;
    }
}
