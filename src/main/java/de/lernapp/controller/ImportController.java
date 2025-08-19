package de.lernapp.controller;

import de.lernapp.service.CsvImportService;
import de.lernapp.service.CsvImportService.ImportResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * REST Controller für den Import von Prüfungsfragen
 * Bietet Endpoints für CSV-Import und Verwaltung
 * 
 * @author Hans Hahn
 */
@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "*")
public class ImportController {
    
    private static final Logger logger = LoggerFactory.getLogger(ImportController.class);
    
    @Autowired
    private CsvImportService csvImportService;
    
    /**
     * Importiert eine hochgeladene CSV-Datei
     */
    @PostMapping("/csv")
    public ResponseEntity<Map<String, Object>> importCSVFile(
            @RequestParam("file") MultipartFile file) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Validierung
        if (file.isEmpty()) {
            response.put("success", false);
            response.put("message", "Keine Datei ausgewählt");
            return ResponseEntity.badRequest().body(response);
        }
        
        // Prüfe Dateityp
        String fileName = file.getOriginalFilename();
        if (fileName == null || !fileName.toLowerCase().endsWith(".csv")) {
            response.put("success", false);
            response.put("message", "Nur CSV-Dateien sind erlaubt");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            logger.info("Starte CSV-Import für Datei: {}", fileName);
            
            ImportResult result = csvImportService.importFromUploadedFile(file);
            
            response.put("success", true);
            response.put("message", String.format(
                "Import erfolgreich! %d von %d Fragen importiert", 
                result.getSuccessfulImports(), 
                result.getTotalProcessed()
            ));
            response.put("statistics", createStatisticsMap(result));
            
            if (!result.getErrorMessages().isEmpty()) {
                response.put("warnings", result.getErrorMessages());
            }
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Fehler beim CSV-Import", e);
            response.put("success", false);
            response.put("message", "Import fehlgeschlagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Importiert alle AP1-Fragen aus dem lokalen Verzeichnis
     * Dieser Endpoint ist nur für Administratoren gedacht
     */
    @PostMapping("/ap1/all")
    public ResponseEntity<Map<String, Object>> importAllAP1Questions() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            logger.info("🚀 Starte Import aller AP1-Fragen...");
            
            ImportResult result = csvImportService.importAllAP1Questions();
            
            response.put("success", true);
            response.put("message", String.format(
                "✅ Gesamt-Import abgeschlossen! %d Fragen erfolgreich importiert", 
                result.getSuccessfulImports()
            ));
            response.put("statistics", createStatisticsMap(result));
            
            if (!result.getErrorMessages().isEmpty()) {
                response.put("warnings", result.getErrorMessages());
            }
            
            // Füge detaillierte Statistiken hinzu
            String stats = csvImportService.getImportStatistics();
            response.put("detailedStatistics", stats);
            
            logger.info("✅ AP1-Import erfolgreich abgeschlossen");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("❌ Fehler beim AP1-Import", e);
            response.put("success", false);
            response.put("message", "Import fehlgeschlagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Importiert eine spezifische lokale CSV-Datei
     * Pfad relativ zum data-Verzeichnis
     */
    @PostMapping("/local")
    public ResponseEntity<Map<String, Object>> importLocalFile(
            @RequestParam("path") String relativePath) {
        
        Map<String, Object> response = new HashMap<>();
        
        // Sicherheitscheck - verhindere Zugriff außerhalb des data-Verzeichnisses
        if (relativePath.contains("..") || relativePath.contains(":")) {
            response.put("success", false);
            response.put("message", "Ungültiger Dateipfad");
            return ResponseEntity.badRequest().body(response);
        }
        
        String fullPath = "C:/SoftwareEntwicklung/Fachinformatiker_Lernapp_Java/data/" + relativePath;
        
        try {
            logger.info("Importiere lokale Datei: {}", relativePath);
            
            ImportResult result = csvImportService.importFromLocalFile(fullPath);
            
            response.put("success", true);
            response.put("message", String.format(
                "Import von %s erfolgreich! %d Fragen importiert", 
                relativePath,
                result.getSuccessfulImports()
            ));
            response.put("statistics", createStatisticsMap(result));
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Fehler beim Import der lokalen Datei", e);
            response.put("success", false);
            response.put("message", "Import fehlgeschlagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Gibt Import-Statistiken zurück
     */
    @GetMapping("/statistics")
    public ResponseEntity<Map<String, Object>> getStatistics() {
        Map<String, Object> response = new HashMap<>();
        
        try {
            String stats = csvImportService.getImportStatistics();
            response.put("success", true);
            response.put("statistics", stats);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Fehler beim Abrufen der Statistiken", e);
            response.put("success", false);
            response.put("message", "Fehler: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * GEFÄHRLICH: Löscht alle Fragen (nur für Development/Testing)
     */
    @DeleteMapping("/questions/all")
    public ResponseEntity<Map<String, Object>> deleteAllQuestions(
            @RequestParam(value = "confirm", defaultValue = "false") boolean confirm) {
        
        Map<String, Object> response = new HashMap<>();
        
        if (!confirm) {
            response.put("success", false);
            response.put("message", "Löschvorgang nicht bestätigt. Setze Parameter 'confirm=true'");
            return ResponseEntity.badRequest().body(response);
        }
        
        try {
            logger.warn("⚠️ LÖSCHE ALLE FRAGEN AUS DER DATENBANK!");
            csvImportService.deleteAllQuestions();
            
            response.put("success", true);
            response.put("message", "Alle Fragen wurden gelöscht");
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Fehler beim Löschen der Fragen", e);
            response.put("success", false);
            response.put("message", "Löschvorgang fehlgeschlagen: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Erstellt eine Map mit Import-Statistiken
     */
    private Map<String, Object> createStatisticsMap(ImportResult result) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalProcessed", result.getTotalProcessed());
        stats.put("successfulImports", result.getSuccessfulImports());
        stats.put("duplicatesSkipped", result.getDuplicatesSkipped());
        stats.put("errorsOccurred", result.getErrorsOccurred());
        
        // Berechne Erfolgsrate
        if (result.getTotalProcessed() > 0) {
            double successRate = (double) result.getSuccessfulImports() / result.getTotalProcessed() * 100;
            stats.put("successRate", String.format("%.1f%%", successRate));
        }
        
        return stats;
    }
}

// Erstellt von Hans Hahn - Alle Rechte vorbehalten