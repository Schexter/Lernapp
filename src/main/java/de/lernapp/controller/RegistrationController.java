package de.lernapp.controller;

import de.lernapp.dto.RegisterRequest;
import de.lernapp.model.User;
import de.lernapp.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import java.util.Optional;

/**
 * Web Controller für Registrierungs-Seiten (Thymeleaf Templates)
 * Erstellt von Hans Hahn - Alle Rechte vorbehalten
 */
@Controller
@RequiredArgsConstructor
public class RegistrationController {
    
    private static final Logger logger = LoggerFactory.getLogger(RegistrationController.class);
    
    private final UserService userService;
    
    /**
     * Zeigt die Registrierungs-Seite an
     */
    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        logger.debug("Registrierungs-Seite wird angezeigt");
        
        model.addAttribute("registration", new RegisterRequest());
        return "register";
    }
    
    /**
     * Verarbeitet die Registrierungs-Form
     */
    @PostMapping("/register")
    public String processRegistration(
            @Valid @ModelAttribute("registration") RegisterRequest registration,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        
        logger.info("Registrierungs-Formular eingereicht für: {}", registration.getUsername());
        
        // Prüfe Validierungsfehler
        if (bindingResult.hasErrors()) {
            logger.warn("Validierungsfehler bei Registrierung: {}", bindingResult.getAllErrors());
            model.addAttribute("error", "Bitte überprüfen Sie Ihre Eingaben.");
            return "register";
        }
        
        try {
            // Prüfe ob Username bereits existiert
            Optional<User> existingUser = userService.findByUsername(registration.getUsername());
            if (existingUser.isPresent()) {
                logger.warn("Registrierung fehlgeschlagen - Username existiert bereits: {}", 
                    registration.getUsername());
                model.addAttribute("error", "Benutzername ist bereits vergeben.");
                return "register";
            }
            
            // Prüfe ob Email bereits existiert
            Optional<User> existingEmail = userService.findByEmail(registration.getEmail());
            if (existingEmail.isPresent()) {
                logger.warn("Registrierung fehlgeschlagen - Email existiert bereits: {}", 
                    registration.getEmail());
                model.addAttribute("error", "E-Mail-Adresse ist bereits registriert.");
                return "register";
            }
            
            // Prüfe Passwort-Bestätigung
            if (!registration.getPassword().equals(registration.getConfirmPassword())) {
                logger.warn("Registrierung fehlgeschlagen - Passwörter stimmen nicht überein für: {}", 
                    registration.getUsername());
                model.addAttribute("error", "Passwörter stimmen nicht überein.");
                return "register";
            }
            
            // Registriere neuen User mit erweiterten Informationen
            User newUser = userService.registerUserExtended(
                registration.getUsername(),
                registration.getEmail(),
                registration.getPassword(),
                registration.getFirstName(),
                registration.getLastName(),
                registration.getAusbildungsrichtung(),
                registration.getAusbildungsjahr(),
                registration.getBerufsschule()
            );
            
            logger.info("✅ Registrierung erfolgreich für User: {} (ID: {})", 
                newUser.getUsername(), newUser.getId());
            
            // Erfolgreiche Registrierung - redirect zum Login
            redirectAttributes.addFlashAttribute("success", 
                "Registrierung erfolgreich! Sie können sich jetzt anmelden.");
            
            return "redirect:/login";
            
        } catch (Exception e) {
            logger.error("❌ Registrierungs-Fehler für User {}: ", registration.getUsername(), e);
            model.addAttribute("error", "Ein Fehler ist aufgetreten. Bitte versuchen Sie es später erneut.");
            return "register";
        }
    }
    
    /**
     * AJAX Endpoint für Username-Verfügbarkeit
     */
    @GetMapping("/api/check-username")
    @ResponseBody
    public String checkUsernameAvailability(@RequestParam String username) {
        logger.debug("Prüfe Username-Verfügbarkeit für: {}", username);
        
        try {
            if (username.length() < 3) {
                return "invalid"; // Zu kurz
            }
            
            Optional<User> existingUser = userService.findByUsername(username);
            return existingUser.isPresent() ? "taken" : "available";
            
        } catch (Exception e) {
            logger.error("Fehler bei Username-Prüfung: ", e);
            return "error";
        }
    }
    
    /**
     * AJAX Endpoint für Email-Verfügbarkeit
     */
    @GetMapping("/api/check-email")
    @ResponseBody
    public String checkEmailAvailability(@RequestParam String email) {
        logger.debug("Prüfe Email-Verfügbarkeit für: {}", email);
        
        try {
            if (!email.contains("@") || !email.contains(".")) {
                return "invalid"; // Ungültiges Format
            }
            
            Optional<User> existingUser = userService.findByEmail(email);
            return existingUser.isPresent() ? "taken" : "available";
            
        } catch (Exception e) {
            logger.error("Fehler bei Email-Prüfung: ", e);
            return "error";
        }
    }
}
