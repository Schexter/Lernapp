package de.lernapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    
    @GetMapping("/")
    public String home() {
        return "home";  // Zeigt das Dashboard
    }
    
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    @GetMapping("/register")
    public String register() {
        return "register";
    }
    
    @GetMapping("/lernen")
    public String lernen() {
        return "lernen";
    }
    
    @GetMapping("/uebungen")
    public String uebungen() {
        return "uebungen";
    }
    
    @GetMapping("/pruefungen")
    public String pruefungen() {
        return "pruefungen";
    }
    
    @GetMapping("/fortschritt")
    public String fortschritt() {
        return "fortschritt";
    }
}
