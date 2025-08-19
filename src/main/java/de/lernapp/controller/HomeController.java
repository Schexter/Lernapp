package de.lernapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controller für die Startseite
 * 
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
}
