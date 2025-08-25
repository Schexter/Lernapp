package de.lernapp.controller;

import de.lernapp.service.MassiveCsvImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/import")
@CrossOrigin(origins = "http://localhost:5173")
public class ImportController {
    
    @Autowired
    private MassiveCsvImportService importService;
    
    @PostMapping("/csv")
    public ResponseEntity<Map<String, String>> importAllCsvFiles() {
        Map<String, String> response = new HashMap<>();
        
        try {
            importService.importAllCsvFiles();
            response.put("status", "success");
            response.put("message", "All CSV files imported successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("status", "error");
            response.put("message", "Error importing CSV files: " + e.getMessage());
            return ResponseEntity.status(500).body(response);
        }
    }
}