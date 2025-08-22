package de.lernapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.info.BuildProperties;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Health Check Controller für Monitoring und Deployment
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {
    
    @Autowired(required = false)
    private BuildProperties buildProperties;
    
    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        Map<String, Object> status = new HashMap<>();
        status.put("status", "UP");
        status.put("timestamp", LocalDateTime.now());
        status.put("service", "Fachinformatiker Lernapp");
        
        // Version Information wenn verfügbar
        if (buildProperties != null) {
            status.put("version", buildProperties.getVersion());
            status.put("name", buildProperties.getName());
        } else {
            status.put("version", "1.0.0");
        }
        
        // System Information
        Map<String, Object> system = new HashMap<>();
        system.put("java.version", System.getProperty("java.version"));
        system.put("java.vendor", System.getProperty("java.vendor"));
        system.put("os.name", System.getProperty("os.name"));
        system.put("os.arch", System.getProperty("os.arch"));
        status.put("system", system);
        
        // Memory Information
        Runtime runtime = Runtime.getRuntime();
        Map<String, Object> memory = new HashMap<>();
        memory.put("total", runtime.totalMemory() / 1024 / 1024 + " MB");
        memory.put("free", runtime.freeMemory() / 1024 / 1024 + " MB");
        memory.put("used", (runtime.totalMemory() - runtime.freeMemory()) / 1024 / 1024 + " MB");
        memory.put("max", runtime.maxMemory() / 1024 / 1024 + " MB");
        status.put("memory", memory);
        
        return ResponseEntity.ok(status);
    }
    
    @GetMapping("/ping")
    public ResponseEntity<String> ping() {
        return ResponseEntity.ok("pong");
    }
    
    @GetMapping("/ready")
    public ResponseEntity<Map<String, Object>> ready() {
        Map<String, Object> readiness = new HashMap<>();
        readiness.put("ready", true);
        readiness.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(readiness);
    }
    
    @GetMapping("/live")
    public ResponseEntity<Map<String, Object>> live() {
        Map<String, Object> liveness = new HashMap<>();
        liveness.put("live", true);
        liveness.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(liveness);
    }
}