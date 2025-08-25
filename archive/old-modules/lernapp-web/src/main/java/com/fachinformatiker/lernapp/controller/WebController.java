package com.fachinformatiker.lernapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Haupt-Controller für alle Web-Seiten Routen
 * SAUBER ORGANISIERT - Januar 2025
 */
@Controller
public class WebController {

    // ========== AUTH SEITEN ==========
    
    @GetMapping("/login")
    public String loginPage(Model model) {
        // Neue saubere Login-Seite ohne Auto-Redirect
        return "auth/clean-login";
    }
    
    @GetMapping("/login-old")
    public String oldLoginPage(Model model) {
        // Alte problematische Login-Seite (für Referenz)
        return "auth/login";
    }
    
    @GetMapping("/register")
    public String registerPage(Model model) {
        return "auth/register";
    }
    
    // ========== DASHBOARD SEITEN ==========
    
    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // Haupt-Dashboard
        return "dashboard/index";
    }
    
    @GetMapping("/static-dashboard")
    public String staticDashboard(Model model) {
        // Fallback Dashboard ohne Auth-Checks
        return "static-dashboard";
    }
    
    // ========== LERN-BEREICHE ==========
    
    @GetMapping("/learn")
    public String learn(Model model) {
        return "learning/index";
    }
    
    @GetMapping("/practice")
    public String practice(Model model) {
        // Verwende practice/index.html statt practice.html
        return "practice/index";
    }
    
    @GetMapping("/exams")
    public String exams(Model model) {
        // Verwende exams/index.html statt exam.html
        return "exams/index";
    }
    
    @GetMapping("/exam")
    public String exam(Model model) {
        // Redirect zu /exams für Konsistenz
        return "redirect:/exams";
    }
    
    @GetMapping("/progress")
    public String progress(Model model) {
        return "progress/index";
    }
    
    // ========== USER BEREICHE ==========
    
    @GetMapping("/profile")
    public String profile(Model model, @AuthenticationPrincipal UserDetails userDetails) {
        if (userDetails != null) {
            model.addAttribute("username", userDetails.getUsername());
        }
        return "profile/index";
    }
    
    @GetMapping("/settings")
    public String settings(Model model) {
        return "settings/index";
    }
    
    // ========== DEBUG/TEST SEITEN ==========
    
    @GetMapping("/api-test")
    public String apiTest(Model model) {
        return "api-test";
    }
    
    @GetMapping("/quick-login")
    public String quickLogin(Model model) {
        return "quick-login";
    }
    
    @GetMapping("/admin/questions")
    public String questionManager(Model model) {
        return "admin/question-manager";
    }
    
    // ========== ALTE/LEGACY ROUTEN (später entfernen) ==========
    
    @GetMapping("/simple-login")
    public String simpleLoginPage(Model model) {
        // Redirect zu neuer Login-Seite
        return "redirect:/login";
    }
    
    @GetMapping("/dashboard-spa")
    public String dashboardSpa(Model model) {
        // Gleiche wie normales Dashboard
        return "redirect:/dashboard";
    }
    
    @GetMapping("/dashboard-main")
    public String dashboardMain(Model model) {
        // Alt - redirect zu normalem Dashboard
        return "redirect:/dashboard";
    }
}
