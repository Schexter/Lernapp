package com.fachinformatiker.lernapp.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CustomErrorController implements ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            
            if(statusCode == HttpStatus.NOT_FOUND.value()) {
                model.addAttribute("status", "404");
                model.addAttribute("error", "Seite nicht gefunden");
                model.addAttribute("message", "Die angeforderte Seite konnte nicht gefunden werden.");
            }
            else if(statusCode == HttpStatus.UNAUTHORIZED.value()) {
                model.addAttribute("status", "401");
                model.addAttribute("error", "Nicht autorisiert");
                model.addAttribute("message", "Bitte melden Sie sich an, um auf diese Seite zuzugreifen.");
                // Redirect to login for unauthorized access
                return "redirect:/login";
            }
            else if(statusCode == HttpStatus.FORBIDDEN.value()) {
                model.addAttribute("status", "403");
                model.addAttribute("error", "Zugriff verweigert");
                model.addAttribute("message", "Sie haben keine Berechtigung, auf diese Seite zuzugreifen.");
            }
            else if(statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                model.addAttribute("status", "500");
                model.addAttribute("error", "Interner Serverfehler");
                model.addAttribute("message", "Ein unerwarteter Fehler ist aufgetreten.");
            }
            else {
                model.addAttribute("status", statusCode);
                model.addAttribute("error", "Fehler");
                model.addAttribute("message", "Ein Fehler ist aufgetreten.");
            }
        }
        
        return "error";
    }
}